package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/structure/OceanMonumentGenerator$Piece")
public abstract class OceanMonumentPiecesPieceMixin {

    @Inject(
            method = "setAirAndWater(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIII)V",
            at = @At(value = "HEAD"), cancellable = true
    )
    private void uad_noWater(StructureWorldAccess world, BlockBox boundingBoxIn, int x1, int y1, int z1, int x2, int y2, int z2, CallbackInfo ci) {
        if(world.toServerWorld().getChunkManager().getChunkGenerator() instanceof UADChunkGenerator){
            if(Math.abs(x1 - x2) > 6 || Math.abs(y1 - y2) > 6 || Math.abs(z1 - z2) > 6){
                // prevent the terrain carving with liquids everywhere
                ci.cancel();
            }
        }
    }
}