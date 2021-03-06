package com.telepathicgrunt.ultraamplifieddimension.world.decorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.decorator.DecoratorConfig;


public class LedgeSurfacePlacerConfig implements DecoratorConfig {
    public static final Codec<LedgeSurfacePlacerConfig> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("column_passes").orElse(0).forGetter((config) -> config.columnCount),
            Codec.floatRange(0, 1).fieldOf("valid_spot_chance").orElse(1F).forGetter((config) -> config.validSpotChance),
            Codec.BOOL.fieldOf("skip_top_ledge").orElse(false).forGetter((config) -> config.skipTopLedge),
            Codec.BOOL.fieldOf("underside_only").orElse(false).forGetter((config) -> config.undersideOnly),
            Codec.BOOL.fieldOf("water_pos_only").orElse(false).forGetter((config) -> config.undersideOnly))
                .apply(builder, LedgeSurfacePlacerConfig::new));

    public final int columnCount;
    public final float validSpotChance;
    public final boolean skipTopLedge;
    public final boolean undersideOnly;
    public final boolean waterPosOnly;

    public LedgeSurfacePlacerConfig(int columnCount, float validSpotChance, boolean skipTopLedge, boolean undersideOnly, boolean waterPosOnly) {
        this.columnCount = columnCount;
        this.validSpotChance = validSpotChance;
        this.skipTopLedge = skipTopLedge;
        this.undersideOnly = undersideOnly;
        this.waterPosOnly = waterPosOnly;
    }
}