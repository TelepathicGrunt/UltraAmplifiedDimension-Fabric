package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import java.util.Random;


public class SnowLayerWithoutIceFeature extends Feature<DefaultFeatureConfig> {
    public SnowLayerWithoutIceFeature(Codec<DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos position, DefaultFeatureConfig config) {
        Biome biome = world.getBiome(position);
        BlockPos.Mutable blockposMutable1 = new BlockPos.Mutable();
        BlockPos.Mutable blockposMutable2 = new BlockPos.Mutable();
        Chunk cachedChunk = world.getChunk(position);

        int y = cachedChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, position.getX(), position.getZ()) + 1;
        blockposMutable1.set(position.getX(), y, position.getZ());
        blockposMutable2.set(blockposMutable1).move(Direction.DOWN);
        BlockState blockState1 = cachedChunk.getBlockState(blockposMutable1);

        if (blockState1.isAir()) {
            if (biome.canSetSnow(world, blockposMutable1)) {

                cachedChunk.setBlockState(blockposMutable1, Blocks.SNOW.getDefaultState(), false);
                BlockState blockStateBottom = cachedChunk.getBlockState(blockposMutable2);

                // Extra check to follow leaves into nearby chunks and give them the snow they would've avoided
                // Run this only when on leaves and pos is on chunk edge to minimize wasted time
                int xMod = blockposMutable1.getX() & 0x000F;
                int zMod = blockposMutable1.getZ() & 0x000F;
                if (blockStateBottom.isIn(BlockTags.LEAVES) && (xMod == 0 || xMod == 15 || zMod == 0 || zMod == 15)) {
                    SnowIceLayerHandlerFeature.placeSnowOnNearbyLeaves(world, biome, blockposMutable1, cachedChunk);
                }

                if (blockStateBottom.contains(SnowyBlock.SNOWY)) {
                    cachedChunk.setBlockState(blockposMutable2, blockStateBottom.with(SnowyBlock.SNOWY, true), false);
                }
            }
        }

        return true;
    }

}