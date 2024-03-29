package com.telepathicgrunt.ultraamplifieddimension.world.features;

import com.mojang.serialization.Codec;
import com.telepathicgrunt.ultraamplifieddimension.utils.GeneralUtils;
import com.telepathicgrunt.ultraamplifieddimension.utils.OpenSimplexNoise;
import com.telepathicgrunt.ultraamplifieddimension.world.features.configs.ColumnConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;


public class ColumnVertical extends Feature<ColumnConfig> {
    protected OpenSimplexNoise noiseGen;
    protected long seed;


    public void setSeed(long seed) {
        if (this.seed != seed || this.noiseGen == null) {
            this.noiseGen = new OpenSimplexNoise(seed);
            this.seed = seed;
        }
    }

    public ColumnVertical(Codec<ColumnConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean generate(FeatureContext<ColumnConfig> context) {

        setSeed(context.getWorld().getSeed());
        BlockPos centerPos = new ChunkPos(context.getOrigin()).getCenterAtY(context.getOrigin().getY());
        BlockPos.Mutable blockposMutable = new BlockPos.Mutable().set(centerPos);
        int minWidth = 3;
        int maxWidth = 10;
        int ceilingHeight;
        int floorHeight;
        int heightDiff;
        Chunk cachedChunk = context.getWorld().getChunk(blockposMutable);

        //checks to see if position is acceptable for pillar gen
        //finds ceiling
        while (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, cachedChunk.getBlockState(blockposMutable))) {
            //too high for column to generate
            if (blockposMutable.getY() > context.getGenerator().getWorldHeight() - 1) {
                return false;
            }
            blockposMutable.move(Direction.UP, 2);
        }
        ceilingHeight = blockposMutable.getY();

        //find floor
        blockposMutable.set(centerPos); // reset back to normal height
        while (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, cachedChunk.getBlockState(blockposMutable))) {
            //too low for column to generate
            if (blockposMutable.getY() < 3) {
                return false;
            }
            blockposMutable.move(Direction.DOWN, 2);
        }
        floorHeight = blockposMutable.getY();

        heightDiff = ceilingHeight - floorHeight;
        if (heightDiff > 100 || heightDiff < 10) {
            //too tall or short for a column to spawn
            return false;
        }

        //finds how big the smallest part of column should be
        int thinnestWidth = (int) (maxWidth * ((heightDiff) / 100F));
        if (thinnestWidth < minWidth) {
            thinnestWidth = minWidth;
        }

        int widthAtHeight;
        int currentWidth;
        widthAtHeight = getWidthAtHeight(0, heightDiff, thinnestWidth);

        //checks to see if there is enough circular land above and below to hold pillar
        for (int x = centerPos.getX() - widthAtHeight; x <= centerPos.getX() + widthAtHeight; x += 3) {
            for (int z = centerPos.getZ() - widthAtHeight; z <= centerPos.getZ() + widthAtHeight; z += 3) {
                int xDiff = x - centerPos.getX();
                int zDiff = z - centerPos.getZ();
                if (xDiff * xDiff + zDiff * zDiff <= (widthAtHeight) * (widthAtHeight)) {

                    if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                        cachedChunk = context.getWorld().getChunk(blockposMutable);

                    BlockState block1 = cachedChunk.getBlockState(blockposMutable.set(x, ceilingHeight + 3, z));
                    BlockState block2 = cachedChunk.getBlockState(blockposMutable.set(x, floorHeight - 2, z));

                    //there is not enough land to contain bases of pillar
                    if (!GeneralUtils.isFullCube(context.getWorld(), blockposMutable, block1) ||
                        !GeneralUtils.isFullCube(context.getWorld(), blockposMutable, block2)) {
                        return false;
                    }
                }
            }
        }

        //position is valid for pillar gen.
        int xMod;
        int zMod;
        //adds perlin noise to the pillar shape to make it more oval
        //larger pillars will be more oval shaped
        boolean flagImperfection1 = context.getRandom().nextBoolean();
        boolean flagImperfection2 = context.getRandom().nextBoolean();

        if (flagImperfection1 && flagImperfection2) {
            xMod = heightDiff / 20 + 1;
            zMod = heightDiff / 20 + 1;
        }
        else if (flagImperfection1) {
            xMod = heightDiff / 20 + 1;
            zMod = 0;
        }
        else if (flagImperfection2) {
            xMod = 0;
            zMod = heightDiff / 20 + 1;
        }
        else {
            xMod = 0;
            zMod = 0;
        }

        //Begin column gen
        for (int y = -2; y <= heightDiff + 2; y++) {
            widthAtHeight = Math.min(getWidthAtHeight(y, heightDiff, thinnestWidth), 23);

            for (int x = centerPos.getX() - widthAtHeight - xMod - 1; x <= centerPos.getX() + widthAtHeight + xMod + 1; ++x) {
                for (int z = centerPos.getZ() - widthAtHeight - zMod - 1; z <= centerPos.getZ() + widthAtHeight + zMod + 1; ++z) {
                    int xDiff = x - centerPos.getX();
                    int zDiff = z - centerPos.getZ();
                    blockposMutable.set(x, y + floorHeight, z);

                    //scratches the surface for more imperfection
                    //cut the number of scratches on smallest part of pillar by 4
                    boolean flagImperfection3 = this.noiseGen.eval(x * 0.06D, z * 0.6D, y * 0.02D) < 0;
                    if (flagImperfection3 && (widthAtHeight > thinnestWidth || (widthAtHeight == thinnestWidth && context.getRandom().nextInt(4) == 0))) {
                        currentWidth = widthAtHeight - 1;
                    }
                    else {
                        currentWidth = widthAtHeight;
                    }

                    //creates pillar with inside block
                    int xzDiffSquaredStretched = (xMod + 1) * (xDiff * xDiff) + (zMod + 1) * (zDiff * zDiff);
                    int xzDiffSquared = (xDiff * xDiff) + (zDiff * zDiff);
                    if (xzDiffSquaredStretched <= (currentWidth - 1) * (currentWidth - 1)) {

                        if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                            cachedChunk = context.getWorld().getChunk(blockposMutable);

                        if (!cachedChunk.getBlockState(blockposMutable).isOpaque()) {
                            cachedChunk.setBlockState(blockposMutable, context.getConfig().insideBlock, false);
                        }
                    }
                    //We are at non-pillar space
                    //adds top and middle block to pillar part exposed in the below half of pillar
                    else if (y < heightDiff / 2 && xzDiffSquared <= (widthAtHeight + 2) * (widthAtHeight + 2)) {
                        //top block followed by 4 middle blocks below that
                        for (int downward = 0; downward < 6 && y - downward >= -3; downward++) {

                            if(blockposMutable.getX() >> 4 != cachedChunk.getPos().x || blockposMutable.getZ() >> 4 != cachedChunk.getPos().z)
                                cachedChunk = context.getWorld().getChunk(blockposMutable);

                            if (cachedChunk.getBlockState(blockposMutable) == context.getConfig().insideBlock) {
                                if(downward == 1 && blockposMutable.getY() >= context.getGenerator().getSeaLevel() - 1){
                                    if(!context.getConfig().snowy){
                                        cachedChunk.setBlockState(blockposMutable, context.getConfig().topBlock, false);
                                    }
                                    else{
                                        cachedChunk.setBlockState(blockposMutable.move(Direction.UP), Blocks.SNOW.getDefaultState(), false);
                                        blockposMutable.move(Direction.DOWN);

                                        if (context.getConfig().topBlock.contains(SnowyBlock.SNOWY)) {
                                            cachedChunk.setBlockState(blockposMutable, context.getConfig().topBlock.with(SnowyBlock.SNOWY, true), false);
                                        }
                                        else{
                                            cachedChunk.setBlockState(blockposMutable, context.getConfig().topBlock, false);
                                        }
                                    }
                                }
                                else{
                                    cachedChunk.setBlockState(blockposMutable, context.getConfig().middleBlock, false);
                                }
                            }

                            blockposMutable.move(Direction.DOWN); //moves down 1 every loop
                        }
                    }
                }
            }
        }

        return true;
    }


    private int getWidthAtHeight(int y, int heightDiff, int thinnestWidth) {
        if (heightDiff > 80) {
            float yFromCenter = Math.abs(y - heightDiff / 2F) - 2;
            return thinnestWidth + (int) ((yFromCenter / 4F) * (yFromCenter / 4F) / 10);
        }
        else if (heightDiff > 60) {
            float yFromCenter = Math.abs(y - heightDiff / 2F) - 1;
            return thinnestWidth + (int) ((yFromCenter / 3F) * (yFromCenter / 3F) / 9);
        }
        else if (heightDiff > 30) {
            float yFromCenter = Math.abs(y - heightDiff / 2F);
            return thinnestWidth + (int) ((yFromCenter / 2.6F) * (yFromCenter / 2.6F) / 6);
        }
        else if (heightDiff > 18) {
            float yFromCenter = Math.abs(y - heightDiff / 2F) + 1;
            return thinnestWidth + (int) ((yFromCenter / 2.8F) * (yFromCenter / 2.8F) / 3);
        }
        else {
            float yFromCenter = Math.abs(y - heightDiff / 2F) + 3;
            return thinnestWidth + (int) ((yFromCenter / 2.7f) * (yFromCenter / 2.7f) / 3);
        }
    }
}
