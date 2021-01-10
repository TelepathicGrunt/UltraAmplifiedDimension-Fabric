package com.telepathicgrunt.ultraamplifieddimension.cardinalcomponents;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface IPlayerComponent extends Component {
    boolean getIsTeleporting();
    void setIsTeleporting(boolean isTeleporting);

    RegistryKey<World> getNonUADimension();
    void setNonUADimension(RegistryKey<World>  nonBZDimension);

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
}