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

	public static Carver<?> RAVINE_CARVER = null;
	public static Carver<?> LONG_RAVINE_CARVER = null;
	public static Carver<?> CAVE_CAVITY_CARVER = null;
	public static Carver<?> UNDERWATER_CAVE_CARVER = null;

	public static Carver<?> createCarver(String name, Supplier<Carver<?>> carver) {
		return Registry.register(Registry.CARVER, new Identifier(UltraAmplifiedDimension.MODID, name), carver.get());
	}

	public static void init(){
		RAVINE_CARVER = createCarver("ravine", () -> new RavineCarver(RavineConfig.CODEC));
		LONG_RAVINE_CARVER = createCarver("long_ravine", () -> new SuperLongRavineCarver(RavineConfig.CODEC));
		CAVE_CAVITY_CARVER = createCarver("cave_cavity", () -> new CaveCavityCarver(CaveConfig.CODEC));
		UNDERWATER_CAVE_CARVER = createCarver("underwater_cave", () -> new UnderwaterCaveCarver(ProbabilityConfig.CODEC));
	}
}
