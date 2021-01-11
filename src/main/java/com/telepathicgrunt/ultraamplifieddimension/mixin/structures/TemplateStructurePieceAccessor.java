package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleStructurePiece.class)
public interface TemplateStructurePieceAccessor {
    @Accessor("structure")
    Structure uad_getStructure();

    @Accessor("pos")
    BlockPos uad_getPos();

    @Accessor("pos")
    void uad_setPos(BlockPos blockPos);
}
