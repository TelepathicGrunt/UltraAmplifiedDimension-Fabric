package com.telepathicgrunt.ultraamplifieddimension.mixin.structures;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADChunkGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherFortressFeature.Start.class)
public abstract class FortressStructureStartMixin {

    /**
     * @author TelepathicGrunt
     * @reason Raise Nether Fortresses in UAD to between y = 88 and 135
     */
    @Inject(
            method = "init(Lnet/minecraft/util/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureManager;IILnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)V",
            at = @At(value = "TAIL")
    )
    private void adjustHeight(DynamicRegistryManager dynamicRegistries, ChunkGenerator chunkGenerator, StructureManager templateManager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig config, CallbackInfo ci) {
        if(chunkGenerator instanceof UADChunkGenerator){
            ((StructureStartAccessor)this).uad_callRandomUpwardTranslation(((StructureStartAccessor)this).uad_getRandom(), 88, 135);
        }
    }
}