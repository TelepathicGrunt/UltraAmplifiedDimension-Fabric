package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import com.telepathicgrunt.ultraamplifieddimension.world.structures.OceanMonumentPiecesUtils;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(OceanMonumentGenerator.Base.class)
public abstract class OceanMonumentPiecesMonumentBuildingMixin {

    /**
     * Prevents the water carving of monuments
     * @author TelepathicGrunt
     * @reason Make Ocean Monuments not turn everything around them into water in Ultra Amplified Dimension.
     */
    @ModifyVariable(
            method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At(value = "STORE", ordinal = 0), ordinal = 0
    )
    private int uad_noWater(int i, StructureWorldAccess world, StructureAccessor structureManager, ChunkGenerator chunkGenerator, Random random, BlockBox mutableBoundingBox) {
        if(chunkGenerator instanceof UADChunkGenerator){
            // Places water correctly that conforms to the monument's shape
            OceanMonumentPiecesUtils.generateWaterBox(world, chunkGenerator, ((OceanMonumentGenerator.Base)(Object)this), mutableBoundingBox);

            // This causes the normal ridiculous makeOpening to not place any water as it's min y passed in is 0.
            return -1;
        }

        return i;
    }
}