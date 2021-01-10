package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.CaveCavityCarver;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.RavineCarver;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.SuperLongRavineCarver;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.UnderwaterCaveCarver;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.CaveConfig;
import com.telepathicgrunt.ultraamplifieddimension.world.carver.configs.RavineConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

import java.util.function.Supplier;


public class UADCarvers
{
	public static void init(){}

	public static final Carver<?> RAVINE_CARVER = createCarver("ravine", () -> new RavineCarver(RavineConfig.CODEC));
	public static final Carver<?> LONG_RAVINE_CARVER = createCarver("long_ravine", () -> new SuperLongRavineCarver(RavineConfig.CODEC));
	public static final Carver<?> CAVE_CAVITY_CARVER = createCarver("cave_cavity", () -> new CaveCavityCarver(CaveConfig.CODEC));
	public static final Carver<?> UNDERWATER_CAVE_CARVER = createCarver("underwater_cave", () -> new UnderwaterCaveCarver(ProbabilityConfig.CODEC));

	public static Carver<?> createCarver(String name, Supplier<Carver<?>> carver) {
		return Registry.register(Registry.CARVER, new Identifier(UltraAmplifiedDimension.MODID, name), carver.get());
	}
}
