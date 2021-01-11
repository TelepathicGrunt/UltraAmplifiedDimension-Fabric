package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShipwreckGenerator.Piece.class)
public interface ShipwreckPiecesPieceAccessor {
    @Accessor("rotation")
    BlockRotation uad_getRotation();
}
