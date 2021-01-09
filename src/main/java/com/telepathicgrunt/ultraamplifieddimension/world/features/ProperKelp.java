package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.ProbabilityAndCountConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class ProperKelp extends Feature<ProbabilityAndCountConfig> {
    public ProperKelp(Codec<ProbabilityAndCountConfig> codec) {
        super(codec);
    }

    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random rand, BlockPos pos, ProbabilityAndCountConfig config) {
        int placedKelp = 0;

        BlockPos.Mutable mutable = new BlockPos.Mutable().set(pos);
        Chunk chunk = world.getChunk(mutable);
        if (chunk.getBlockState(mutable).isOf(Blocks.WATER)) {
            BlockState kelpState = Blocks.KELP.getDefaultState();
            BlockState kelpState2 = Blocks.KELP_PLANT.getDefaultState();
            int height = 1 + rand.nextInt(10);

            for(int currentHeight = 0; currentHeight <= height; ++currentHeight) {
                if (chunk.getBlockState(mutable).isOf(Blocks.WATER) &&
                    chunk.getBlockState(mutable.up()).isOf(Blocks.WATER) &&
                    kelpState2.canPlaceAt(world, mutable))
                {
                    if (currentHeight == height) {
                        chunk.setBlockState(mutable, kelpState.with(KelpBlock.AGE, rand.nextInt(4) + 20), false);
                        ++placedKelp;
                    } else {
                        world.setBlockState(mutable, kelpState2, 2);
                    }
                }
                else if (currentHeight > 0) {
                    BlockPos blockpos1 = mutable.down();

                    if (kelpState.canPlaceAt(world, blockpos1) &&
                        !chunk.getBlockState(blockpos1.down()).isOf(Blocks.KELP))
                    {
                        chunk.setBlockState(blockpos1, kelpState.with(KelpBlock.AGE, rand.nextInt(4) + 20), false);
                        ++placedKelp;
                    }
                    break;
                }

                mutable.move(Direction.UP);
            }
        }

        return placedKelp > 0;
    }
}
