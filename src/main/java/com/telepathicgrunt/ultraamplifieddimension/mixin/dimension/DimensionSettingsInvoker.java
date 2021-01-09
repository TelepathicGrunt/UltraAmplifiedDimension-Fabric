package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.GenerationShapeConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkGeneratorSettings.class)
public interface DimensionSettingsInvoker {

    @Invoker("<init>")
    static ChunkGeneratorSettings invokeinit(StructuresConfig structures, GenerationShapeConfig noise, BlockState defaultBlock, BlockState defaultFluid, int p_i231905_5_, int p_i231905_6_, int p_i231905_7_, boolean p_i231905_8_) {
        throw new UnsupportedOperationException();
    }

    @Invoker("isMobGenerationDisabled")
    boolean invokefunc_236120_h_();
}