package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.NoiseChunkGeneratorAccessor;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.ColumnConfig;
import net.minecraft.block.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;
import java.util.Set;


public class ColumnRamp extends Feature<ColumnConfig> {
    public final Set<Block> irreplacableBlocks;

    public ColumnRamp(Codec<ColumnConfig> configFactory) {
        super(configFactory);

        irreplacableBlocks = ImmutableSet.of(Blocks.BEEHIVE, Blocks.AIR, Blocks.CAVE_AIR, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM, Blocks.CACTUS, UADBlocks.BIG_CACTUS_BODY_BLOCK, UADBlocks.BIG_CACTUS_CORNER_BLOCK, UADBlocks.BIG_CACTUS_MAIN_BLOCK);
    }


    @Override
    public boolean generate(FeatureContext<ColumnConfig> context) {

        BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(context.getOrigin());
        int minWidth = 4;
        int ceilingHeight;
        int bottomFloorHeight;
        int topFloorHeight;
        int heightDiff;
        Chunk cachedChunk = context.getWorld().getChunk(blockposMutable);

        //finds ceiling
        while (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, cachedChunk.getBlockState(blockposMutable))) {
            //too high for ramp to generate
            if (blockposMutable.getY() > context.getGenerator().getWorldHeight() - 1) {
                return false;
            }
            blockposMutable.move(Direction.UP, 2);
        }
        ceilingHeight = blockposMutable.getY();

        //finds floor above ceiling
        while (GeneralUtils.isFullCube(context.getWorld(), blockposMutable, cachedChunk.getBlockState(blockposMutable))) {
            //too high for ramp to generate
            if (blockposMutable.getY() > context.getGenerator().getWorldHeight() - 1) {
                return false;
            }
            blockposMutable.move(Direction.UP);
        }
        topFloorHeight = blockposMutable.getY();

        //too thick or thin for ramp to generate
        int ledgeThickness = topFloorHeight - ceilingHeight;
        if (ledgeThickness > 7 || ledgeThickness < 2) {
            return false;
        }

        //find floor
        blockposMutable.set(context.getOrigin()); // reset back to normal height
        while (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, cachedChunk.getBlockState(blockposMutable))) {
            //too low/tall for column to generate
            if (blockposMutable.getY() < 70) {
                return false;
            }
            blockposMutable.move(Direction.DOWN, 2);
        }
        bottomFloorHeight = blockposMutable.getY();

        heightDiff = ceilingHeight - bottomFloorHeight;
        if (heightDiff > 27 || heightDiff < 8) {
            //too tall or short for a column ramp to spawn
            return false;
        }

        //how much to turn on a range of -1 to 1. -1 for north, 0 for south
        float randFloat = context.getRandom().nextFloat();
        float xTurningValue = (float) Math.sin(randFloat * Math.PI * 2);
        float zTurningValue = (float) Math.cos(randFloat * Math.PI * 2);

        //min thickness   where we are in height  /  controls thickening rate
        int widthAtHeight = getWidthAtHeight(0, heightDiff + 5, minWidth);

        //gets center of the ceiling position and floor position
        int xPosCeiling = context.getOrigin().getX() + getOffsetAtHeight(heightDiff + 1, heightDiff, xTurningValue);
        int zPosCeiling = context.getOrigin().getZ() + getOffsetAtHeight(0, heightDiff, zTurningValue);
        int xPosFloor = context.getOrigin().getX() - getOffsetAtHeight(heightDiff - 1, heightDiff, xTurningValue);
        int zPosFloor = context.getOrigin().getZ() + getOffsetAtHeight(0, heightDiff, zTurningValue);

        //checks to see if there is enough land above and below to hold pillar
        for (int x = -widthAtHeight; x <= widthAtHeight; x++) {
            for (int z = -widthAtHeight; z <= widthAtHeight; z++) {
                if (x * x + z * z > widthAtHeight * widthAtHeight * 0.85 && x * x + z * z < widthAtHeight * widthAtHeight) {

                    if (blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                        cachedChunk = context.getWorld().getChunk(blockposMutable);

                    BlockState block1 = cachedChunk.getBlockState(blockposMutable.set(xPosCeiling + x, ceilingHeight + 2, zPosCeiling + z));
                    BlockState block2 = cachedChunk.getBlockState(blockposMutable.set(xPosFloor + x, bottomFloorHeight - 2, zPosFloor + z));

                    //debugging
                    //world.setBlockState(blockpos$Mutable.setPos(position.getX() + x + getOffsetAtHeight(heightDiff + 1, heightDiff, xTurningValue), ceilingHeight + 2, position.getZ() + z + getOffsetAtHeight(0, heightDiff, zTurningValue)), Blocks.REDSTONE_BLOCK.getDefaultState(), 2);
                    //world.setBlockState(blockpos$Mutable.setPos(position.getX() + x - getOffsetAtHeight(-1, heightDiff, xTurningValue), bottomFloorHeight - 2, position.getZ() + z - getOffsetAtHeight(0, heightDiff, zTurningValue)), Blocks.LAPIS_BLOCK.getDefaultState(), 2);

                    //there is not enough land to contain bases of ramp
                    if (!block1.isOpaque() || !block2.isOpaque()) {
                        return false;
                    }
                }
            }
        }

        //If this is reached, position is valid for ramp gen.

        //debugging
        //        if(heightDiff > 18) {
        //        	UltraAmplified.LOGGER.info("Large Ramp: "+position.getX()+" "+position.getY()+" "+position.getZ());
        //        }

        int xOffset;
        int zOffset;
        int xDiff;
        int zDiff;
        BlockPos.Mutable tempMutable = new BlockPos.Mutable();
        BlockPos.Mutable tempPos2 = new BlockPos.Mutable();

        //clears hole for ramp
        for (int y = -2; y <= heightDiff + 3; y++) {
            // Method interprets input as:  min thickness  ,  where we are in height  ,  controls thickening rate
            widthAtHeight = getWidthAtHeight(y, heightDiff + 2, minWidth);

            if (heightDiff < 16) {
                xOffset = (int) (getOffsetAtHeight(y, heightDiff, xTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, xTurningValue) / 2f) * 2);
                zOffset = (int) (getOffsetAtHeight(y, heightDiff, zTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, zTurningValue) / 2f) * 2);
            }
            else if (heightDiff < 21) {
                xOffset = (int) (getOffsetAtHeight(y, heightDiff, xTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, xTurningValue) / 3f) * 4);
                zOffset = (int) (getOffsetAtHeight(y, heightDiff, zTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, zTurningValue) / 3f) * 4);
            }
            else {
                xOffset = (int) (getOffsetAtHeight(y, heightDiff, xTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, xTurningValue) / 3f) * 6);
                zOffset = (int) (getOffsetAtHeight(y, heightDiff, zTurningValue) - Math.signum(getOffsetAtHeight(y, heightDiff, zTurningValue) / 3f) * 6);
            }

            //Begin clearing gen
            for (int x = context.getOrigin().getX() - widthAtHeight - 1; x <= context.getOrigin().getX() + widthAtHeight + 1; ++x) {
                for (int z = context.getOrigin().getZ() - widthAtHeight - 1; z <= context.getOrigin().getZ() + widthAtHeight + 1; ++z) {
                    xDiff = x - context.getOrigin().getX();
                    zDiff = z - context.getOrigin().getZ();
                    blockposMutable.set(x + xOffset, y + bottomFloorHeight + 3, z + zOffset);

                    //creates pillar with inside block
                    int xzDiffSquaredStretched = (xDiff * xDiff) + (zDiff * zDiff);
                    int circleBounds = (int) ((widthAtHeight - 1) * (widthAtHeight - 1) - 0.5F);

                    if (y > heightDiff) {
                        circleBounds *= (0.6f / (y - heightDiff));
                    }

                    if (blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                        cachedChunk = context.getWorld().getChunk(blockposMutable);

                    BlockState block = cachedChunk.getBlockState(blockposMutable);
                    if (!block.isIn(BlockTags.LEAVES) && !block.isIn(BlockTags.LOGS) && !irreplacableBlocks.contains(block.getBlock()) && xzDiffSquaredStretched <= circleBounds) {
                        if (blockposMutable.getY() < context.getGenerator().getSeaLevel() && context.getGenerator() instanceof NoiseChunkGenerator) {
                            cachedChunk.setBlockState(blockposMutable, ((NoiseChunkGeneratorAccessor) context.getGenerator()).uad_getDefaultFluid(), false);
                        }
                        else {

                            tempMutable.set(blockposMutable).move(Direction.DOWN);
                            if (context.getConfig().snowy && Blocks.SNOW.getDefaultState().canPlaceAt(context.getWorld(), blockposMutable)) {
                                cachedChunk.setBlockState(blockposMutable, Blocks.SNOW.getDefaultState(), false);

                                BlockState belowBlock = cachedChunk.getBlockState(tempMutable);
                                if (belowBlock.contains(SnowyBlock.SNOWY)) {
                                    cachedChunk.setBlockState(tempMutable, belowBlock.with(SnowyBlock.SNOWY, true), false);
                                }
                            }
                            else {
                                BlockState aboveBlock = cachedChunk.getBlockState(blockposMutable.move(Direction.UP));
                                blockposMutable.move(Direction.DOWN);
                                // Reduce amount of floating trees by leaving the dirt under their trunk.
                                if(!aboveBlock.isIn(BlockTags.LOGS)){
                                    cachedChunk.setBlockState(blockposMutable, Blocks.AIR.getDefaultState(), false);
                                }
                            }
                        }

                        //remove floating plants so they aren't hovering.
                        //check above while moving up one.
                        tempMutable.set(blockposMutable).move(Direction.UP);
                        block = cachedChunk.getBlockState(tempMutable);
                        while (tempMutable.getY() < context.getGenerator().getWorldHeight() && !block.canPlaceAt(context.getWorld(), tempMutable)) {
                            cachedChunk.setBlockState(tempMutable, Blocks.AIR.getDefaultState(), false);
                            block = cachedChunk.getBlockState(tempMutable.move(Direction.UP));
                        }

                        //adds top block to exposed middle block after air was set
                        BlockState blockBelowAir = cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN));
                        BlockState blockBelowBelowAir = cachedChunk.getBlockState(blockposMutable.move(Direction.DOWN));
                        blockposMutable.move(Direction.UP); // Move back to blockBelowAir

                        if (GeneralUtils.isFullCube(context.getWorld(), blockposMutable, blockBelowAir)) {
                            if ((context.getConfig().topBlock.getBlock() instanceof FallingBlock && blockBelowBelowAir.isAir()) || blockposMutable.getY() < context.getGenerator().getSeaLevel()) {
                                cachedChunk.setBlockState(blockposMutable, context.getConfig().middleBlock, false);
                            }
                            else {
                                cachedChunk.setBlockState(blockposMutable, context.getConfig().topBlock, false);
                                tempMutable.set(blockposMutable).move(Direction.UP);
                                BlockState aboveBlock = cachedChunk.getBlockState(tempMutable);

                                if (context.getConfig().snowy && aboveBlock.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(context.getWorld(), tempMutable)) {
                                    cachedChunk.setBlockState(tempMutable, Blocks.SNOW.getDefaultState(), false);

                                    if (context.getConfig().topBlock.contains(SnowyBlock.SNOWY)) {
                                        cachedChunk.setBlockState(blockposMutable, context.getConfig().topBlock.with(SnowyBlock.SNOWY, true), false);
                                    }
                                }
                            }
                        }

                        blockposMutable.move(Direction.UP); // Move back to air spot
                    }
                }
            }
        }

        //makes ramp
        for (int y = -2; y <= heightDiff + 4; y++) {
            // Method interprets input as:  min thickness  ,  where we are in height  ,  controls thickening rate
            widthAtHeight = getWidthAtHeight(y, heightDiff + 5, minWidth);
            xOffset = getOffsetAtHeight(y, heightDiff, xTurningValue);
            zOffset = getOffsetAtHeight(y, heightDiff, zTurningValue);

            //Begin column gen
            for (int x = context.getOrigin().getX() - widthAtHeight - 1; x <= context.getOrigin().getX() + widthAtHeight + 1; ++x) {
                for (int z = context.getOrigin().getZ() - widthAtHeight - 1; z <= context.getOrigin().getZ() + widthAtHeight + 1; ++z) {
                    xDiff = x - context.getOrigin().getX();
                    zDiff = z - context.getOrigin().getZ();
                    blockposMutable.set(x + xOffset, y + bottomFloorHeight, z + zOffset);

                    //creates pillar with inside block
                    int xzDiffSquaredStretched = (xDiff * xDiff) + (zDiff * zDiff);
                    int circleBounds = (int) ((widthAtHeight - 1) * (widthAtHeight - 1) - 0.5F);

                    if (y > heightDiff - 3) {
                        circleBounds *= (0.8f / (y - (heightDiff - 3)));
                    }

                    if (y <= heightDiff && xzDiffSquaredStretched <= circleBounds) {
                        if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, context.getWorld().getBlockState(blockposMutable))) {
                            context.getWorld().setBlockState(blockposMutable, context.getConfig().insideBlock, 2);
                        }
                    }
                    //We are at non-pillar space
                    //adds top and middle block to pillar part exposed in the below half of pillar
                    else if (y > heightDiff || xzDiffSquaredStretched <= (widthAtHeight + 3) * (widthAtHeight + 3)) {
                        //top block followed by 4 middle blocks below that
                        for (int downward = 0; downward < 6 && y - downward >= -3; downward++) {
                            tempMutable.set(blockposMutable).move(Direction.DOWN, downward);
                            BlockState block = context.getWorld().getBlockState(tempMutable);
                            BlockState blockBelow = context.getWorld().getBlockState(tempMutable.move(Direction.DOWN));
                            tempMutable.move(Direction.UP);

                            if (block == context.getConfig().insideBlock) {
                                if (tempMutable.getY() >= context.getGenerator().getSeaLevel() - 1 && downward == 1 && !(context.getConfig().topBlock.getBlock() instanceof FallingBlock && blockBelow.isAir())) {
                                    context.getWorld().setBlockState(tempMutable, context.getConfig().topBlock, 2);

                                    tempPos2.set(tempMutable).move(Direction.UP);
                                    BlockState aboveBlock = context.getWorld().getBlockState(tempPos2);

                                    if (context.getConfig().snowy && aboveBlock.isAir() && Blocks.SNOW.getDefaultState().canPlaceAt(context.getWorld(), tempPos2)) {
                                        context.getWorld().setBlockState(tempPos2, Blocks.SNOW.getDefaultState(), 2);

                                        if (context.getConfig().topBlock.contains(SnowyBlock.SNOWY)) {
                                            context.getWorld().setBlockState(tempMutable, context.getConfig().topBlock.with(SnowyBlock.SNOWY, true), 2);
                                        }
                                    }
                                }
                                else {
                                    context.getWorld().setBlockState(tempMutable, context.getConfig().middleBlock, 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }


    private int getWidthAtHeight(int y, int heightDiff, int thinnestWidth) {
        float yFromCenter = y - heightDiff * 0.5F;
        yFromCenter = Math.abs(yFromCenter * 0.4F) + 3;

        return thinnestWidth + (int) ((yFromCenter * yFromCenter) / 8);
    }


    private int getOffsetAtHeight(int y, int heightDiff, float turningValue) {
        float yFromCenter = y - heightDiff / 2F;
        return (int) (turningValue * yFromCenter);
    }
}
