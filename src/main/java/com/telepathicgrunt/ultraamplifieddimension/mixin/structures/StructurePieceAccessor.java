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
    @Invoker
    BlockState callGetBlockAt(BlockView worldIn, int x, int y, int z, BlockBox boundingboxIn);

    @Invoker
    int callApplyXTransform(int x, int z);

    @Invoker
    int callApplyYTransform(int y);

    @Invoker
    int callApplyZTransform(int x, int z);

    @Invoker
    void callFillHalfEllipsoid(StructureWorldAccess worldIn, BlockBox boundingboxIn, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState blockstateIn, boolean excludeAir);

    @Invoker
    void callFillWithOutline(StructureWorldAccess worldIn, BlockBox boundingboxIn, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, BlockState boundaryBlockState, BlockState insideBlockState, boolean existingOnly);

    @Invoker
    void callAddBlock(StructureWorldAccess worldIn, BlockState blockstateIn, int x, int y, int z, BlockBox boundingboxIn);
}
