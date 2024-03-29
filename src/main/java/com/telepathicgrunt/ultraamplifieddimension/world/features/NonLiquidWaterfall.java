package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.TwoBlockStateConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class NonLiquidWaterfall extends Feature<TwoBlockStateConfig> {


    public NonLiquidWaterfall(Codec<TwoBlockStateConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<TwoBlockStateConfig> context) {

        //creates a waterfall of blue ice that has a puddle at bottom
        BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
        Chunk cachedChunk = context.getWorld().getChunk(blockposMutable);

        // valid ceiling
        BlockState blockState = cachedChunk.getBlockState(blockposMutable.move(Direction.UP));
        if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, blockState) || !blockState.getFluidState().isEmpty()) {
            return false;
        }

        // If bottom is valid block too, then we need 1 opening on side to spawn
        int numberOfSolidSides = 0;
        int neededNumberOfSides;

        blockState = cachedChunk.getBlockState(blockposMutable.set(context.getOrigin()).move(Direction.DOWN));
        if(!blockState.getFluidState().isEmpty()) return false;

        if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, blockState)) {
            neededNumberOfSides = 4; // Ceiling with all sides blocked off
        }
        else {
            neededNumberOfSides = 3; // Wall needs 3 sides blocked off
        }

        Direction emptySpot = null;
        for (Direction face : Direction.Type.HORIZONTAL) {
            blockposMutable.set(context.getOrigin()).move(face);
            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                cachedChunk = context.getWorld().getChunk(blockposMutable);

            blockState = cachedChunk.getBlockState(blockposMutable);
            if(!blockState.getFluidState().isEmpty()) return false;

            if (GeneralUtils.isFullCube(context.getWorld(), blockposMutable, blockState)) {
                ++numberOfSolidSides;
            }
            else {
                emptySpot = face;
            }
        }

        // Position invalid. Do not pass go or collect $200
        if(numberOfSolidSides != neededNumberOfSides){
            return false;
        }

        //initial starting point of icefall
        blockposMutable.set(context.getOrigin());
        if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
            cachedChunk = context.getWorld().getChunk(blockposMutable);

        cachedChunk.setBlockState(blockposMutable, context.getConfig().state1, false);


        // If in wall, move out of wall
        if (emptySpot != null) {
            // move to empty spot and set it to ice
            blockposMutable.move(emptySpot);
            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                cachedChunk = context.getWorld().getChunk(blockposMutable);

            cachedChunk.setBlockState(blockposMutable, context.getConfig().state1, false);
        }

        int ledgeOffsets = 0;
        boolean deadEnd;

        //places blue ice downward until it hit solid block
        while (blockposMutable.getY() > 1) {

            // we went over too many ledges. End the flow.
            if (ledgeOffsets > 3) {
                break;
            }

            blockposMutable.move(Direction.DOWN); //move down to check below
            BlockState belowBlockState = cachedChunk.getBlockState(blockposMutable);

            // Move down until it hits a solid block or liquid
            if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, belowBlockState) && belowBlockState.getFluidState().isEmpty())
            {
                cachedChunk.setBlockState(blockposMutable, context.getConfig().state1, false);
                continue; //restart loop to keep moving downward
            }

            //move back up above the solid/liquid block
            blockposMutable.move(Direction.UP);

            //goes around ledge
            deadEnd = true;
            for (Direction face : Direction.Type.HORIZONTAL) {

                blockposMutable.move(face);
                if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                    cachedChunk = context.getWorld().getChunk(blockposMutable);
                BlockState sideBlockState = cachedChunk.getBlockState(blockposMutable);

                // side is open to move to
                if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, sideBlockState) && sideBlockState.getFluidState().isEmpty()) {

                    // check under side to see if it is good
                    blockposMutable.move(Direction.DOWN);
                    BlockState belowSideBlockState = cachedChunk.getBlockState(blockposMutable);

                    // side below is valid. Time to flow to ledge
                    if(!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, belowSideBlockState) && belowSideBlockState.getFluidState().isEmpty()){
                        blockposMutable.move(Direction.UP);
                        cachedChunk.setBlockState(blockposMutable, context.getConfig().state1, false);
                        cachedChunk.setBlockState(blockposMutable.move(Direction.DOWN), context.getConfig().state1, false);

                        ledgeOffsets++;
                        deadEnd = false;
                        if (blockposMutable.getY() <= 1) {
                            return false;
                        }
                        else {
                            // move back to the while loop to move down
                            break;
                        }
                    }

                    // move back up to side pos
                    blockposMutable.move(Direction.UP);
                }

                // move back to center to check the next side
                blockposMutable.move(face.getOpposite());
            }

            if(deadEnd){
                break;
            }
        }

        //creates blue ice puddle at bottom
        BlockPos position = blockposMutable.toImmutable();
        int width = context.getRandom().nextInt(2) + 2;
        for (int y = -2; y < 0; y++) {
            for (int x = -width; x <= width; x++) {
                for (int z = -width; z <= width; z++) {
                    if ((x * x) + (z * z) <= width * width) {
                        if (position.getY() + y > 1 && position.getY() + y < context.getGenerator().getWorldHeight()) {

                            blockposMutable.set(position).move(x, y, z);
                            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z){
                                cachedChunk = context.getWorld().getChunk(blockposMutable);
                            }

                            BlockState blockStateAtPuddlePos = cachedChunk.getBlockState(blockposMutable);

                            //replace solid and liquid blocks
                            if (GeneralUtils.isFullCube(context.getWorld(), blockposMutable, blockStateAtPuddlePos) || !blockStateAtPuddlePos.getFluidState().isEmpty()) {

                                BlockState biomeTopBlock = context.getWorld().getBiome(blockposMutable).getGenerationSettings().getSurfaceConfig().getTopMaterial();
                                BlockState aboveBlockState = cachedChunk.getBlockState(blockposMutable.move(Direction.UP));
                                boolean isAboveFullCube = GeneralUtils.isFullCube(context.getWorld(), blockposMutable, aboveBlockState);
                                blockposMutable.move(Direction.DOWN);

                                // If replacing biome top block, place state 2 instead of state 1
                                if (blockStateAtPuddlePos == biomeTopBlock && !isAboveFullCube) {
                                    cachedChunk.setBlockState(blockposMutable, context.getConfig().state2, false);

                                    // Remove snow layer above
                                    if(aboveBlockState.isOf(Blocks.SNOW)){
                                        cachedChunk.setBlockState(blockposMutable, Blocks.AIR.getDefaultState(), false);
                                    }
                                }
                                else{
                                    // Make ice turn lava into obsidian to help separate the two better.
                                    if(context.getConfig().state1.isIn(BlockTags.ICE) && blockStateAtPuddlePos.getFluidState().isIn(FluidTags.LAVA)){
                                        cachedChunk.setBlockState(blockposMutable, Blocks.OBSIDIAN.getDefaultState(), false);
                                    }
                                    else{
                                        cachedChunk.setBlockState(blockposMutable, context.getConfig().state1, false);
                                    }
                                }
                            }
                        }
                        else {
                            break;
                        }

                    }
                }
            }

            width++;
        }

        return true;
    }


}