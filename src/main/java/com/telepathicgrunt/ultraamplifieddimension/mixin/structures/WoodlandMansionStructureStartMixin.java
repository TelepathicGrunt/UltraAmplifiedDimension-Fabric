package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
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
            method = "init(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;Lnet/minecraft/world/HeightLimitView;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;getHeightInGround(IILnet/minecraft/world/Heightmap$Type;Lnet/minecraft/world/HeightLimitView;)I", ordinal = 0), ordinal = 0
    )
    private int uad_fixedYHeightForUAD(int i2, DynamicRegistryManager dynamicRegistries, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        if(chunkGenerator instanceof UADChunkGenerator){
            return Math.min(i2, heightLimitView.getTopY() - 40);
        }
        return i2;
    }
}