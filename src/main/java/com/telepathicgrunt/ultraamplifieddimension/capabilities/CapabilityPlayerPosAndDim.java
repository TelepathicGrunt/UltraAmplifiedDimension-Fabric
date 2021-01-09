package com.telepathicgrunt.ultraamplifieddimension.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;


public class CapabilityPlayerPosAndDim
{
	//the capability itself
	@CapabilityInject(IPlayerPosAndDim.class)
	public static Capability<IPlayerPosAndDim> PAST_POS_AND_DIM = null;


	//registers the capability and defines how it will read/write data from nbt
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IPlayerPosAndDim.class, new Capability.IStorage<IPlayerPosAndDim>()
		{
			@Override
			@Nullable
			public Tag writeNBT(Capability<IPlayerPosAndDim> capability, IPlayerPosAndDim instance, Direction side)
			{
				return instance.saveNBTData();
			}


			@Override
			public void readNBT(Capability<IPlayerPosAndDim> capability, IPlayerPosAndDim instance, Direction side, Tag nbt)
			{
				instance.loadNBTData((CompoundTag) nbt);
			}
		}, PlayerPositionAndDimension::new);
	}
}
