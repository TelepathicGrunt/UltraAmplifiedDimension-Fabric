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
        ((StructurePieceAccessor)room).uad_callFillWithOutline(world, mutableBoundingBox, box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMinY(), box.getMaxZ(), Blocks.COARSE_DIRT.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
    }

    // called in structures/MineshaftPiecesMixin
    public static void generateLargeRoom(StructureWorldAccess world, MineshaftGenerator.MineshaftRoom room, BlockBox mutableBoundingBox){
        BlockBox box = room.getBoundingBox();
        box.encompass(new BlockBox(
                box.getMinX(),
                box.getMinY(),
                mutableBoundingBox.getMinZ(),
                mutableBoundingBox.getMaxX(),
                box.getMaxY() + Math.min(140, 225 - box.getMinY()),
                mutableBoundingBox.getMaxZ()));

        // floor
        ((StructurePieceAccessor)room).uad_callFillWithOutline(world, mutableBoundingBox, box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX() + 8, box.getMinY(), box.getMaxZ(), Blocks.COARSE_DIRT.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
        ((StructurePieceAccessor)room).uad_callFillWithOutline(world, mutableBoundingBox, box.getMinX() + 3, box.getMinY() + 1, box.getMinZ() + 3, box.getMaxX() - 1, box.getMinY() + 4, box.getMaxZ() - 1, Blocks.CAVE_AIR.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);

        for (BlockBox mutableBoundingBox2 : ((MineshaftRoomAccessor)room).uad_getEntrances()) {
            ((StructurePieceAccessor)room).uad_callFillWithOutline(world, mutableBoundingBox, mutableBoundingBox2.getMinX(), mutableBoundingBox2.getMaxY() - 2, mutableBoundingBox2.getMinZ(), mutableBoundingBox2.getMaxX(), mutableBoundingBox2.getMaxY(), mutableBoundingBox2.getMaxZ(), Blocks.CAVE_AIR.getDefaultState(), Blocks.CAVE_AIR.getDefaultState(), false);
        }

        // wall
        ((StructurePieceAccessor)room).uad_callFillHalfEllipsoid(world, mutableBoundingBox, box.getMinX() + 3, box.getMinY() + 4, box.getMinZ() + 3, box.getMaxX() - 3, box.getMaxY(), box.getMaxZ() - 3, Blocks.CAVE_AIR.getDefaultState(), false);
        MineshaftPiecesUtils.updateLiquidBlocks(room, world, box, box.getMinX() - 1, box.getMinY() + 4, box.getMinZ() - 1, box.getMaxX() + 1, box.getMaxY(), box.getMaxZ() + 1);
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
                    if (!((StructurePieceAccessor) room).uad_callGetBlockAt(world, x, y, z, boundingboxIn).getFluidState().isEmpty()) {
                        float threshold = (xModifier * xModifier) + (yModifier * yModifier) + (zModifier * zModifier);
                        if (threshold <= 1.05F) {
                            mutable.set(((StructurePieceAccessor) room).uad_callApplyXTransform(x, z), ((StructurePieceAccessor) room).uad_callApplyYTransform(y), ((StructurePieceAccessor) room).uad_callApplyZTransform(x, z));

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
