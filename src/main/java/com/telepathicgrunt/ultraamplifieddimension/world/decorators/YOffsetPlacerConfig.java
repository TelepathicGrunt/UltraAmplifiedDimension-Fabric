package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;


public record YOffsetPlacerConfig(int yOffset, int ySpread) implements DecoratorConfig {
    public static final Codec<YOffsetPlacerConfig> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Codec.INT.fieldOf("yoffset").orElse(0).forGetter((config) -> config.yOffset),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("yspread").orElse(0).forGetter((config) -> config.ySpread))
            .apply(builder, YOffsetPlacerConfig::new));

}