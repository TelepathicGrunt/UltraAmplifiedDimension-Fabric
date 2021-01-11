package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.block.BlockState;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructurePiece.class)
public interface StructurePieceAccessor {
    @Invoker("getBlockAt")
    BlockState uad_callGetBlockAt(BlockView worldIn, int x, int y, int z, BlockBox boundingboxIn);

    @Invoker("applyXTransform")
    int uad_callApplyXTransform(int x, int z);

    @Invoker("applyYTransform")
    int uad_callApplyYTransform(int y);

    @Invoker("applyZTransform")
    int uad_callApplyZTransform(int x, int z);

    @Invoker("fillHalfEllipsoid")
    void uad_callFillHalfEllipsoid(StructureWorldAccess worldIn, BlockBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockstateIn, boolean excludeAir);

    @Invoker("fillWithOutline")
    void uad_callFillWithOutline(StructureWorldAccess worldIn, BlockBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, BlockState boundaryBlockState, BlockState insideBlockState, boolean existingOnly);

    @Invoker("addBlock")
    void uad_callAddBlock(StructureWorldAccess worldIn, BlockState blockstateIn, int x, int y, int z, BlockBox boundingboxIn);
}
