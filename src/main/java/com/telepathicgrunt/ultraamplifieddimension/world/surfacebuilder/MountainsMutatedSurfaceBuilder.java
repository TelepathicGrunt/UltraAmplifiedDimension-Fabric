package com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADSurfaceBuilders;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import java.util.Random;


public class MountainsMutatedSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public MountainsMutatedSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }


    @Override
    public void buildSurface(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, TernarySurfaceConfig config) {
        if (!(noise < -1.0D) && !(noise > 2.0D)) {
            if (noise > 1.0D) {
                UADSurfaceBuilders.GRAVEL_SURFACE_BUILDER.get().generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.STONE_CONFIG);
            }
            else {
                UADSurfaceBuilders.GRAVEL_SURFACE_BUILDER.get().generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRASS_CONFIG);
            }
        }
        else {
            UADSurfaceBuilders.GRAVEL_SURFACE_BUILDER.get().generate(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.GRAVEL_CONFIG);
        }

    }
}