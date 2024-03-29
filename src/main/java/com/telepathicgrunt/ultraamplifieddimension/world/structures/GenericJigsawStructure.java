package com.telepathicgrunt.ultraamplifieddimension.world.structures;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADStructures;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericJigsawStructure extends AbstractBaseStructure {
    protected final Identifier START_POOL;
    protected final int STRUCTURE_SIZE;
    protected final int PIECE_Y_OFFSET;
    protected final int BOUNDS_Y_OFFSET;
    protected final int FIXED_HEIGHT;
    protected final boolean SPAWN_AT_TOP_LAND;
    protected final int BIOME_RANGE;
    protected Pair<ChunkGenerator, List<Map.Entry<StructureFeature<?>, StructureConfig>>> STRUCTURE_SPACING_CACHE = null;

    public GenericJigsawStructure(Codec<DefaultFeatureConfig> config, Identifier poolRL, int structureSize,
                                  int pieceYOffset, int boundsYOffset, int fixedHeight, int biomeRange) {
        super(config);
        START_POOL = poolRL;
        STRUCTURE_SIZE = structureSize;
        PIECE_Y_OFFSET = pieceYOffset;
        BOUNDS_Y_OFFSET = boundsYOffset;
        FIXED_HEIGHT = fixedHeight;
        SPAWN_AT_TOP_LAND = false;
        BIOME_RANGE = biomeRange;
    }

    public GenericJigsawStructure(Codec<DefaultFeatureConfig> config, Identifier poolRL, int structureSize,
                                  int pieceYOffset, int boundsYOffset, int biomeRange) {
        super(config);
        START_POOL = poolRL;
        STRUCTURE_SIZE = structureSize;
        PIECE_Y_OFFSET = pieceYOffset;
        BOUNDS_Y_OFFSET = boundsYOffset;
        FIXED_HEIGHT = -1;
        SPAWN_AT_TOP_LAND = true;
        BIOME_RANGE = biomeRange;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, ChunkRandom chunkRandom, ChunkPos chunkPos1, Biome biome, ChunkPos chunkPos2, DefaultFeatureConfig NoFeatureConfig, HeightLimitView world) {
        for (int curChunkX = chunkPos1.x - BIOME_RANGE; curChunkX <= chunkPos1.x + BIOME_RANGE; curChunkX++) {
            for (int curChunkZ = chunkPos1.z - BIOME_RANGE; curChunkZ <= chunkPos1.z + BIOME_RANGE; curChunkZ++) {
                if (curChunkX != chunkPos1.x &&
                        curChunkZ != chunkPos1.z &&
                        !biomeSource.getBiomeForNoiseGen(curChunkX << 2, 60, curChunkZ << 2).getGenerationSettings().hasStructureFeature(this)) {
                    return false;
                }
            }
        }

        // Cache the filtered spacing as streaming and shit every structure spawn can get ridiculous.
        // We store the current chunkGenerator as if we go into different dimension, our cache is useless.
        // Gets all UAD structures to space away from (exclude self or else you can't do low spacing anymore)
        // Exclude very low spacing structures or else we can't spawn at all
        if(STRUCTURE_SPACING_CACHE == null || STRUCTURE_SPACING_CACHE.getFirst() != chunkGenerator){
            STRUCTURE_SPACING_CACHE = new Pair<>(
                    chunkGenerator,
                    chunkGenerator.getStructuresConfig().getStructures().entrySet().stream()
                            .filter(entry -> entry.getKey() != this &&
                                            entry.getValue().getSpacing() > 6 &&
                                            UADStructures.REGISTERED_UAD_STRUCTURES.contains(entry.getKey()))
                            .collect(Collectors.toList()));
        }

        int structureRange = 1;
        for (int curChunkX = chunkPos1.x - structureRange; curChunkX <= chunkPos1.x + structureRange; curChunkX++) {
            for (int curChunkZ = chunkPos1.z - structureRange; curChunkZ <= chunkPos1.z + structureRange; curChunkZ++) {
                for (Map.Entry<StructureFeature<?>, StructureConfig> spacingSettings : STRUCTURE_SPACING_CACHE.getSecond()) {
                    ChunkPos structurePos = spacingSettings.getKey().getStartChunk(
                            spacingSettings.getValue(),
                            seed,
                            chunkRandom,
                            chunkPos1.x,
                            chunkPos1.z);

                    // The other structure is here! ABORT!
                    if (structurePos.x == curChunkX && structurePos.z == curChunkZ) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public GenerationStep.Feature getGenerationStep() {
        return GenerationStep.Feature.SURFACE_STRUCTURES;
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return MainStart::new;
    }

    public class MainStart extends MarginedStructureStart<DefaultFeatureConfig> {
        public MainStart(StructureFeature<DefaultFeatureConfig> structureIn, ChunkPos chunkPos, int referenceIn, long seedIn) {
            super(structureIn, chunkPos, referenceIn, seedIn);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig NoFeatureConfig, HeightLimitView heightLimitView) {

            BlockPos blockpos = new BlockPos(chunkPos.getStartX(), FIXED_HEIGHT, chunkPos.getStartZ());
            StructurePoolBasedGenerator.generate(
                    dynamicRegistryManager,
                    new StructurePoolFeatureConfig(() -> dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY)
                            .get(START_POOL),
                            STRUCTURE_SIZE),
                    PoolStructurePiece::new,
                    chunkGenerator,
                    structureManager,
                    blockpos,
                    this,
                    this.random,
                    false,
                    SPAWN_AT_TOP_LAND,
                    heightLimitView);

            // Calculate the size of the structure based on all all children.
            this.setBoundingBoxFromChildren();

            // **THE FOLLOWING TWO LINES ARE OPTIONAL**
            //
            // Right here, you can do interesting stuff with the pieces in this.components such as offset the
            // center piece by 50 blocks up for no reason, remove repeats of a piece or add a new piece so
            // only 1 of that piece exists, etc. But you do not have access to the piece's blocks as this list
            // holds just the piece's size and positions. Blocks will be placed later in JigsawManager.
            //
            // In this case, we do `piece.offset` to raise pieces up by 1 block so that the house is not right on
            // the surface of water or sunken into land a bit.
            //
            // Then we extend the bounding box down by 1 by doing `piece.getBoundingBox().getMinY()` which will cause the
            // land formed around the structure to be lowered and not cover the doorstep. You can raise the bounding
            // box to force the structure to be buried as well. This bounding box stuff with land is only for structures
            // that you added to Structure.field_236384_t_ field handles adding land around the base of structures.
            //
            // By lifting the house up by 1 and lowering the bounding box, the land at bottom of house will now be
            // flush with the surrounding terrain without blocking off the doorstep.
            this.children.forEach(piece -> piece.translate(0, PIECE_Y_OFFSET, 0));
            this.children.forEach(piece -> piece.getBoundingBox().move(0,BOUNDS_Y_OFFSET, 0));

            // I use to debug and quickly find out if the structure is spawning or not and where it is.
            // This is returning the coordinates of the center starting piece.
//            UltraAmplifiedDimension.LOGGER.log(Level.WARN, this.getStructure().getStructureName() + " at " +
//                    this.components.get(0).getBoundingBox().getMinX() + " " +
//                    this.components.get(0).getBoundingBox().getMinY() + " " +
//                    this.components.get(0).getBoundingBox().getMinZ());
        }
    }
}