package com.telepathicgrunt.ultraamplifieddimension.capabilities;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;


@EventBusSubscriber(modid = UltraAmplifiedDimension.MODID, bus = EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler
{
	public static final Identifier PLAYER_PAST_POS_AND_DIM = new Identifier(UltraAmplifiedDimension.MODID, "player_past_pos_and_dim");


	@SubscribeEvent
	public static void onAttachCapabilitiesToEntities(AttachCapabilitiesEvent<Entity> e)
	{
		Entity ent = e.getObject();
		if (ent instanceof PlayerEntity)
		{
			e.addCapability(PLAYER_PAST_POS_AND_DIM, new PastPosAndDimProvider());
		}
	}
}