package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder.*;
import com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder.configs.QuadrarySurfaceBuilderConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.function.Supplier;


public class UADSurfaceBuilders
{
	public static void init(){}

	public static final SurfaceBuilder<TernarySurfaceConfig> BADLANDS_SURFACE_BUILDER = createSurfaceBuilder("badlands_surface_builder", () -> new BadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> DEEP_OCEAN_SURFACE_BUILDER = createSurfaceBuilder("deep_ocean_surface_builder", () -> new DeepOceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> DESERT_LAKE_SURFACE_BUILDER = createSurfaceBuilder("desert_lake_surface_builder", () -> new DesertLakesSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> DISSECTED_BADLANDS_SURFACE_BUILDER = createSurfaceBuilder("dissected_badlands_surface_builder", () -> new DissectedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> SPIKY_BADLANDS = createSurfaceBuilder("spiky_badlands_surface_builder", () -> new SpikyBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> ICED_TERRAIN_SURFACE_BUILDER = createSurfaceBuilder("iced_terrain_surface_builder", () -> new IcedTerrainSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> NETHER_WASTES_SURFACE_BUILDER = createSurfaceBuilder("nether_surface_builder", () -> new NetherwastesSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> GRASSY_END_SURFACE_BUILDER = createSurfaceBuilder("grassy_end_surface_builder", () -> new GrassyEndSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> OCEAN_SURFACE_BUILDER = createSurfaceBuilder("ocean_surface_builder", () -> new OceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> SAND_SURFACE_BUILDER = createSurfaceBuilder("sand_surface_builder", () -> new SandSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> GRAVEL_SURFACE_BUILDER = createSurfaceBuilder("gravel_surface_builder", () -> new GravelSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> MOUNTAINS_MUTATED_SURFACE_BUILDER = createSurfaceBuilder("mountains_mutated_surface_builder", () -> new MountainsMutatedSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<QuadrarySurfaceBuilderConfig> PLATEAU_SURFACE_BUILDER = createSurfaceBuilder("plateau_surface_builder", () -> new PlateauSurfaceBuilder(QuadrarySurfaceBuilderConfig.CODEC));
	public static final SurfaceBuilder<QuadrarySurfaceBuilderConfig> SHATTERED_SAVANNA_SURFACE_BUILDER = createSurfaceBuilder("shattered_savanna_surface_builder", () -> new ShatteredSavannaSurfaceBuilder(QuadrarySurfaceBuilderConfig.CODEC));

	public static <D extends SurfaceBuilder<?>> D createSurfaceBuilder(String name, Supplier<? extends D> surfacebuilder) {
		return Registry.register(Registry.SURFACE_BUILDER, new Identifier(UltraAmplifiedDimension.MODID, name), surfacebuilder.get());
	}

	public static final TernarySurfaceConfig SAND_SANDSTONE_SANDSTONE_SURFACE = new TernarySurfaceConfig(Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState());
	public static final QuadrarySurfaceBuilderConfig COARSE_DIRT_COARSE_DIRT_GRAVEL_DIRT_SURFACE = new QuadrarySurfaceBuilderConfig(Blocks.COARSE_DIRT.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.DIRT.getDefaultState());
	public static final QuadrarySurfaceBuilderConfig GRASS_BLOCK_DIRT_GRAVEL_COARSE_DIRT_SURFACE = new QuadrarySurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState());
}
