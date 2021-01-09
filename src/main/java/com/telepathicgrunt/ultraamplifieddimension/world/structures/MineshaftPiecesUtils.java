package com.telepathicgrunt.ultraamplifieddimension.world.structures;

import com.telepathicgrunt.ultraamplifieddimension.mixin.structures.MineshaftRoomAccessor;
import com.telepathicgrunt.ultraamplifieddimension.mixin.structures.StructurePieceAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

public class MineshaftPiecesUtils {
    // called in structures/MineshaftPiecesMixin
    public static void generateFloorRoom(StructureWorldAccess world, MineshaftGenerator.MineshaftRoom room, BlockBox mutableBoundingBox){
        BlockBox box = room.getBoundingBox();
        ((StructurePieceAccessor)room).callFillWithOutline(world, mutableBoundingBox, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, Blocks.COARSE_DIRT.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
    }

    // called in structures/MineshaftPiecesMixin
    public static void generateLargeRoom(StructureWorldAccess world, MineshaftGenerator.MineshaftRoom room, BlockBox mutableBoundingBox){
        BlockBox box = room.getBoundingBox();
        box.encompass(new BlockBox(
                mutableBoundingBox.minX,
                box.minY,
                mutableBoundingBox.minZ,
                mutableBoundingBox.maxX,
                box.maxY + Math.min(140, 225 - box.minY),
                mutableBoundingBox.maxZ));

        // floor
        ((StructurePieceAccessor)room).callFillWithOutline(world, mutableBoundingBox, box.minX, box.minY, box.minZ, box.maxX + 8, box.minY, box.maxZ, Blocks.COARSE_DIRT.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
        ((StructurePieceAccessor)room).callFillWithOutline(world, mutableBoundingBox, box.minX + 3, box.minY + 1, box.minZ + 3, box.maxX - 1, box.minY + 4, box.maxZ - 1, Blocks.CAVE_AIR.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);

        for (BlockBox MutableBoundingBox : ((MineshaftRoomAccessor)room).getEntrances()) {
            ((StructurePieceAccessor)room).callFillWithOutline(world, mutableBoundingBox, MutableBoundingBox.minX, MutableBoundingBox.maxY - 2, MutableBoundingBox.minZ, MutableBoundingBox.maxX, MutableBoundingBox.maxY, MutableBoundingBox.maxZ, Blocks.CAVE_AIR.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
        }

        // wall
        ((StructurePieceAccessor)room).callFillHalfEllipsoid(world, mutableBoundingBox, box.minX + 3, box.minY + 4, box.minZ + 3, box.maxX - 3, box.maxY, box.maxZ - 3, Blocks.CAVE_AIR.getDefaultState(), false);
        MineshaftPiecesUtils.updateLiquidBlocks(room, world, box, box.minX - 1, box.minY + 4, box.minZ - 1, box.maxX + 1, box.maxY, box.maxZ + 1);
    }

    // Prevents walls of water that doesnt flow or move.
    public static void updateLiquidBlocks(MineshaftGenerator.MineshaftRoom room, WorldAccess world, BlockBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        float f = maxX - minX + 1;
        float f1 = maxY - minY + 1;
        float f2 = maxZ - minZ + 1;
        float f3 = minX + f / 2.0F;
        float f4 = minZ + f2 / 2.0F;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int y = minY; y <= maxY; ++y) {
            float yModifier = (y - minY) / f1;

            for (int x = minX; x <= maxX; ++x) {
                float xModifier = (x - f3) / (f * 0.5F);

                for (int z = minZ; z <= maxZ; ++z) {
                    float zModifier = (z - f4) / (f2 * 0.5F);
                    if (!((StructurePieceAccessor) room).callGetBlockAt(world, x, y, z, boundingboxIn).getFluidState().isEmpty()) {
                        float threshold = (xModifier * xModifier) + (yModifier * yModifier) + (zModifier * zModifier);
                        if (threshold <= 1.05F) {
                            mutable.set(((StructurePieceAccessor) room).callApplyXTransform(x, z), ((StructurePieceAccessor) room).callApplyYTransform(y), ((StructurePieceAccessor) room).callApplyZTransform(x, z));

                            FluidState ifluidstate = world.getFluidState(mutable);
                            if (!ifluidstate.isEmpty()) {
                                world.getFluidTickScheduler().schedule(mutable, ifluidstate.getFluid(), 0);

                            }
                        }
                    }
                }
            }
        }
    }
}
