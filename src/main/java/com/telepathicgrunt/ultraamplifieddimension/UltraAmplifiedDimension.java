package com.telepathicgrunt.ultraamplifieddimension;

import com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents.IPlayerComponent;
import com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents.PlayerComponent;
import com.telepathicgrunt.ultraamplifieddimension.configs.UADimensionConfig.UADimensionConfigValues;
import com.telepathicgrunt.ultraamplifieddimension.dimension.AmplifiedPortalCreation;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADDimension;
import com.telepathicgrunt.ultraamplifieddimension.modInit.*;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UltraAmplifiedDimension implements ModInitializer, EntityComponentInitializer {
	public static final String MODID = "ultra_amplified_dimension";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ComponentKey<IPlayerComponent> PLAYER_COMPONENT =
			ComponentRegistry.getOrCreate(new Identifier(MODID, "player_component"), IPlayerComponent.class);

	public static UADimensionConfigValues UADimensionConfig = null;
	//public static UAModCompatConfigValues UAModCompatConfig = null;

	//public static WBConfig WB_CONFIG;
	//WB_CONFIG = AutoConfig.getConfigHolder(WBConfig.class).getConfig();
	//UseBlockCallback.EVENT.register(WBPortalSpawning::blockRightClick);

	@Override
	public void onInitialize() {
		UADBlocks.ITEMS.register(modEventBus);
		UADBlocks.BLOCKS.register(modEventBus);
		UADFeatures.FEATURES.register(modEventBus);
		UADStructures.STRUCTURES.register(modEventBus);
		UADPlacements.DECORATORS.register(modEventBus);
		UADCarvers.WORLD_CARVERS.register(modEventBus);
		UADSurfaceBuilders.SURFACE_BUILDERS.register(modEventBus);
		UADTreeDecoratorTypes.TREE_DECORATOR_TYPES.register(modEventBus);
		UADTags.tagInit();

		forgeBus.addListener(EventPriority.NORMAL, UADDimension::worldTick);
		forgeBus.addListener(EventPriority.NORMAL, AmplifiedPortalCreation::PortalCreationRightClick);

		//generates config
		UADimensionConfig = ConfigHelper.register(ModConfig.Type.SERVER, UADimensionConfigValues::new, "ultra_amplified_dimension-dimension.toml");
		//UAModCompatConfig = ConfigHelper.register(ModConfig.Type.SERVER, UAModCompatConfigValues::new, "ultra_amplified_dimension-mod_compat.toml");

		UADDimension.setupDimension();
		UADStructures.setupStructures();
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		//attach component to player
		registry.registerForPlayers(PLAYER_COMPONENT, p -> new PlayerComponent(), RespawnCopyStrategy.INVENTORY);
	}
}
