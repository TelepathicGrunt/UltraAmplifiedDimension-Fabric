package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.OceanStructurePiecesUtils;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ShipwreckGenerator.Piece.class)
public abstract class ShipwreckPiecesPieceMixin {

    /**
     * @author TelepathicGrunt
     * @reason Make Shipwrecks be placed at various heights under ledges as well in Ultra Amplified Dimension.
     */
    @Inject(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/SimpleStructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private void fixedYHeightForUAD(StructureWorldAccess world, StructureAccessor structureManager, ChunkGenerator chunkGenerator, Random random, BlockBox mutableBoundingBox, ChunkPos chunkPosX, BlockPos chunkPosZ, CallbackInfoReturnable<Boolean> cir) {
        if (chunkGenerator instanceof UADChunkGenerator) {
            ShipwreckGenerator.Piece piece = ((ShipwreckGenerator.Piece)(Object)this);
            int newHeight = OceanStructurePiecesUtils.getNewLedgeHeight(world, chunkGenerator, random, ((TemplateStructurePieceAccessor)piece).getStructure(), ((ShipwreckPiecesPieceAccessor)piece).getRotation(), ((TemplateStructurePieceAccessor)piece).getPos());
            BlockPos oldPos = ((TemplateStructurePieceAccessor)piece).getPos();
            ((TemplateStructurePieceAccessor)piece).setPos(new BlockPos(oldPos.getX(), newHeight, oldPos.getZ()));
        }
    }
}