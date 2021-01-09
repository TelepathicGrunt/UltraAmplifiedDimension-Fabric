package com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.layer.transformers;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;

public interface CastleWithPositionTransformer extends ParentedLayer, NorthWestCoordinateTransformer {
    int apply(LayerRandomnessSource context, int north, int west, int south, int east, int center, int x, int z);

    default int sample(LayerSampleContext<?> context, LayerSampler area, int x, int z) {
        return this.apply(context, area.sample(this.transformX(x + 1), this.transformZ(z + 0)), area.sample(this.transformX(x + 2), this.transformZ(z + 1)), area.sample(this.transformX(x + 1), this.transformZ(z + 2)), area.sample(this.transformX(x + 0), this.transformZ(z + 1)), area.sample(this.transformX(x + 1), this.transformZ(z + 1)), x, z);
    }
}