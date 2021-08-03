package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.utils.WorldSeedHolder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

@Mixin(BeehiveTreeDecorator.class)
public class BeehiveTreeDecoratorMixin {

	/**
	 * Stops crash with vanilla trees in UAD caused by Beehive decorator being fed an empty log set when it doesn't check for empty logs.
	 * @author TelepathicGrunt
	 * @reason Prevent crash as beehive decorator only checks if leaves set is empty when it needs both leaves and logs.
	 */
	@Inject(method = "generate(Lnet/minecraft/world/TestableWorld;Ljava/util/function/BiConsumer;Ljava/util/Random;Ljava/util/List;Ljava/util/List;)V",
			at = @At(value = "HEAD"), cancellable = true)
	private void uad_stopBeehiveTreeCrash(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, CallbackInfo ci)
	{
		if(logPositions.isEmpty()) {
			//if(!leavesPositions.isEmpty()) UltraAmplifiedDimension.LOGGER.warn("Empty log set fed to beehive decorator at: {}", leavesPositions.get(0));
			ci.cancel();
		}
	}
}