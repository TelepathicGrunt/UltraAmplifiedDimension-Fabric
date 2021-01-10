package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.OceanStructurePiecesUtils;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(OceanRuinGenerator.Piece.class)
public abstract class OceanRuinPiecesPieceMixin {

    /**
     * @author TelepathicGrunt
     * @reason Make Ocean Ruins be placed at various heights under ledges as well in Ultra Amplified Dimension.
     */
    @ModifyVariable(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "STORE", ordinal = 0), ordinal = 0
    )
    private int fixedYHeightForUAD(int i, StructureWorldAccess world, StructureAccessor structureManager, ChunkGenerator chunkGenerator, Random random) {
        if (chunkGenerator instanceof UADChunkGenerator) {
            if(i > 100 && random.nextFloat() > 0.25f){
                OceanRuinGenerator.Piece piece = ((OceanRuinGenerator.Piece)(Object)this);
                return OceanStructurePiecesUtils.getNewLedgeHeight(world, chunkGenerator, random, ((TemplateStructurePieceAccessor)piece).getStructure(), ((OceanRuinPiecesPieceAccessor)piece).getRotation(), ((TemplateStructurePieceAccessor)piece).getPos());
            }
            return i;
        }
        return i;
    }
}