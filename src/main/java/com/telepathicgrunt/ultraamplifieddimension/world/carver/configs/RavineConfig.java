package com.telepathicgrunt.ultraamplifieddimension.world.carver.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class RavineConfig extends CarverConfig implements FeatureConfig {

    public static final Codec<RavineConfig> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            CarverConfig.CONFIG_CODEC.forGetter((config) -> config),
            Codec.INT.fieldOf("cutoff_height").forGetter((config) -> config.cutoffHeight),
            UniformHeightProvider.UNIFORM_CODEC.fieldOf("tallness").forGetter((config) -> config.tallness)
    ).apply(builder, RavineConfig::new));

    public final int cutoffHeight;
    public final UniformHeightProvider tallness;

    public RavineConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, boolean aquifers, CarverDebugConfig debugConfig, int cutoffHeight, UniformHeightProvider tallness) {
        super(probability, y, yScale, lavaLevel, aquifers, debugConfig);
        this.cutoffHeight = cutoffHeight;
        this.tallness = tallness;
    }

    public RavineConfig(CarverConfig config, int cutoffHeight, UniformHeightProvider tallness) {
        this(config.probability, config.y, config.yScale, config.lavaLevel, config.aquifers, config.debugConfig, cutoffHeight, tallness);
    }
}