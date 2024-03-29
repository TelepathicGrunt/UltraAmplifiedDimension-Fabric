package com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;


public class DeepOceanSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public DeepOceanSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    private final static BlockState[] DEAD_CORAL_ARRAY = {Blocks.DEAD_HORN_CORAL_BLOCK.getDefaultState(), Blocks.DEAD_BRAIN_CORAL_BLOCK.getDefaultState(), Blocks.DEAD_BUBBLE_CORAL_BLOCK.getDefaultState(), Blocks.DEAD_FIRE_CORAL_BLOCK.getDefaultState(), Blocks.DEAD_TUBE_CORAL_BLOCK.getDefaultState()};


    @Override
    public void generate(Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int minY, long seed, TernarySurfaceConfig config) {
        this.buildSurface(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, config.getTopMaterial(), config.getUnderMaterial(), config.getUnderwaterMaterial(), seaLevel);
    }


    protected void buildSurface(Random random, Chunk chunkIn, Biome biomeIn, int xStart, int zStart, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState topBlock, BlockState middleBlock, BlockState bottomBlock, int seaLevel) {
        int x = xStart & 15;
        int z = zStart & 15;
        BlockPos.Mutable blockpos$Mutable = new BlockPos.Mutable().set(x, startHeight + 1, z);
        BlockPos.Mutable blockpos$MutableUp = new BlockPos.Mutable();
        BlockState bottom;
        BlockState middle;
        BlockState aboveBlock = middleBlock;
        BlockState above2Block = middleBlock;
        BlockState above3Block = middleBlock;
        BlockState currentBlock;
        boolean useCoral = bottomBlock == DEAD_CORAL_ARRAY[0];

        for (int y = startHeight; y >= seaLevel - 10; --y) {
            blockpos$MutableUp.set(blockpos$Mutable); // store old pos (above 1 block)
            blockpos$Mutable.set(x, y, z);
            currentBlock = chunkIn.getBlockState(blockpos$Mutable);

            currentBlock.getBlock();
            if (currentBlock == defaultBlock) {
                //turns terrain into water to manipulate later
                bottom = defaultFluid;
                chunkIn.setBlockState(blockpos$Mutable, bottom, false);
                currentBlock = bottom;
            }
            else if (!aboveBlock.getFluidState().isEmpty() && currentBlock.getMaterial() == Material.AIR) {

                if (above2Block.getMaterial() == Material.AIR) {
                    //sets very bottom of terrain to bottom block
                    bottom = topBlock;
                    chunkIn.setBlockState(blockpos$MutableUp, bottom, false);
                }
                else {

                    //removes two block high land
                    if (above3Block.getMaterial() == Material.AIR) {
                        bottom = Blocks.AIR.getDefaultState();
                        middle = Blocks.AIR.getDefaultState();
                    }
                    //Chooses blocks for bottom of terrain
                    else {
                        if (useCoral) {
                            //if coral is passed in, randomly chooses a coral block
                            bottom = DEAD_CORAL_ARRAY[random.nextInt(DEAD_CORAL_ARRAY.length)];
                        }
                        else {
                            bottom = bottomBlock;
                        }

                        //prevent ravines from making huge amounts of sand and gravel that can fall which causes an insane spike in lag
                        if (y > 65) {
                            middle = middleBlock;
                        }
                        else {
                            middle = bottomBlock;
                        }
                    }

                    chunkIn.setBlockState(blockpos$MutableUp, bottom, false);
                    currentBlock = bottom;

                    if (!above2Block.getFluidState().isEmpty()){
                        chunkIn.setBlockState(blockpos$MutableUp.move(Direction.UP), middle, false);
                    }
                }
            }

            above3Block = above2Block;
            above2Block = aboveBlock;
            aboveBlock = currentBlock;
        }

        //detects if we are a Lukewarm Ocean, Warm Ocean, or ocean plus their deep variants.
        //Adds sand/gravel bottom towards bottom of terrain gen between 40 - 70
        if (BlockTags.SAND.contains(middleBlock.getBlock()) || bottomBlock == Blocks.DEAD_HORN_CORAL_BLOCK.getDefaultState()) {
            blockpos$Mutable.set(x, 72, z);
            aboveBlock = chunkIn.getBlockState(blockpos$Mutable.move(Direction.DOWN));
            above2Block = chunkIn.getBlockState(blockpos$Mutable.move(Direction.DOWN));

            for (int y = 69; y >= 40; --y) {
                blockpos$Mutable.move(Direction.DOWN);
                currentBlock = chunkIn.getBlockState(blockpos$Mutable);

                //detects if above block is solid and has solid block below and liquid block above.
                //if true, set above block to either sand or gravel
                if (currentBlock.isOpaque() && aboveBlock.isOpaque() && !above2Block.getFluidState().isEmpty()) {
                    if (BlockTags.SAND.contains(middleBlock.getBlock()) || random.nextFloat() < 0.8F) {
                        chunkIn.setBlockState(blockpos$Mutable.move(Direction.UP), middleBlock, false);
                        blockpos$Mutable.move(Direction.DOWN);
                        aboveBlock = middleBlock;
                    }
                }

                above2Block = aboveBlock;
                aboveBlock = currentBlock;
            }
        }
    }
}