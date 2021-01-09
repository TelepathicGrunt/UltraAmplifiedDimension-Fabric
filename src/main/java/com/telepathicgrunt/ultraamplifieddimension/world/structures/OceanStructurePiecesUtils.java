package com.telepathicgrunt.ultraamplifieddimension.world.structures;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;

public class OceanStructurePiecesUtils {
    // called in structures/OceanRuinPiecesMixin and structures/ShipwreckPiecesMixin
    public static int getNewLedgeHeight(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, Structure template, BlockRotation rotation, BlockPos templatePosition) {

        int highestHeight;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        //get center of ruins
        int halfSizeX = template.getSize().getX() / 2;
        int halfSizeZ = template.getSize().getZ() / 2;

        mutable.set(Structure.transformAround(new BlockPos(template.getSize().getX() / 2 - 1, 0, template.getSize().getZ() / 2 - 1), BlockMirror.NONE, rotation, new BlockPos(0, 0, 0)).add(templatePosition));
        highestHeight = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, mutable.getX(), mutable.getZ());
        int bottomOfSea = chunkGenerator.getSeaLevel() - 10;
        BlockState currentState;
        BlockState pastState = Blocks.STONE.getDefaultState();

        // Bias towards the ocean floor.
        int startHeight = Math.min(random.nextInt(random.nextInt(Math.max(highestHeight - bottomOfSea, 1)) + 1) + bottomOfSea + 5, 245);

        // Iterate downward until it hits underwater land that can hold the structure
        for(mutable.move(Direction.UP, startHeight);
            mutable.getY() > Math.max(bottomOfSea - 20, 5);
            mutable.move(Direction.DOWN))
        {
            currentState = world.getBlockState(mutable);
            if((currentState.isOpaque() && !currentState.isIn(BlockTags.ICE)) && pastState.getFluidState().isIn(FluidTags.WATER)){
                if(noAirAround(world, mutable.down(), (int) (halfSizeX * 0.8f), (int) (halfSizeZ * 0.8f))){
                    return mutable.getY();
                }
            }
            pastState = currentState;
        }

        // Set structure at bottom of sea if no valid place was found.
        return bottomOfSea;
    }

    public static boolean noAirAround(StructureWorldAccess world, BlockPos blockpos, int xRange, int zRange) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -xRange; x <= xRange; x += xRange) {
            for (int z = -zRange; z <= zRange; z += zRange) {
                BlockState state = world.getBlockState(mutable.set(blockpos).move(x, 0, z));
                if (state.isAir()) {
                    return false; // No air allowed
                }
            }
        }
        return true;
    }
}
