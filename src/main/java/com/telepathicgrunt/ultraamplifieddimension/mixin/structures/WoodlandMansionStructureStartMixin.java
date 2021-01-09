package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WoodlandMansionFeature.Start.class)
public abstract class WoodlandMansionStructureStartMixin {

    /**
     * @author TelepathicGrunt
     * @reason Make Woodland Mansions not be placed so high in Ultra Amplified Dimension's dimension and cause the game to die due to out of bounds world deadlock.
     */
    @ModifyVariable(
            method = "init(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;IILnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)V",
            at = @At(value = "STORE", ordinal = 0), ordinal = 10
    )
    private int fixedYHeightForUAD(int i2, DynamicRegistryManager dynamicRegistries, ChunkGenerator chunkGenerator) {
        if(chunkGenerator instanceof UADChunkGenerator){
            return Math.min(i2, 215);
        }
        return i2;
    }
}