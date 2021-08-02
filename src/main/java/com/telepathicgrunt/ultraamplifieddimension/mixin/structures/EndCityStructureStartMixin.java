package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndCityFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndCityFeature.Start.class)
public abstract class EndCityStructureStartMixin {

    /**
     * @author TelepathicGrunt
     * @reason Make End Cities not be placed so high in Ultra Amplified Dimension's dimension and cause the game to die due to out of bounds world deadlock.
     */
    @ModifyVariable(
            method = "init(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;Lnet/minecraft/world/HeightLimitView;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/gen/feature/EndCityFeature;getGenerationHeight(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/HeightLimitView;)I")
    )
    private int uad_fixedYHeightForUAD(int y, DynamicRegistryManager dynamicRegistries, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
        if(chunkGenerator instanceof UADChunkGenerator){
            return heightLimitView.getTopY()-150;
        }
        return y;
    }
}