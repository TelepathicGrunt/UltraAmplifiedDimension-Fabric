package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGeneratorSettings.class)
public interface ChunkGeneratorSettingsInvoker {
    @Invoker("isMobGenerationDisabled")
    boolean uad_callIsMobGenerationDisabled();

    @Invoker("hasAquifers")
    boolean uad_callHasAquifers();

    @Invoker("hasNoiseCaves")
    boolean uad_callHasNoiseCaves();

    @Invoker("hasDeepslate")
    boolean uad_callHasDeepslate();

    @Invoker("hasOreVeins")
    boolean uad_callHasOreVeins();

    @Invoker("hasNoodleCaves")
    boolean uad_callHasNoodleCaves();
}