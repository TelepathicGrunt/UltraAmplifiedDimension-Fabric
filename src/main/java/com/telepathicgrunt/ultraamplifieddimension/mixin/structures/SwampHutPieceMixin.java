package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SwampHutGenerator.class)
public abstract class SwampHutPieceMixin {
    
    /**
     * @author TelepathicGrunt
     * @reason Place Witch Huts on top of land properly
     */
    @Inject(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(target = "Lnet/minecraft/structure/SwampHutGenerator;fillWithOutline(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V",
                    value = "INVOKE", ordinal = 0)
    )
    private void uad_fixedYHeightForUAD(StructureWorldAccess world, StructureAccessor structureManager, ChunkGenerator chunkGenerator, Random random, BlockBox mutableBoundingBox, ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(chunkGenerator instanceof UADChunkGenerator){
            BlockBox box = ((SwampHutGenerator)(Object)this).getBoundingBox();
            ((SwampHutGenerator)(Object)this).getBoundingBox().move(0, chunkGenerator.getHeight(box.getMinX() + (box.getBlockCountX() / 2), box.getMinZ() + (box.getBlockCountZ() / 2), Heightmap.Type.WORLD_SURFACE_WG, world) - box.getMinY() + 2, 0);
        }
    }
}