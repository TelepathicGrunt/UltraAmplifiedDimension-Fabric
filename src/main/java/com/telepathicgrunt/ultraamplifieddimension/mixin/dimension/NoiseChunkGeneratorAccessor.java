package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;
import java.util.function.Supplier;

@Mixin(NoiseChunkGenerator.class)
public interface NoiseChunkGeneratorAccessor {

    @Accessor("verticalNoiseResolution")
    int uad_getVerticalNoiseResolution();

    @Accessor("horizontalNoiseResolution")
    int uad_getHorizontalNoiseResolution();

    @Accessor("noiseSizeX")
    int uad_getNoiseSizeX();

    @Accessor("noiseSizeY")
    int uad_getNoiseSizeY();

    @Accessor("noiseSizeZ")
    int uad_getNoiseSizeZ();

    @Accessor("seed")
    long uad_getfield_236084_w_();

    @Accessor("settings")
    Supplier<ChunkGeneratorSettings> uad_getfield_236080_h_();

    @Accessor("defaultFluid")
    BlockState uad_getDefaultFluid();

    @Accessor("surfaceDepthNoise")
    NoiseSampler uad_getSurfaceDepthNoise();

    @Invoker("buildBedrock")
    void uad_callBuildBedrock(Chunk chunkIn, Random rand);
}