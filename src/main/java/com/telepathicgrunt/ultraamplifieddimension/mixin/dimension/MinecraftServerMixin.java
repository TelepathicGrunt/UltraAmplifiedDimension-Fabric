package com.telepathicgrunt.ultraamplifieddimension.mixin.dimension;

import com.telepathicgrunt.ultraamplifieddimension.dimension.UADWorldSavedData;
import com.telepathicgrunt.ultraamplifieddimension.utils.BiomeSetsHelper;
import com.telepathicgrunt.ultraamplifieddimension.utils.WorldSeedHolder;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.CaveCavityCarver;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Final
	@Shadow
	protected DynamicRegistryManager.Impl registryManager;

	/**
	 * Used for setting up everything that needs to be initialized for UA worldgen.
	 * Also, to get the seed that the world is using.
	 * @author TelepathicGrunt
	 */
	@Inject(method = "createWorlds(Lnet/minecraft/server/WorldGenerationProgressListener;)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/source/BiomeAccess;hashSeed(J)J"),
		locals = LocalCapture.CAPTURE_FAILHARD)
	private void seedCarvers(WorldGenerationProgressListener chunkStatusListener, CallbackInfo ci, ServerWorldProperties iserverworldinfo,
									GeneratorOptions dimensiongeneratorsettings, boolean isDebugWorld, long seed, long hashedSeed)
	{
		WorldSeedHolder.setWorldSeed(hashedSeed);
		MutableRegistry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
		CaveCavityCarver.setSeed(hashedSeed);
		BiomeSetsHelper.generateBiomeSets(biomeRegistry);
	}
}