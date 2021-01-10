package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
    @Accessor("populationSource")
    BiomeSource getbiomeProvider();

    @Mutable
    @Accessor
    void setWorldSeed(long worldSeed);
}