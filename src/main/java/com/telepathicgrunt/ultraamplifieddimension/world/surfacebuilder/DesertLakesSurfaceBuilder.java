package com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADSurfaceBuilders;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import java.util.Random;


public class DesertLakesSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public DesertLakesSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }


    @Override
    public void buildSurface(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TernarySurfaceConfig config) {

        if (noise > 1.0D) {
            UADSurfaceBuilders.SAND_SURFACE_BUILDER.get().generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, UADSurfaceBuilders.SAND_SANDSTONE_SANDSTONE_SURFACE);
        }
        else {
            SurfaceBuilder.DEFAULT.generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
        }
    }
}