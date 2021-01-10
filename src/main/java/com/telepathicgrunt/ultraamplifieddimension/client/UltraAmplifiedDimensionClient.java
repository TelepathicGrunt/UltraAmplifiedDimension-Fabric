package com.telepathicgrunt.ultraamplifieddimension.client;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADSkyProperty;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.SkyPropertiesAccessor;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class UltraAmplifiedDimensionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient()
	{
		SkyPropertiesAccessor.getfield_239208_a_().put(new Identifier(UltraAmplifiedDimension.MODID, "sky_property"), new UADSkyProperty());

		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWSTONE_ORE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWGRASS_BLOCK, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWMYCELIUM, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWPODZOL, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWDIRT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.COARSE_GLOWDIRT, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.GLOWSAND, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.RED_GLOWSAND, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(UADBlocks.AMPLIFIED_PORTAL, RenderLayer.getTranslucent());

		ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> view != null && pos != null ? BiomeColors.getGrassColor(view, pos) : GrassColors.getColor(0.5D, 1.0D), UADBlocks.GLOWGRASS_BLOCK);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D), UADBlocks.GLOWGRASS_BLOCK);
	}
}
