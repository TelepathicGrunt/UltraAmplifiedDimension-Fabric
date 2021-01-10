package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public interface NoiseChunkGeneratorAccessor {

    @Accessor
    int getVerticalNoiseResolution();

    @Accessor
    int getHorizontalNoiseResolution();

    @Accessor
    int getNoiseSizeX();

    @Accessor
    int getNoiseSizeY();

    @Accessor
    int getNoiseSizeZ();

    @Accessor
    OctavePerlinNoiseSampler getLowerInterpolatedNoise();

    @Accessor
    OctavePerlinNoiseSampler getUpperInterpolatedNoise();

    @Accessor
    OctavePerlinNoiseSampler getInterpolationNoise();

    @Accessor
    OctavePerlinNoiseSampler getDensityNoise();

    @Accessor("seed")
    long getfield_236084_w_();

    @Accessor("settings")
    Supplier<ChunkGeneratorSettings> getfield_236080_h_();

    @Accessor
    BlockState getDefaultFluid();

    @Accessor
    NoiseSampler getSurfaceDepthNoise();

    @Invoker
    void callBuildBedrock(Chunk chunkIn, Random rand);

    @Accessor
    long getSeed();

    @Mutable
    @Accessor
    void setSeed(long seed);
}