package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeArray.class)
public interface BiomeContainerAccessor {
    @Accessor
    IndexedIterable<Biome> getField_25831();
}
