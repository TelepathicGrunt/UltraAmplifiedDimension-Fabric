package com.telepathicgrunt.ultraamplifieddimension.mixin.surfacebuilders;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.NetherSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Random;

@Mixin(NetherSurfaceBuilder.class)
public class NetherSurfaceBuilderMixin {

	/**
	 * Allow vanilla nether surfacebuilders to work at any height for any dimension including my own.
	 *
	 * @author TelepathicGrunt
	 * @reason We do this to maximize mod compat with vanilla and modded nether biomes as best as we can.
 	 */
	@ModifyConstant(method = "generate(Ljava/util/Random;Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/biome/Biome;IIIDLnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;IJLnet/minecraft/world/gen/surfacebuilder/TernarySurfaceConfig;)V",
			constant = @Constant(intValue = 127))
	private int buildSurfaceAnyHeight(int constant, Random random, Chunk chunkIn, Biome biomeIn, int x, int z, int startHeight)
	{
		return startHeight;
	}
}