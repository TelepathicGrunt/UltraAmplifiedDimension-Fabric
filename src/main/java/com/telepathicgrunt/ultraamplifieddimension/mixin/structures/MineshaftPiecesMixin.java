package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.MineshaftPiecesUtils;
import net.minecraft.structure.MineshaftGenerator;
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

@Mixin(MineshaftGenerator.MineshaftRoom.class)
public abstract class MineshaftPiecesMixin {

    /**
     * @author TelepathicGrunt
     * @reason Creates giant Mineshaft pit room and updates all fluids within the giant room Mineshafts in Ultra Amplified Dimension
     */
    @Inject(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "TAIL")
    )
    private void giantRoom(StructureWorldAccess world, StructureAccessor structureManager, ChunkGenerator chunkGenerator, Random random, BlockBox mutableBoundingBox, ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (chunkGenerator instanceof UADChunkGenerator) {
            if(random.nextFloat() < 0.25f){
                MineshaftPiecesUtils.generateLargeRoom(world, ((MineshaftGenerator.MineshaftRoom)(Object)this), mutableBoundingBox);
            }
            else{
                MineshaftPiecesUtils.generateFloorRoom(world, ((MineshaftGenerator.MineshaftRoom)(Object)this), mutableBoundingBox);
            }
        }
    }
}