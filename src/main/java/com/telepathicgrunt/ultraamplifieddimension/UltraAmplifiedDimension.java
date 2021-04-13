package com.telepathicgrunt.ultraamplifieddimension;

import com.telepathicgrunt.ultraamplifieddimension.blocks.AmplifiedPortalBlock;
import com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents.IPlayerComponent;
import com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents.PlayerComponent;
import com.telepathicgrunt.ultraamplifieddimension.configs.UADConfig;
import com.telepathicgrunt.ultraamplifieddimension.dimension.AmplifiedPortalCreation;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADWorldSavedData;
import com.telepathicgrunt.ultraamplifieddimension.modInit.*;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UltraAmplifiedDimension implements ModInitializer, EntityComponentInitializer {
	public static final String MODID = "ultra_amplified_dimension";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ComponentKey<IPlayerComponent> PLAYER_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MODID, "player_component"), IPlayerComponent.class);

	public static UADConfig UAD_CONFIG = null;

	@Override
	public void onInitialize() {
		AutoConfig.register(UADConfig.class, JanksonConfigSerializer::new);
		UAD_CONFIG = AutoConfig.getConfigHolder(UADConfig.class).getConfig();

		UADTags.tagInit();

		UADBlocks.init();
		UADCarvers.init();
		UADTreeDecoratorTypes.init();
		UADFeatures.init();
		UADStructures.init();
		UADPlacements.init();
		UADSurfaceBuilders.init();
		UADProcessors.registerProcessors();

		PlayerBlockBreakEvents.BEFORE.register(AmplifiedPortalBlock::removedByPlayer);
		UseBlockCallback.EVENT.register(AmplifiedPortalCreation::PortalCreationRightClick);
		ServerTickEvents.END_WORLD_TICK.register(UADWorldSavedData::tick);

		UADDimension.setupDimension();
		UADStructures.setupStructures();
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		//attach component to player
		registry.registerForPlayers(PLAYER_COMPONENT, p -> new PlayerComponent(), RespawnCopyStrategy.INVENTORY);
	}
}
