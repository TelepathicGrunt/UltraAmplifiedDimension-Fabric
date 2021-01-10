package com.telepathicgrunt.ultraamplifieddimension.dimension;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.dimension.biomeprovider.UADBiomeProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class UADDimension {
    public static final RegistryKey<World> UAD_WORLD_KEY = RegistryKey.of(Registry.DIMENSION, new Identifier(UltraAmplifiedDimension.MODID, UltraAmplifiedDimension.MODID));

    public static void setupDimension() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(UltraAmplifiedDimension.MODID, "terrain"), UADChunkGenerator.UAD_CHUNK_GENERATOR_CODEC);
        Registry.register(Registry.BIOME_SOURCE, new Identifier(UltraAmplifiedDimension.MODID, "biome_source"), UADBiomeProvider.CODEC);
    }
}
