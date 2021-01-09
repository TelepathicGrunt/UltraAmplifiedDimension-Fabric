package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder.*;
import com.telepathicgrunt.ultraamplifieddimension.world.surfacebuilder.configs.QuadrarySurfaceBuilderConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;


public class UADSurfaceBuilders
{
	public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, UltraAmplifiedDimension.MODID);

	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> BADLANDS_SURFACE_BUILDER = createDecorator("badlands_surface_builder", () -> new BadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> DEEP_OCEAN_SURFACE_BUILDER = createDecorator("deep_ocean_surface_builder", () -> new DeepOceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> DESERT_LAKE_SURFACE_BUILDER = createDecorator("desert_lake_surface_builder", () -> new DesertLakesSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> DISSECTED_BADLANDS_SURFACE_BUILDER = createDecorator("dissected_badlands_surface_builder", () -> new DissectedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> SPIKY_BADLANDS = createDecorator("spiky_badlands_surface_builder", () -> new SpikyBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> ICED_TERRAIN_SURFACE_BUILDER = createDecorator("iced_terrain_surface_builder", () -> new IcedTerrainSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> NETHER_WASTES_SURFACE_BUILDER = createDecorator("nether_surface_builder", () -> new NetherwastesSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> GRASSY_END_SURFACE_BUILDER = createDecorator("grassy_end_surface_builder", () -> new GrassyEndSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> OCEAN_SURFACE_BUILDER = createDecorator("ocean_surface_builder", () -> new OceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> SAND_SURFACE_BUILDER = createDecorator("sand_surface_builder", () -> new SandSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> GRAVEL_SURFACE_BUILDER = createDecorator("gravel_surface_builder", () -> new GravelSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<TernarySurfaceConfig>> MOUNTAINS_MUTATED_SURFACE_BUILDER = createDecorator("mountains_mutated_surface_builder", () -> new MountainsMutatedSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<QuadrarySurfaceBuilderConfig>> PLATEAU_SURFACE_BUILDER = createDecorator("plateau_surface_builder", () -> new PlateauSurfaceBuilder(QuadrarySurfaceBuilderConfig.CODEC));
	public static final RegistryObject<SurfaceBuilder<QuadrarySurfaceBuilderConfig>> SHATTERED_SAVANNA_SURFACE_BUILDER = createDecorator("shattered_savanna_surface_builder", () -> new ShatteredSavannaSurfaceBuilder(QuadrarySurfaceBuilderConfig.CODEC));

	public static <D extends SurfaceBuilder<?>> RegistryObject<D> createDecorator(String name, Supplier<? extends D> surfacebuilder) {
		return SURFACE_BUILDERS.register(name, surfacebuilder);
	}

	public static final TernarySurfaceConfig SAND_SANDSTONE_SANDSTONE_SURFACE = new TernarySurfaceConfig(Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState());
	public static final QuadrarySurfaceBuilderConfig COARSE_DIRT_COARSE_DIRT_GRAVEL_DIRT_SURFACE = new QuadrarySurfaceBuilderConfig(Blocks.COARSE_DIRT.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.DIRT.getDefaultState());
	public static final QuadrarySurfaceBuilderConfig GRASS_BLOCK_DIRT_GRAVEL_COARSE_DIRT_SURFACE = new QuadrarySurfaceBuilderConfig(Blocks.GRASS_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.COARSE_DIRT.getDefaultState());
}
