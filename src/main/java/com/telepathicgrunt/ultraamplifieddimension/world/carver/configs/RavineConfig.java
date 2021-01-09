package com.telepathicgrunt.ultraamplifieddimension.world.carver.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RavineConfig implements CarverConfig, FeatureConfig {

    public static final Codec<RavineConfig> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((config) -> config.probability),
            UniformIntDistribution.createValidatedCodec(0, 255, 255).fieldOf("height_placement").forGetter((config) -> config.heightPlacement),
            Codec.INT.fieldOf("cutoff_height").forGetter((config) -> config.cutoffHeight),
            UniformIntDistribution.createValidatedCodec(0, 255, 255).fieldOf("tallness").forGetter((config) -> config.tallness)
    ).apply(builder, RavineConfig::new));

    public final float probability;
    public final UniformIntDistribution heightPlacement;
    public final int cutoffHeight;
    public final UniformIntDistribution tallness;

    public RavineConfig(float probability, UniformIntDistribution heightPlacement, int cutoffHeight, UniformIntDistribution tallness) {
        this.probability = probability;
        this.heightPlacement = heightPlacement;
        this.cutoffHeight = cutoffHeight;
        this.tallness = tallness;
    }
}