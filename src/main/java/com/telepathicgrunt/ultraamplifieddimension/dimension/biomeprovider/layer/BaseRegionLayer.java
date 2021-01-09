package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;


public class BaseRegionLayer implements InitLayer {

    /*
     * LAYER KEY FOR MYSELF:
     * 0 = ocean region
     * 1 = end region
     * 2 = nether region
     * 3 = hot region
     * 4 = warm region
     * 5 = cool region
     * 6 = icy region
     */
    public int sample(LayerRandomnessSource noise, int x, int z) {
        double regionNoise = (noise.getNoiseSampler().sample(
                        (double)x / 4.0D,
                        (double)z / 4.0D,
                        0.0D,
                        0.0D,
                        0.0D)
                      * 0.5D) + 0.5D;

        if(regionNoise < 0.3D){
            if(noise.nextInt(25) == 0){
                return 2;
            }
            return 3;
        }
        else if(regionNoise < 0.5D){
            return 4;
        }
        else if(regionNoise < 0.7D){
            if(noise.nextInt(30) == 0){
                return 1;
            }
            return 5;
        }
        else{
            return 6;
        }
    }
}