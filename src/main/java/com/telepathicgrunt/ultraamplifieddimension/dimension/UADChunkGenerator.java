package com.telepathicgrunt.ultraamplifieddimension.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.StructureAccessorAccessor;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.InterpolatedNoiseSamplerAccessor;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.ChunkGeneratorAccessor;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.ChunkGeneratorSettingsInvoker;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.NoiseChunkGeneratorAccessor;
import com.telepathicgrunt.ultraamplifieddimension.utils.WorldSeedHolder;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;


public class UADChunkGenerator extends NoiseChunkGenerator {

    private static final float[] BIOME_WEIGHTING_KERNEL = Util.make(new float[25], (floats) -> {
        for (int xRelative = -2; xRelative <= 2; ++xRelative) {
            for (int zRelative = -2; zRelative <= 2; ++zRelative) {
                float biomeWeighting = 10.0F / MathHelper.sqrt(xRelative * xRelative + zRelative * zRelative + 0.2F);
                floats[xRelative + 2 + (zRelative + 2) * 5] = biomeWeighting;
            }
        }
    });

    private static final float[] TERRAFORMING_NOISE_KERNAL = Util.make(new float[13824], (floats) -> {
        for(int x = 0; x < 24; ++x) {
            for(int z = 0; z < 24; ++z) {
                for(int y = 0; y < 24; ++y) {
                    floats[x * 24 * 24 + z * 24 + y] = (float) getTerrainValue(z - 12, y - 12, x - 12);
                }
            }
        }
    });

    private static final float[] GIANT_TERRAFORMING_NOISE_KERNAL = Util.make(new float[13824], (floats) -> {
        for(int x = 0; x < 24; ++x) {
            for(int z = 0; z < 24; ++z) {
                for(int y = 0; y < 24; ++y) {
                    floats[x * 24 * 24 + z * 24 + y] = (float) getGiantTerrainValue(z - 12, y - 12, x - 12);
                }
            }
        }
    });

    public static final Codec<NoiseSamplingConfig> UAD_SCALING_CODEC = RecordCodecBuilder.create((scalingSettingsInstance) ->
            scalingSettingsInstance.group(
                    Codec.DOUBLE.fieldOf("xz_scale").forGetter(NoiseSamplingConfig::getXZScale),
                    Codec.DOUBLE.fieldOf("y_scale").forGetter(NoiseSamplingConfig::getYScale),
                    Codec.DOUBLE.fieldOf("xz_factor").forGetter(NoiseSamplingConfig::getXZFactor),
                    Codec.DOUBLE.fieldOf("y_factor").forGetter(NoiseSamplingConfig::getYFactor))
                        .apply(scalingSettingsInstance, NoiseSamplingConfig::new));

    public static final Codec<GenerationShapeConfig> UAD_NOISE_SETTINGS_CODEC = RecordCodecBuilder.create((noiseSettingsInstance) ->
            noiseSettingsInstance.group(
                    Codec.INT.fieldOf("min_y").forGetter(GenerationShapeConfig::getMinimumY),
                    Codec.INT.fieldOf("height").forGetter(GenerationShapeConfig::getHeight),
                    UAD_SCALING_CODEC.fieldOf("sampling").forGetter(GenerationShapeConfig::getSampling),
                    SlideConfig.CODEC.fieldOf("top_slide").forGetter(GenerationShapeConfig::getTopSlide),
                    SlideConfig.CODEC.fieldOf("bottom_slide").forGetter(GenerationShapeConfig::getBottomSlide),
                    Codec.INT.fieldOf("size_horizontal").forGetter(GenerationShapeConfig::getSizeHorizontal),
                    Codec.INT.fieldOf("size_vertical").forGetter(GenerationShapeConfig::getSizeVertical),
                    Codec.DOUBLE.fieldOf("density_factor").forGetter(GenerationShapeConfig::getDensityFactor),
                    Codec.DOUBLE.fieldOf("density_offset").forGetter(GenerationShapeConfig::getDensityOffset),
                    Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(GenerationShapeConfig::hasSimplexSurfaceNoise),
                    Codec.BOOL.optionalFieldOf("random_density_offset", Boolean.FALSE, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasRandomDensityOffset),
                    Codec.BOOL.optionalFieldOf("island_noise_override", Boolean.FALSE, Lifecycle.experimental()).forGetter(GenerationShapeConfig::hasIslandNoiseOverride),
                    Codec.BOOL.optionalFieldOf("amplified", Boolean.FALSE, Lifecycle.experimental()).forGetter(GenerationShapeConfig::isAmplified))
                        .apply(noiseSettingsInstance, GenerationShapeConfig::new));

    public static final Codec<ChunkGeneratorSettings> UAD_DIMENSION_SETTINGS_CODEC = RecordCodecBuilder.create((dimensionSettingsInstance) ->
            dimensionSettingsInstance.group(
                    StructuresConfig.CODEC.fieldOf("structures").forGetter(ChunkGeneratorSettings::getStructuresConfig),
                    UAD_NOISE_SETTINGS_CODEC.fieldOf("noise").forGetter(ChunkGeneratorSettings::getGenerationShapeConfig),
                    BlockState.CODEC.fieldOf("default_block").forGetter(ChunkGeneratorSettings::getDefaultBlock),
                    BlockState.CODEC.fieldOf("default_fluid").forGetter(ChunkGeneratorSettings::getDefaultFluid),
                    Codec.INT.fieldOf("bedrock_roof_position").forGetter(ChunkGeneratorSettings::getBedrockCeilingY),
                    Codec.INT.fieldOf("bedrock_floor_position").forGetter(ChunkGeneratorSettings::getBedrockFloorY),
                    Codec.INT.fieldOf("sea_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
                    Codec.INT.fieldOf("min_surface_level").forGetter(ChunkGeneratorSettings::getSeaLevel),
                    Codec.BOOL.fieldOf("disable_mob_generation").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callIsMobGenerationDisabled()),
                    Codec.BOOL.fieldOf("aquifers_enabled").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callHasAquifers()),
                    Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callHasNoiseCaves()),
                    Codec.BOOL.fieldOf("deepslate_enabled").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callHasDeepslate()),
                    Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callHasOreVeins()),
                    Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(dimensionSettings -> ((ChunkGeneratorSettingsInvoker)(Object)dimensionSettings).uad_callHasNoodleCaves()))
                        .apply(dimensionSettingsInstance, ChunkGeneratorSettings::new));


    public static final Codec<NoiseChunkGenerator> UAD_CHUNK_GENERATOR_CODEC = RecordCodecBuilder.create((noiseChunkGeneratorInstance) -> noiseChunkGeneratorInstance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((noiseChunkGenerator) -> ((ChunkGeneratorAccessor)noiseChunkGenerator).uad_getbiomeProvider()),
                    Codec.LONG.fieldOf("seed").orElseGet(WorldSeedHolder::getSeed).forGetter((noiseChunkGenerator) -> ((NoiseChunkGeneratorAccessor)noiseChunkGenerator).uad_getSeed()),
                    UAD_DIMENSION_SETTINGS_CODEC.fieldOf("settings").forGetter((noiseChunkGenerator) -> ((NoiseChunkGeneratorAccessor)noiseChunkGenerator).uad_getSettings().get()))
                        .apply(noiseChunkGeneratorInstance, noiseChunkGeneratorInstance.stable(UADChunkGenerator::new)));

    // Cache the sealevel
    private final int sealevel;
    protected final long seed;
    protected List<StructureFeature<?>> landTerraformingStructures;
    private final InterpolatedNoiseSampler interpolatedNoiseSampler;
    private static final BlockState[] EMPTY = new BlockState[0];

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return UAD_CHUNK_GENERATOR_CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new UADChunkGenerator(this.populationSource.withSeed(seed), seed, this.settings.get());
    }

    public UADChunkGenerator(BiomeSource biomeProvider, long seed, ChunkGeneratorSettings dimensionSettings) {
        super(biomeProvider, seed, () -> dimensionSettings);
        this.seed = seed;

        sealevel = this.settings.get().getSeaLevel();
        landTerraformingStructures = new ArrayList<>(StructureFeature.LAND_MODIFYING_STRUCTURES);
        landTerraformingStructures.add(StructureFeature.MONUMENT);

        ChunkRandom chunkRandom = new ChunkRandom(seed);
        interpolatedNoiseSampler = new InterpolatedNoiseSampler(chunkRandom);
    }

    private void sampleNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
        GenerationShapeConfig noisesettings = this.settings.get().getGenerationShapeConfig();
        double d0;
        double d1;
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;

        for(int k = -2; k <= 2; ++k) {
            for(int l = -2; l <= 2; ++l) {
               // Biome biome = this.biomeProvider.getNoiseBiome(noiseX + k, j, noiseZ + l);
                float depthWeight = 0; // biome.getDepth();
                float scaleWeight = 0; // biome.getScale();

                // Does not take into account the biome's base height and scale.
                // Making the terrain's height based on biomes took away some of the magic and coolness.
                // Thus, all biomes now have a uniformed base and scale applied to their terrain.
                // offset + scale
                depthWeight = 1.0F + (0.0F + 0.4F) * 2F;
                scaleWeight = 1.0F + (0.0F + 0.3F) * 12F;

                float f9 = BIOME_WEIGHTING_KERNEL[k + 2 + (l + 2) * 5] / (depthWeight + 2.0F);
                f += scaleWeight * f9;
                f1 += depthWeight * f9;
                f2 += f9;
            }
        }

        float f10 = f1 / f2;
        float f11 = f / f2;
        double d16 = f10 * 0.5F - 0.125F;
        double d18 = f11 * 0.9F + 0.1F;
        d0 = d16 * 0.265625D;
        d1 = 96.0D / d18;

        double d12 = 684.412D * noisesettings.getSampling().getXZScale();
        double d13 = 684.412D * noisesettings.getSampling().getYScale();
        double d14 = d12 / noisesettings.getSampling().getXZFactor();
        double d15 = d13 / noisesettings.getSampling().getYFactor();
        double d17 = noisesettings.getTopSlide().getTarget();
        double d19 = noisesettings.getTopSlide().getSize();
        double d20 = noisesettings.getTopSlide().getOffset();
        double d21 = noisesettings.getBottomSlide().getTarget();
        double d2 = noisesettings.getBottomSlide().getSize();
        double d3 = noisesettings.getBottomSlide().getOffset();
        double d4 = noisesettings.hasRandomDensityOffset() ? this.getRandomDensityAt(noiseX, noiseZ) : 0.0D;
        double d5 = noisesettings.getDensityFactor();
        double d6 = noisesettings.getDensityOffset();

        for(int i1 = 0; i1 <= ((NoiseChunkGeneratorAccessor)this).uad_getNoiseSizeY(); ++i1) {
            double d7 = this.sampleNoise(noiseX, i1, noiseZ, d12, d13, d14, d15);
            double d8 = 1.0D - (double)i1 * 2.0D / (double)((NoiseChunkGeneratorAccessor)this).uad_getNoiseSizeY() + d4;
            double d9 = d8 * d5 + d6;
            double d10 = (d9 + d0) * d1;
            if (d10 > 0.0D) {
                d7 = d7 + d10 * 4.0D;
            } else {
                d7 = d7 + d10;
            }

            if (d19 > 0.0D) {
                double d11 = ((double)(((NoiseChunkGeneratorAccessor)this).uad_getNoiseSizeY() - i1) - d20) / d19;
                d7 = MathHelper.clampedLerp(d17, d7, d11);
            }

            if (d2 > 0.0D) {
                double d22 = ((double)i1 - d3) / d2;
                d7 = MathHelper.clampedLerp(d21, d7, d22);
            }

            noiseColumn[i1] = d7;
        }
    }


    /**
     * SuperCoder's optimization she PRed into Lithium.
     * https://github.com/jellysquid3/lithium-fabric/blob/96c4347f2feeeb7310906760566ea2f1ed02e2cd/src/main/java/me/jellysquid/mods/lithium/mixin/gen/fast_noise_interpolation/NoiseChunkGeneratorMixin.java
     *
     * I asked if I could recreate it here and was given the green light.
     * Special thanks to SuperCoder!
     *
     * Note from Lithium:
     *  To generate it's terrain, Minecraft uses two different perlin noises.
     *  It interpolates these two noises to create the final sample at a position.
     *  However, the interpolation noise is not all that good and spends most of it's time at > 1 or < 0, rendering
     *  one of the noises completely unnecessary in the process.
     *  By taking advantage of that, we can reduce the sampling needed per block through the interpolation noise.
     */
    private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        // This controls both the frequency and amplitude of the noise.
        double frequency = 1.0;
        double interpolationValue = 0.0;

        // Calculate interpolation data to decide what noise to sample.
        for (int octave = 0; octave < 8; octave++) {
            double scaledVerticalScale = verticalStretch * frequency;
            double scaledY = y * scaledVerticalScale;

            interpolationValue += sampleOctave(((InterpolatedNoiseSamplerAccessor)interpolatedNoiseSampler).uad_getInterpolationNoise().getOctave(octave),
                    OctavePerlinNoiseSampler.maintainPrecision(x * horizontalStretch * frequency),
                    OctavePerlinNoiseSampler.maintainPrecision(scaledY),
                    OctavePerlinNoiseSampler.maintainPrecision(z * horizontalStretch * frequency), scaledVerticalScale, scaledY, frequency);

            frequency /= 2.0;
        }

        double clampedInterpolation = (interpolationValue / 10.0 + 1.0) / 2.0;

        if (clampedInterpolation >= 1) {
            // Sample only upper noise, as the lower noise will be interpolated out.
            frequency = 1.0;
            double noise = 0.0;
            for (int octave = 0; octave < 16; octave++) {
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;

                noise += sampleOctave(((InterpolatedNoiseSamplerAccessor)interpolatedNoiseSampler).uad_getUpperInterpolatedNoise().getOctave(octave),
                        OctavePerlinNoiseSampler.maintainPrecision(x * horizontalScale * frequency),
                        OctavePerlinNoiseSampler.maintainPrecision(scaledY),
                        OctavePerlinNoiseSampler.maintainPrecision(z * horizontalScale * frequency), scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            return noise / 512.0;
        }
        else if (clampedInterpolation <= 0) {
            // Sample only lower noise, as the upper noise will be interpolated out.
            frequency = 1.0;
            double noise = 0.0;
            for (int octave = 0; octave < 16; octave++) {
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;
                noise += sampleOctave(((InterpolatedNoiseSamplerAccessor)interpolatedNoiseSampler).uad_getLowerInterpolatedNoise().getOctave(octave),
                        OctavePerlinNoiseSampler.maintainPrecision(x * horizontalScale * frequency),
                        OctavePerlinNoiseSampler.maintainPrecision(scaledY),
                        OctavePerlinNoiseSampler.maintainPrecision(z * horizontalScale * frequency), scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            return noise / 512.0;
        }
        else {
            // [VanillaCopy] SurfaceChunkGenerator#sampleNoise
            // Sample both and interpolate, as in vanilla.

            frequency = 1.0;
            double lowerNoise = 0.0;
            double upperNoise = 0.0;

            for (int octave = 0; octave < 16; octave++) {
                // Pre calculate these values to share them
                double scaledVerticalScale = verticalScale * frequency;
                double scaledY = y * scaledVerticalScale;
                double xVal = OctavePerlinNoiseSampler.maintainPrecision(x * horizontalScale * frequency);
                double yVal = OctavePerlinNoiseSampler.maintainPrecision(scaledY);
                double zVal = OctavePerlinNoiseSampler.maintainPrecision(z * horizontalScale * frequency);

                upperNoise += sampleOctave(((InterpolatedNoiseSamplerAccessor)interpolatedNoiseSampler).uad_getUpperInterpolatedNoise().getOctave(octave), xVal, yVal, zVal, scaledVerticalScale, scaledY, frequency);
                lowerNoise += sampleOctave(((InterpolatedNoiseSamplerAccessor)interpolatedNoiseSampler).uad_getLowerInterpolatedNoise().getOctave(octave), xVal, yVal, zVal, scaledVerticalScale, scaledY, frequency);

                frequency /= 2.0;
            }

            // Vanilla behavior, return interpolated noise
            return MathHelper.lerp(clampedInterpolation, lowerNoise / 512.0, upperNoise / 512.0);
        }
    }

    /**
     * Also from SuperCoder and Lithium
     */
    private static double sampleOctave(PerlinNoiseSampler sampler, double x, double y, double z, double scaledVerticalScale, double scaledY, double frequency) {
        return sampler.sample(x, y, z, scaledVerticalScale, scaledY) / frequency;
    }


    private double[] sampleNoiseColumn(int p_222547_1_, int p_222547_2_) {
        double[] adouble = new double[((NoiseChunkGeneratorAccessor)this).uad_getNoiseSizeY() + 1];
        this.sampleNoiseColumn(adouble, p_222547_1_, p_222547_2_);
        return adouble;
    }

    private double getRandomDensityAt(int x, int z) {
        double d0 = ((NoiseChunkGeneratorAccessor)this).uad_getEdgeDensityNoise().sample(
                x * 200,
                10.0D,
                z * 200);

        if (d0 < 0.0D) {
            d0 *= 3.0D;
        }

        double d2 = d0 * 24.575625D - 2.0D;
        return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), world.getTopY());
        int k = MathHelper.floorDiv(i, ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution());
        int l = MathHelper.floorDiv(j - i, ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution());
        return l <= 0 ? world.getBottomY() : this.sampleHeightmap(x, z, null, heightmap.getBlockPredicate(), k, l).orElse(world.getBottomY());
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        int i = Math.max(this.settings.get().getGenerationShapeConfig().getMinimumY(), world.getBottomY());
        int j = Math.min(this.settings.get().getGenerationShapeConfig().getMinimumY() + this.settings.get().getGenerationShapeConfig().getHeight(), world.getTopY());
        int k = MathHelper.floorDiv(i, ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution());
        int l = MathHelper.floorDiv(j - i, ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution());
        if (l <= 0) {
            return new VerticalBlockSample(i, EMPTY);
        } else {
            BlockState[] blockStates = new BlockState[l * ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution()];
            this.sampleHeightmap(x, z, blockStates, null, k, l);
            return new VerticalBlockSample(i, blockStates);
        }
    }

    private OptionalInt sampleHeightmap(int x, int z, BlockState[] p_236087_3_, Predicate<BlockState> p_236087_4_, int minY, int noiseSizeY) {
        int i = Math.floorDiv(x, ((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution());
        int j = Math.floorDiv(z, ((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution());
        int k = Math.floorMod(x, ((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution());
        int l = Math.floorMod(z, ((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution());
        double d0 = (double)k / (double)((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution();
        double d1 = (double)l / (double)((NoiseChunkGeneratorAccessor)this).uad_getHorizontalNoiseResolution();
        double[][] adouble = new double[][]{this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)};

        Biome biome = getCachedBiome(null, new BlockPos(x, 0, z));
        for(int ySection = ((NoiseChunkGeneratorAccessor)this).uad_getNoiseSizeY() - 1; ySection >= 0; --ySection) {
            double d2 = adouble[0][ySection];
            double d3 = adouble[1][ySection];
            double d4 = adouble[2][ySection];
            double d5 = adouble[3][ySection];
            double d6 = adouble[0][ySection + 1];
            double d7 = adouble[1][ySection + 1];
            double d8 = adouble[2][ySection + 1];
            double d9 = adouble[3][ySection + 1];

            for(int j1 = ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution() - 1; j1 >= 0; --j1) {
                double d10 = (double)j1 / (double)((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution();
                double noiseValue = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
                int y = ySection * ((NoiseChunkGeneratorAccessor)this).uad_getVerticalNoiseResolution() + j1;
                BlockState blockstate = this.getTerrainBlock(null, noiseValue, biome, x, y, z);
                if (p_236087_3_ != null) {
                    p_236087_3_[y] = blockstate;
                }

                if (p_236087_4_ != null && p_236087_4_.test(blockstate)) {
                    return OptionalInt.of(y + 1);
                }
            }
        }

        return OptionalInt.of(0);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor structureAccessor, Chunk chunk) {
        ObjectList<StructurePiece> objectlist = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
        ChunkPos chunkpos = chunk.getPos();
        int xChunkPos = chunkpos.x;
        int zChunkPos = chunkpos.z;
        int xPos = xChunkPos << 4;
        int zPos = zChunkPos << 4;


        for (StructureFeature<?> structure : this.landTerraformingStructures) {
            structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(chunkpos, 0), structure).forEach((p_236089_5_) -> {
                for (StructurePiece structurepiece1 : p_236089_5_.getChildren()) {
                    if (structurepiece1.intersectsChunk(chunkpos, 12)) {
                        if (structurepiece1 instanceof PoolStructurePiece abstractvillagepiece) {
                            StructurePool.Projection jigsawpattern$placementbehaviour = abstractvillagepiece.getPoolElement().getProjection();
                            if (jigsawpattern$placementbehaviour == StructurePool.Projection.RIGID) {
                                objectlist.add(abstractvillagepiece);
                            }

                            for (JigsawJunction jigsawjunction1 : abstractvillagepiece.getJunctions()) {
                                int xSource = jigsawjunction1.getSourceX();
                                int zSource = jigsawjunction1.getSourceZ();
                                if (xSource > xPos - 12 && zSource > zPos - 12 && xSource < xPos + 15 + 12 && zSource < zPos + 15 + 12) {
                                    objectlist1.add(jigsawjunction1);
                                }
                            }
                        } else {
                            objectlist.add(structurepiece1);
                        }
                    }
                }

            });
        }

        double[][][] adouble = new double[2][((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ() + 1][((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeY() + 1];

        for (int i5 = 0; i5 < ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ() + 1; ++i5) {
            adouble[0][i5] = new double[((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeY() + 1];
            this.sampleNoiseColumn(adouble[0][i5], xChunkPos * ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeX(), zChunkPos * ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ() + i5);
            adouble[1][i5] = new double[((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeY() + 1];
        }

        ProtoChunk chunkprimer = (ProtoChunk) chunk;
        WorldAccess world = ((StructureAccessorAccessor)structureAccessor).uad_getWorld();
        Heightmap heightmap = chunkprimer.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap1 = chunkprimer.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> objectlistiterator = objectlist.iterator();
        ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

        for (int xNoiseSize = 0; xNoiseSize < ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeX(); ++xNoiseSize) {
            for (int zNoiseSize = 0; zNoiseSize < ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ() + 1; ++zNoiseSize) {
                this.sampleNoiseColumn(adouble[1][zNoiseSize], xChunkPos * ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeX() + xNoiseSize + 1, zChunkPos * ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ() + zNoiseSize);
            }

            for (int j5 = 0; j5 < ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeZ(); ++j5) {
                ChunkSection chunksection = chunkprimer.getSection(15);
                chunksection.lock();

                for (int k1 = ((NoiseChunkGeneratorAccessor) this).uad_getNoiseSizeY() - 1; k1 >= 0; --k1) {
                    double d0 = adouble[0][j5][k1];
                    double d1 = adouble[0][j5 + 1][k1];
                    double d2 = adouble[1][j5][k1];
                    double d3 = adouble[1][j5 + 1][k1];
                    double d4 = adouble[0][j5][k1 + 1];
                    double d5 = adouble[0][j5 + 1][k1 + 1];
                    double d6 = adouble[1][j5][k1 + 1];
                    double d7 = adouble[1][j5 + 1][k1 + 1];

                    for (int xSection = 0; xSection < ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution(); ++xSection) {
                        int x = xPos + xNoiseSize * ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution() + xSection;
                        int xInChunk = x & 15;
                        double d13 = (double) xSection / (double) ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution();

                        for (int zSection = 0; zSection < ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution(); ++zSection) {
                            int z = zPos + j5 * ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution() + zSection;
                            int zInChunk = z & 15;
                            double d16 = (double) zSection / (double) ((NoiseChunkGeneratorAccessor) this).uad_getHorizontalNoiseResolution();

                            // Do it here instead of in getTerrainBlock as the biome is the same for the entire y height.
                            Biome biome = getCachedBiome(world, new BlockPos(x, 0, z));

                            for (int ySection = ((NoiseChunkGeneratorAccessor) this).uad_getVerticalNoiseResolution() - 1; ySection >= 0; --ySection) {
                                int y = k1 * ((NoiseChunkGeneratorAccessor) this).uad_getVerticalNoiseResolution() + ySection;
                                int yInChunk = y & 15;
                                int yChunk = y >> 4;
                                if (chunksection.getYOffset() >> 4 != yChunk) {
                                    chunksection.unlock();
                                    chunksection = chunkprimer.getSection(yChunk);
                                    chunksection.lock();
                                }

                                double d8 = (double) ySection / (double) ((NoiseChunkGeneratorAccessor) this).uad_getVerticalNoiseResolution();
                                double d9 = MathHelper.lerp(d8, d0, d4);
                                double d10 = MathHelper.lerp(d8, d2, d6);
                                double d11 = MathHelper.lerp(d8, d1, d5);
                                double d12 = MathHelper.lerp(d8, d3, d7);
                                double d14 = MathHelper.lerp(d13, d9, d10);
                                double d15 = MathHelper.lerp(d13, d11, d12);
                                double d17 = MathHelper.lerp(d16, d14, d15);
                                double noiseValue = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

                                int pieceX;
                                int pieceY;
                                int pieceZ;
                                for (noiseValue = noiseValue / 2.0D - noiseValue * noiseValue * noiseValue / 24.0D;
                                     objectlistiterator.hasNext(); ) {
                                    StructurePiece structurepiece = objectlistiterator.next();
                                    BlockBox mutableboundingbox = structurepiece.getBoundingBox();
                                    pieceX = Math.max(0, Math.max(mutableboundingbox.getMinX() - x, x - mutableboundingbox.getMaxX()));
                                    pieceY = y - (mutableboundingbox.getMinY() + (structurepiece instanceof PoolStructurePiece ? ((PoolStructurePiece) structurepiece).getGroundLevelDelta() : 0));
                                    pieceZ = Math.max(0, Math.max(mutableboundingbox.getMinZ() - z, z - mutableboundingbox.getMaxZ()));

                                    if (structurepiece instanceof OceanMonumentGenerator.Base) {
                                        pieceY -= 2;
                                        noiseValue += giantTerraformNoise(pieceX, pieceY, pieceZ) * 0.8D;
                                    } else {
                                        noiseValue += terraformNoise(pieceX, pieceY, pieceZ) * 0.8D;
                                    }
                                }

                                objectlistiterator.back(objectlist.size());

                                while (objectlistiterator1.hasNext()) {
                                    JigsawJunction jigsawjunction = objectlistiterator1.next();
                                    pieceX = z - jigsawjunction.getSourceX();
                                    pieceY = y - jigsawjunction.getSourceGroundY();
                                    pieceZ = z - jigsawjunction.getSourceZ();
                                    noiseValue += terraformNoise(pieceX, pieceY, pieceZ) * 0.4D;
                                }

                                objectlistiterator1.back(objectlist1.size());
                                BlockState blockstate = this.getTerrainBlock(world, noiseValue, biome, x, y, z);
                                if (blockstate != Blocks.AIR.getDefaultState()) {
                                    blockpos$mutable.set(x, y, z);
                                    if (blockstate.getLuminance() != 0) {
                                        chunkprimer.addLightSource(blockpos$mutable);
                                    }

                                    chunksection.setBlockState(xInChunk, yInChunk, zInChunk, blockstate, false);
                                    heightmap.trackUpdate(xInChunk, y, zInChunk, blockstate);
                                    heightmap1.trackUpdate(xInChunk, y, zInChunk, blockstate);
                                }
                            }
                        }
                    }
                }

                chunksection.unlock();
            }

            double[][] adouble1 = adouble[0];
            adouble[0] = adouble[1];
            adouble[1] = adouble1;
        }

        return CompletableFuture.completedFuture(chunkprimer);
    }

    /**
     * This is used to select the main block for land and sea.
     * We added biome category checks to help make nether and end biomes
     * as close to their actual dimensions as possible for best compat.
     */
    protected BlockState getTerrainBlock(WorldAccess world, double noiseValue, Biome biome, int x, int y, int z) {
        BlockState blockstate;
        if (noiseValue > 0.0D) {
            blockstate = this.defaultBlock;

            // Change blockstate for end and nether biomes so that their features and carvers behave correctly
            if(biome.getCategory() == Biome.Category.NETHER){
                blockstate = Blocks.NETHERRACK.getDefaultState();
            }
            else if(biome.getCategory() == Biome.Category.THEEND){
                blockstate = Blocks.END_STONE.getDefaultState();
            }
        }
        else if (y < this.getSeaLevel()) {
            if(biome.getCategory() == Biome.Category.NETHER){
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                // If nether biome is surrounded by nether biomes, place lava.
                // This way, all imported nether biomes gets the lava they want.
                if(isSurroundedByNether(world, mutable, x, z)) {
                    if(y > this.getSeaLevel() - 7){
                        blockstate = this.defaultFluid;
                    }
                    else if(y == this.getSeaLevel() - 7){
                        blockstate = Blocks.MAGMA_BLOCK.getDefaultState();
                    }
                    else{
                        blockstate = Blocks.LAVA.getDefaultState();
                    }
                }
                // Make an obsidian border to separate lava from default fluid.
                else if(y <= this.getSeaLevel() - 6){
                    blockstate = Blocks.OBSIDIAN.getDefaultState();
                }
                else{
                    blockstate = this.defaultFluid;
                }
            }
            else{
                // default world fluid
                blockstate = this.defaultFluid;
            }
        } else {
            blockstate = Blocks.AIR.getDefaultState();
        }

        return blockstate;
    }


    private boolean isSurroundedByNether(WorldAccess world, BlockPos.Mutable mutable, int x, int z){
        for(int xOffset = -2; xOffset <= 2; xOffset++){
            for(int zOffset = -2; zOffset <= 2; zOffset++){
                if(Math.abs(xOffset * zOffset) == 2) {
                    if(getCachedBiome(world, mutable.set(x + xOffset, 0, z + zOffset)).getCategory() != Biome.Category.NETHER){
                        return false;
                    }
                }
            }
        }
        return true;

    }

    /**
     * Calls up the biome's surfacebuilders.
     * We added biome category checks to pass in netherrack and end stone for default blocks
     * to mimic the nether and end dimensions as much as possible for best compat.
     */
    public void buildSurface(ChunkRegion worldGenRegion, Chunk chunk) {
        ChunkPos chunkpos = chunk.getPos();
        int x = chunkpos.x;
        int z = chunkpos.z;
        ChunkRandom sharedseedrandom = new ChunkRandom();
        sharedseedrandom.setTerrainSeed(x, z);
        ChunkPos chunkpos1 = chunk.getPos();
        int xStart = chunkpos1.getStartX();
        int zStart = chunkpos1.getStartZ();
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(int xInChunk = 0; xInChunk < 16; ++xInChunk) {
            for(int zInChunk = 0; zInChunk < 16; ++zInChunk) {
                int xPos = xStart + xInChunk;
                int zPos = zStart + zInChunk;
                int maxY = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, xInChunk, zInChunk) + 1;
                double noise = ((NoiseChunkGeneratorAccessor)this).uad_getSurfaceDepthNoise().sample((double)xPos * 0.0625D, (double)zPos * 0.0625D, 0.0625D, (double)xInChunk * 0.0625D) * 15.0D;
                Biome biome = getCachedBiome(worldGenRegion, blockpos$mutable.set(xPos, 0, zPos));
                BlockState defaultBlockForSurface = Blocks.STONE.getDefaultState();
                if(biome.getCategory() == Biome.Category.NETHER){
                    defaultBlockForSurface = Blocks.NETHERRACK.getDefaultState();
                }
                else if(biome.getCategory() == Biome.Category.THEEND){
                    defaultBlockForSurface = Blocks.END_STONE.getDefaultState();
                }

                int minY = this.settings.get().getMinSurfaceLevel();
                biome.buildSurface(sharedseedrandom, chunk, xPos, zPos, maxY, noise, defaultBlockForSurface, this.defaultFluid, this.getSeaLevel(), minY, worldGenRegion.getSeed());
            }
        }

        ((NoiseChunkGeneratorAccessor)this).uad_callBuildBedrock(chunk, sharedseedrandom);
    }

    private static double terraformNoise(int x, int y, int z) {
        int xPos = x + 12;
        int yPos = y + 12;
        int zPos = z + 12;
        if (xPos >= 0 && xPos < 24) {
            if (yPos >= 0 && yPos < 24) {
                return zPos >= 0 && zPos < 24 ? (double) TERRAFORMING_NOISE_KERNAL[zPos * 24 * 24 + xPos * 24 + yPos] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double giantTerraformNoise(int x, int y, int z) {
        int xPos = x + 12;
        int zPos = z + 12;
        if (xPos >= 0 && xPos < 24) {
            if (y >= 0 && y < 24) {
                return zPos >= 0 && zPos < 24 ? (double) GIANT_TERRAFORMING_NOISE_KERNAL[zPos * 24 * 24 + xPos * 24 + y] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    private static double getTerrainValue(int x, int y, int z) {
        double horizontalDist = (x * x) + (z * z);
        double offsetY = (double)y + 0.5D;
        double squaredOffsetY = offsetY * offsetY;
        double d3 = Math.pow(Math.E, -(squaredOffsetY / 16.0D + horizontalDist / 16.0D));
        double d4 = -offsetY * MathHelper.fastInverseSqrt(squaredOffsetY / 2.0D + horizontalDist / 2.0D) / 2.0D;
        return d4 * d3;
    }

    private static double getGiantTerrainValue(int x, int y, int z) {
        double horizontalDist = (x * x) + (z * z) + 0.0001D;
        double v = (12 - Math.abs(y)) * 0.08D;
        return -((MathHelper.fastInverseSqrt(horizontalDist) * 1.1D) - 1D + v);
    }

    // Use a field to hold sealevel int to make this not as performance heavy as
    // what the parent class does which is runs a supplier every time it is called.
    public int getSeaLevel() {
        return sealevel;
    }

    private static final Long2ReferenceOpenHashMap<Biome> CACHED_BIOMES = new Long2ReferenceOpenHashMap<>();
    public Biome getCachedBiome(WorldAccess world, BlockPos blockpos) {

        // shrink cache if it is too large to clear out old biome refs no longer needed.
        if(CACHED_BIOMES.size() > 200){
            CACHED_BIOMES.clear();
        }

        // gets the biome saved or does the expensive getting of biome if it isn't cached yet.
        long posLong = blockpos.asLong();
        Biome biome = CACHED_BIOMES.get(posLong);
        if(biome == null){
            if(world != null){
                biome = world.getBiome(blockpos);
            }
            else{
                biome = VoronoiBiomeAccessType.INSTANCE.getBiome(this.seed, blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.populationSource);
            }
            CACHED_BIOMES.put(posLong, biome);
        }

        return biome;
    }
}