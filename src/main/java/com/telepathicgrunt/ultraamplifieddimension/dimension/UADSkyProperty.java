package com.telepathicgrunt.ultraamplifieddimension.dimension;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class UADSkyProperty extends SkyProperties {
    public UADSkyProperty() {
        super(UltraAmplifiedDimension.UADimensionConfig.cloudHeight.get(), true, SkyType.NORMAL, false, false);
    }

    @Override
    // thick fog or no
    public boolean useThickFog(int camX, int camY) {
        return UltraAmplifiedDimension.UADimensionConfig.heavyFog.get();
    }


    @Override
    // sky/fog color
    public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
        return adjustFogColorByHeight(color, sunHeight);
    }

    public Vec3d adjustFogColorByHeight(Vec3d color, float celestialAngle) {

        float f = MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        double f1 = color.x;
        double f2 = color.y;
        double f3 = color.z;

        //returns a multiplier between 0 and 1 and will decrease the lower down the player gets from 256
        float multiplierOfBrightness = getHeightBasedModifier();

        f1 = f1 * (f * 0.94F + 0.06F) * multiplierOfBrightness;
        f2 = f2 * (f * 0.94F + 0.06F) * multiplierOfBrightness;
        f3 = f3 * (f * 0.91F + 0.09F) * multiplierOfBrightness;
        return new Vec3d(f1, f2, f3);
    }

    private float getHeightBasedModifier() {
        @SuppressWarnings("resource")
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        float multiplierOfBrightness = 1;
        if(player != null){
            int fullBrightnessFromTop = 56; // brightness of 1 starts at y = 200
            int noBrightnessFromBottom = 90; // brightness of 0 starts at y = 90
            multiplierOfBrightness = (float) ((player.getCameraPosVec(1).y - noBrightnessFromBottom) / Math.max(player.clientWorld.getDimension().getLogicalHeight() - fullBrightnessFromTop - noBrightnessFromBottom, 1));
        }
        return Math.min(Math.max(multiplierOfBrightness, 0), 1);
    }

}
