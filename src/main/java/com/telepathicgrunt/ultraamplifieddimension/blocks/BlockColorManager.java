package com.telepathicgrunt.ultraamplifieddimension.blocks;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;


@Mod.EventBusSubscriber(modid = UltraAmplifiedDimension.MODID, bus = Bus.MOD, value = { Dist.CLIENT })
public class BlockColorManager
{

	@Mod.EventBusSubscriber(modid = UltraAmplifiedDimension.MODID, bus = Bus.MOD, value = { Dist.CLIENT })
	private static class ForgeEvents
	{
		/**
		 * Register the {@link IBlockColor} handlers.
		 */
		@SubscribeEvent
		public static void onBlockColorsInit(ColorHandlerEvent.Block event)
		{
			final BlockColors blockColors = event.getBlockColors();

			//registers the colors for blocks that changes colors based on biome
			blockColors.registerColorProvider((unknown1, lightReader, pos, unknown2) ->
			{
				return lightReader != null && pos != null ? BiomeColors.getGrassColor(lightReader, pos) : GrassColors.getColor(0.5D, 1.0D);
			}, UADBlocks.GLOWGRASS_BLOCK);
		}


		/**
		 * Register the {@link IItemColor} handlers
		 */
		@SubscribeEvent
		public static void onItemColorsInit(ColorHandlerEvent.Item event)
		{
			final BlockColors blockColors = event.getBlockColors();
			final ItemColors itemColors = event.getItemColors();

			// Use the Block's colour handler for an ItemBlock
			final ItemColorProvider itemBlockColourHandler = (stack, tintIndex) ->
			{
				final BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
				return blockColors.getColor(state, null, null, tintIndex);
			};

			itemColors.register(itemBlockColourHandler, UADBlocks.GLOWGRASS_BLOCK.get());
		}
	}
}