package com.telepathicgrunt.ultraamplifieddimension.world.features.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class LootTableConfig implements FeatureConfig {
    public static final Codec<LootTableConfig> CODEC = RecordCodecBuilder.create((cactusConfigInstance) -> cactusConfigInstance.group(
            Identifier.CODEC.fieldOf("loot_table").forGetter((config) -> config.lootTable)
    ).apply(cactusConfigInstance, LootTableConfig::new));

    public final Identifier lootTable;

    public LootTableConfig(Identifier lootTable) {
        this.lootTable = lootTable;
    }
}
