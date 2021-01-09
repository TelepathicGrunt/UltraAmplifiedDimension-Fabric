package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.BambooConfig;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;


public class SafeBamboo extends Feature<BambooConfig> {

    private static final BlockState BAMBOO_DEFAULT = Blocks.BAMBOO.getDefaultState().with(BambooBlock.AGE, 1).with(BambooBlock.LEAVES, BambooLeaves.NONE).with(BambooBlock.STAGE, 0);
    private static final BlockState BAMBOO_LEAVES_LARGE_TOP = BAMBOO_DEFAULT.with(BambooBlock.LEAVES, BambooLeaves.LARGE).with(BambooBlock.STAGE, 1);
    private static final BlockState BAMBOO_LEAVES_LARGE = BAMBOO_DEFAULT.with(BambooBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_LEAVES_SMALL = BAMBOO_DEFAULT.with(BambooBlock.LEAVES, BambooLeaves.SMALL);

    public SafeBamboo(Codec<BambooConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random rand, BlockPos position, BambooConfig bambooConfig) {

        int i = 0;
        int maxHeight = rand.nextInt((bambooConfig.maxHeight + 1) - bambooConfig.minHeight) + bambooConfig.minHeight;
        int podzolRange = rand.nextInt((bambooConfig.podzolMaxRadius + 1) - bambooConfig.podzolMinRadius) + bambooConfig.podzolMinRadius;
        BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(position);
        Chunk cachedChunk = world.getChunk(position);

        if (cachedChunk.getBlockState(blockposMutable).isAir()) {
            if (Blocks.BAMBOO.getDefaultState().canPlaceAt(world, blockposMutable)) {
                for (int x = position.getX() - podzolRange; x <= position.getX() + podzolRange; ++x) {
                    for (int z = position.getZ() - podzolRange; z <= position.getZ() + podzolRange; ++z) {
                        for (int y = position.getY() - 2; y <= position.getY() + 2; ++y) {
                            int xDiff = x - position.getX();
                            int zDiff = z - position.getZ();
                            if (xDiff * xDiff + zDiff * zDiff <= podzolRange * podzolRange) {
                                blockposMutable.set(x, y, z);
                                if (rand.nextFloat() < 0.4F && world.getBlockState(blockposMutable).getBlock() == Blocks.GRASS_BLOCK) {
                                    world.setBlockState(blockposMutable, Blocks.PODZOL.getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }

                blockposMutable.set(position);
                for (int height = 0; height < maxHeight && height <= chunkGenerator.getWorldHeight() && cachedChunk.getBlockState(blockposMutable).isAir(); ++height) {
                    cachedChunk.setBlockState(blockposMutable, BAMBOO_DEFAULT, false);
                    blockposMutable.move(Direction.UP, 1);
                }

                // Set the top of bamboo. We moved down one as the block above broke the previous loop.
                if(cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN)).isOf(Blocks.BAMBOO))
                    cachedChunk.setBlockState(blockposMutable, BAMBOO_LEAVES_LARGE_TOP, false);
                if(cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN)).isOf(Blocks.BAMBOO))
                    cachedChunk.setBlockState(blockposMutable, BAMBOO_LEAVES_LARGE, false);
                if(cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN)).isOf(Blocks.BAMBOO))
                    cachedChunk.setBlockState(blockposMutable, BAMBOO_LEAVES_SMALL, false);
            }
            ++i;
        }
        return i > 0;
    }
}
