package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusBodyBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusCornerBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusMainBlock;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.HeightConfig;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class BigCactus extends Feature<HeightConfig> {

    public BigCactus(Codec<HeightConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<HeightConfig> context) {
        BlockPos.Mutable blockpos = new BlockPos.Mutable().set(context.getOrigin());
        Chunk cachedChunk = context.getWorld().getChunk(blockpos);
        Direction cactusFacing = Direction.fromHorizontal(context.getRandom().nextInt(4));

        //gets height with some variations
        //then gets left and right side between 2 and maximum height - 3
        //then finds the direction that the cactus will be facing
        int maxHeight = context.getConfig().height + context.getRandom().nextInt(2);
        int frontSideHeight = 2 + context.getRandom().nextInt(maxHeight - 4);
        int backSideHeight = 2 + context.getRandom().nextInt(maxHeight - 4);

        // make sure there is space for the cactus and its arms
        while(blockpos.getY() <= context.getOrigin().getY() + context.getConfig().height + 1){
            if(!cachedChunk.getBlockState(blockpos).isAir() ||
                (blockpos.getY() >= context.getOrigin().getY() + frontSideHeight && (!cachedChunk.getBlockState(blockpos.move(cactusFacing)).isAir() || !cachedChunk.getBlockState(blockpos.move(cactusFacing)).isAir())) ||
                (blockpos.getY() >= context.getOrigin().getY() + backSideHeight && (!cachedChunk.getBlockState(blockpos.move(cactusFacing.getOpposite(), 3)).isAir() || !cachedChunk.getBlockState(blockpos.move(cactusFacing.getOpposite())).isAir())))
            {
                return false;
            }

            blockpos.move(Direction.UP).move(cactusFacing, 2); // move up and back to center stalk
        }
        // Go back to start pos
        blockpos.set(context.getOrigin());

        if (cachedChunk.getBlockState(blockpos.move(Direction.DOWN)).isIn(BlockTags.SAND)) {
            blockpos.move(Direction.UP); // Move back to start pos

            //create cactus from ground up
            for (int currentHeight = 0; currentHeight < maxHeight && cachedChunk.getBlockState(blockpos).isAir(); currentHeight++) {
                if (blockpos.getY() <= 254 && (currentHeight == frontSideHeight || currentHeight == backSideHeight)) {
                    //will make at least one branch

                    //finds what the center should be
                    if (frontSideHeight == backSideHeight) {
                        cachedChunk.setBlockState(blockpos, UADBlocks.BIG_CACTUS_BODY_BLOCK.getDefaultState().with(BigCactusBodyBlock.FACING, cactusFacing), false);
                    }
                    else {
                        cachedChunk.setBlockState(blockpos, UADBlocks.BIG_CACTUS_CORNER_BLOCK.getDefaultState().with(BigCactusCornerBlock.FACING, currentHeight == frontSideHeight ? cactusFacing.getOpposite() : cactusFacing), false);
                    }

                    //create the branches off of cactus
                    if (currentHeight == frontSideHeight) {
                        createBranch(context.getWorld(), context.getGenerator(), blockpos, cactusFacing, context.getRandom().nextInt(maxHeight - frontSideHeight - 2) + 2);
                    }
                    if (currentHeight == backSideHeight) {
                        createBranch(context.getWorld(), context.getGenerator(), blockpos, cactusFacing.getOpposite(), context.getRandom().nextInt(maxHeight - backSideHeight - 2) + 2);
                    }

                }
                else {
                    //places normal vertical cactus
                    cachedChunk.setBlockState(blockpos, UADBlocks.BIG_CACTUS_MAIN_BLOCK.getDefaultState().with(BigCactusMainBlock.FACING, Direction.UP), false);
                }

                blockpos.move(Direction.UP);
            }

        }

        //cactus finished generating
        return true;
    }


    /**
     * Will create branch but will stop early if any spot along the way is not an air block
     *
     * @param world           - world to check and place blocks in
     * @param position        - position of center of cactus that branch is coming off of
     * @param branchDirection - direction that the branch will go
     */
    private void createBranch(StructureWorldAccess world, ChunkGenerator chunkGenerator, BlockPos position, Direction branchDirection, int maxHeightUp) {
        //horizontal part of branch first
        BlockPos.Mutable blockpos = new BlockPos.Mutable().set(position).move(branchDirection);
        if (world.isAir(blockpos)) {
            world.setBlockState(blockpos, UADBlocks.BIG_CACTUS_MAIN_BLOCK.getDefaultState().with(BigCactusMainBlock.FACING, branchDirection), 3);
        }
        else {
            return;
        }

        //corner
        blockpos.move(branchDirection);
        if (world.isAir(blockpos)) {
            world.setBlockState(blockpos, UADBlocks.BIG_CACTUS_CORNER_BLOCK.getDefaultState().with(BigCactusCornerBlock.FACING, branchDirection), 3);
        }
        else {
            return;
        }

        //upward part of branch
        blockpos.move(Direction.UP);
        Chunk cachedChunk = world.getChunk(blockpos);
        for (int currentHeight = 1; currentHeight < maxHeightUp && blockpos.getY() <= chunkGenerator.getWorldHeight(); currentHeight++) {
            if (cachedChunk.getBlockState(blockpos).isAir()) {
                cachedChunk.setBlockState(blockpos, UADBlocks.BIG_CACTUS_MAIN_BLOCK.getDefaultState().with(BigCactusMainBlock.FACING, Direction.UP), false);

            }
            else {
                return;
            }
            blockpos.move(Direction.UP);
        }

    }
}
