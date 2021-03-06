package com.telepathicgrunt.ultraamplifieddimension.mixin.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpreadableBlock.class)
public interface SpreadableSnowyDirtBlockAccessor {
    @Invoker("canSpread")
    static boolean uad_callCanSpread(BlockState state, WorldView worldReader, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Invoker("canSurvive")
    static boolean uad_callCanSurvive(BlockState state, WorldView worldReader, BlockPos pos) {
        throw new UnsupportedOperationException();
    }
}
