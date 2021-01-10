package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer;

import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.UADBiomeProvider;
import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer.transformers.CenterWithPositionTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;


public class OceanBaseRegionLayer implements CenterWithPositionTransformer {

    public int apply(LayerRandomnessSource noise, int center, int x, int z) {
        double oceanThresholdNoise = (noise.getNoiseSampler().sample(
                        (double)x / 6.0D,
                        (double)z / 6.0D,
                        12361.0D,
                        0.0D,
                        0.0D)
                     + 1) * 0.5D;

        // Use a separate noise to make ocean spots separate from the temperature noise
        if(oceanThresholdNoise < 0.27D){
            return UADBiomeProvider.REGIONS.OCEAN.ordinal();
        }

        return center;
    }
}