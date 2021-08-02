package com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.UADDimension;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class PlayerComponent implements IPlayerComponent {
    private boolean teleporting = false;
    private RegistryKey<World> nonUADimension = World.OVERWORLD;
    public Vec3d nonUADPosition = null;
    public float nonUADPitch = 0;
    public float nonUADYaw = 0;
    public Vec3d UADPosition = null;
    public float UADPitch = 0;
    public float UADYaw = 0;

    @Override
    public void setNonUAPos(Vec3d incomingPos)
    {
        nonUADPosition = incomingPos;
    }
    @Override
    public Vec3d getNonUAPos()
    {
        return nonUADPosition;
    }

    @Override
    public RegistryKey<World> getNonUADimension() {
        return this.nonUADimension;
    }

    @Override
    public void setNonUADimension(RegistryKey<World> nonUADimension) {
        if (nonUADimension.equals(UADDimension.UAD_WORLD_KEY)) {
            this.nonUADimension = World.OVERWORLD;
            UltraAmplifiedDimension.LOGGER.log(Level.ERROR, "Error: The non-UA dimension passed in to be stored was UA dimension. Please contact mod creator to let them know of this issue.");
        } else {
            this.nonUADimension = nonUADimension;
        }

    }

    @Override
    public void setNonUAPitch(float incomingPitch){
        nonUADPitch = incomingPitch;
    }

    @Override
    public float getNonUAPitch() {
        return nonUADPitch;
    }


    @Override
    public void setNonUAYaw(float incomingYaw){
        nonUADYaw = incomingYaw;
    }

    @Override
    public float getNonUAYaw(){
        return nonUADYaw;
    }


    @Override
    public void setUAPos(Vec3d incomingPos)
    {
        UADPosition = incomingPos;
    }
    @Override
    public Vec3d getUAPos()
    {
        return UADPosition;
    }

    @Override
    public void setUAPitch(float incomingPitch){
        UADPitch = incomingPitch;
    }

    @Override
    public float getUAPitch() {
        return UADPitch;
    }

    @Override
    public void setUAYaw(float incomingYaw){
        UADYaw = incomingYaw;
    }

    @Override
    public float getUAYaw(){
        return UADYaw;
    }


    @Override
    public void readFromNbt(NbtCompound tag) {
        this.teleporting = tag.getBoolean("teleporting");
        this.nonUADimension = RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.getString("non_ua_dimension_namespace"), tag.getString("non_ua_dmension_path")));
        this.nonUADPitch = tag.getFloat("pitch");
        this.nonUADYaw = tag.getFloat("yaw");
    }
    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("teleporting", this.teleporting);
        tag.putString("non_ua_dimension_namespace", nonUADimension.getValue().getNamespace());
        tag.putString("non_ua_dmension_path", nonUADimension.getValue().getPath());
        tag.putFloat("pitch", this.nonUADPitch);
        tag.putFloat("yaw", this.nonUADYaw);
    }

}