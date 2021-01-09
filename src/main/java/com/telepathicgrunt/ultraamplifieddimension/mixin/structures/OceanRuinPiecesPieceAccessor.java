package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OceanRuinGenerator.Piece.class)
public interface OceanRuinPiecesPieceAccessor {
    @Accessor
    BlockRotation getRotation();
}
