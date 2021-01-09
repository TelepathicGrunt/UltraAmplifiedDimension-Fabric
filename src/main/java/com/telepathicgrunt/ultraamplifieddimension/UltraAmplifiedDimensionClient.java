package com.telepathicgrunt.ultraamplifieddimension;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADSkyProperty;
import com.telepathicgrunt.ultraamplifieddimension.mixin.dimension.SkyPropertiesAccessor;
import com.telepathicgrunt.ultraamplifieddimension.modInit.UADBlocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class UltraAmplifiedDimensionClient {
	public static void subscribeClientEvents()
	{
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(UltraAmplifiedDimensionClient::onClientSetup);
	}


	public static void onClientSetup(FMLClientSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			SkyPropertiesAccessor.getfield_239208_a_().put(new Identifier(UltraAmplifiedDimension.MODID, "sky_property"), new UADSkyProperty());

			RenderLayers.setRenderLayer(UADBlocks.GLOWSTONE_ORE.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.GLOWGRASS_BLOCK.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.GLOWMYCELIUM.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.GLOWPODZOL.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.GLOWDIRT.get(), RenderLayer.getCutout());
			RenderLayers.setRenderLayer(UADBlocks.COARSE_GLOWDIRT.get(), RenderLayer.getCutout());
			RenderLayers.setRenderLayer(UADBlocks.GLOWSAND.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.RED_GLOWSAND.get(), RenderLayer.getTranslucent());
			RenderLayers.setRenderLayer(UADBlocks.AMPLIFIED_PORTAL.get(), RenderLayer.getTranslucent());
		});
	}
}
