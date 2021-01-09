package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer.transformers;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface CenterWithPositionTransformer extends ParentedLayer, NorthWestCoordinateTransformer {
    int apply(LayerRandomnessSource context, int center, int x, int z);

    default int sample(LayerSampleContext<?> context, LayerSampler area, int x, int z) {
        return this.apply(context, area.sample(this.transformX(x + 1), this.transformZ(z + 1)), x, z);
    }
}