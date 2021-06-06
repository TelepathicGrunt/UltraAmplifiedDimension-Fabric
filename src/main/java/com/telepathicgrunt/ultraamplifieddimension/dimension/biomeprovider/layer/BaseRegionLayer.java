package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.UADBiomeProvider;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;


public class BaseRegionLayer implements InitLayer {

    public int sample(LayerRandomnessSource noise, int x, int z) {
        double regionNoise = (noise.getNoiseSampler().sample(
                        (double)x / 4.2D,
                        (double)z / 4.2D,
                        0.0D,
                        0.0D,
                        0.0D)
                      * 0.75D) + 0.5D; // -0.25 to 1.25

        if(regionNoise < 0.3D){
            if(noise.nextInt(25) == 0){
                return UADBiomeProvider.REGIONS.NETHER.ordinal();
            }
            return UADBiomeProvider.REGIONS.HOT.ordinal();
        }
        else if(regionNoise < 0.5D){
            return UADBiomeProvider.REGIONS.WARM.ordinal();
        }
        else if(regionNoise < 0.7D){
            if(noise.nextInt(30) == 0){
                return UADBiomeProvider.REGIONS.END.ordinal();
            }
            return UADBiomeProvider.REGIONS.COOL.ordinal();
        }
        else{
            return UADBiomeProvider.REGIONS.ICY.ordinal();
        }
    }
}