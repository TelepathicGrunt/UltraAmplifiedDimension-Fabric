package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer;

import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.RegionManager;
import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer.transformers.CastleWithPositionTransformer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;


public class TestBiomeLayer implements CastleWithPositionTransformer {
    private final Registry<Biome> dynamicRegistry;
    private final RegionManager regionManager;

    public TestBiomeLayer(Registry<Biome> dynamicRegistry, RegionManager regionManager){
        this.dynamicRegistry = dynamicRegistry;
        this.regionManager = regionManager;
    }

    public int apply(LayerRandomnessSource noise, int north, int west, int south, int east, int biomeID, int x, int z) {




        double hillNoise = (noise.getNoiseSampler().sample(
                (double)x / 3.0D,
                (double)z / 3.0D,
                54670.0D,
                0.0D,
                0.0D)
                * 0.5D) + 0.5D; // -0 to 1

        return 0;
    }
}