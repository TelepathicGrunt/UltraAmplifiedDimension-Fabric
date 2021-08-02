package com.telepathicgrunt.ultraamplifieddimension.world.carver.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.carver.RavineCarverConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class CaveConfig extends CarverConfig implements FeatureConfig {

    public static final Codec<CaveConfig> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            CarverConfig.CONFIG_CODEC.forGetter((config) -> config),
            Codec.INT.fieldOf("cutoff_height").forGetter((config) -> config.cutoffHeight)
    ).apply(builder, CaveConfig::new));

    public final int cutoffHeight;

    public CaveConfig(float probability, HeightProvider y, FloatProvider yScale, YOffset lavaLevel, boolean aquifers, CarverDebugConfig debugConfig, int cutoffHeight) {
        super(probability, y, yScale, lavaLevel, aquifers, debugConfig);
        this.cutoffHeight = cutoffHeight;
    }

    public CaveConfig(CarverConfig config, int cutoffHeight) {
        this(config.probability, config.y, config.yScale, config.lavaLevel, config.aquifers, config.debugConfig, cutoffHeight);
    }
}