package com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;


public class GravelSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public GravelSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int minY, long seed, TernarySurfaceConfig config) {
        this.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, config.getTopMaterial(), config.getUnderMaterial(), config.getUnderwaterMaterial(), minY, seaLevel);
    }

    protected void buildSurface(Random random, Chunk chunkIn, Biome biomeIn, int xStart, int zStart, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState topBlock, BlockState middleBlock, BlockState bottomBlock, int minY, int seaLevel) {

        BlockState iblockstate = topBlock;
        BlockState iblockstate1 = middleBlock;
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable();
        int bottomLayerNoise = -1;
        int noiseThing = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int x = xStart & 15;
        int z = zStart & 15;

        for (int y = startHeight; y >= minY; --y) {
            blockpos$Mutable.set(x, y, z);
            BlockState iblockstate2 = chunkIn.getBlockState(blockpos$Mutable);
            if (iblockstate2.getMaterial() == Material.AIR) {
                bottomLayerNoise = -1;
            }
            else if (iblockstate2.getBlock() == defaultBlock.getBlock()) {
                if (bottomLayerNoise == -1) {
                    if (noiseThing <= 0) {
                        iblockstate = Blocks.AIR.getDefaultState();
                        iblockstate1 = defaultBlock;
                    }
                    else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                        iblockstate = topBlock;
                        iblockstate1 = middleBlock;
                    }

                    if (y < seaLevel && iblockstate.getMaterial() == Material.AIR) {
                        if (biomeIn.getTemperature(blockpos$Mutable.set(xStart, y, zStart)) < 0.15F) {
                            iblockstate = Blocks.ICE.getDefaultState();
                        }
                        else {
                            iblockstate = defaultFluid;
                        }

                        blockpos$Mutable.set(x, y, z);
                    }

                    bottomLayerNoise = noiseThing;
                    if (y >= seaLevel - 1) {
                        chunkIn.setBlockState(blockpos$Mutable, iblockstate, false);
                    }
                    else if (y < seaLevel - 7 - noiseThing) {
                        iblockstate = Blocks.AIR.getDefaultState();
                        iblockstate1 = defaultBlock;
                        chunkIn.setBlockState(blockpos$Mutable, bottomBlock, false);
                    }
                    else {
                        chunkIn.setBlockState(blockpos$Mutable, iblockstate1, false);
                    }
                }
                else if (bottomLayerNoise > 0) {
                    --bottomLayerNoise;
                    chunkIn.setBlockState(blockpos$Mutable, iblockstate1, false);
                }
            }

            //needed to contain fallable blocks
            if (y < chunkIn.getHeight() && y > 0) {
                Material materialAbove = chunkIn.getBlockState(blockpos$Mutable.up()).getMaterial();
                Material materialBelow = chunkIn.getBlockState(blockpos$Mutable.down()).getMaterial();

                //at bottom of ledge
                if (materialBelow == Material.AIR) {
                    if (materialAbove == Material.AGGREGATE) {
                        // sets bottom block so block above cannot fall
                        chunkIn.setBlockState(blockpos$Mutable, Blocks.STONE.getDefaultState(), false);
                    }
                    else if (materialAbove == Material.AIR) {
                        // one block thick ledges gets removed
                        chunkIn.setBlockState(blockpos$Mutable.up(), Blocks.AIR.getDefaultState(), false);
                    }
                }

            }
        }
    }
}