package com.telepathicgrunt.ultraamplifieddimension.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;


public interface IPlayerPosAndDim
{

	//what methods the capability will have and what the capability is

	void setNonUADim(RegistryKey<World> incomingDim);
	RegistryKey<World> getNonUADim();
	
	void setNonUAPos(Vec3d incomingPos);
	Vec3d getNonUAPos();
	
	void setNonUAPitch(float incomingPitch);
	float getNonUAPitch();

	void setNonUAYaw(float incomingYaw);
	float getNonUAYaw();

	void setUAPos(Vec3d incomingPos);
	Vec3d getUAPos();

	void setUAPitch(float incomingPitch);
	float getUAPitch();

	void setUAYaw(float incomingYaw);
	float getUAYaw();

	CompoundTag saveNBTData();
	void loadNBTData(CompoundTag nbtTag);
}
