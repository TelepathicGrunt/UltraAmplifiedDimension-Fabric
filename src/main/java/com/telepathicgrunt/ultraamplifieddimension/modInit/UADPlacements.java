package com.telepathicgrunt.ultraamplifieddimension.modInit;


import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.decorators.*;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class UADPlacements {
    public static final DeferredRegister<Decorator<?>> DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, UltraAmplifiedDimension.MODID);

    public static final RegistryObject<Decorator<LedgeSurfacePlacerConfig>> LEDGE_SURFACE_PLACER = createDecorator("ledge_surface_placer", () -> new LedgeSurfacePlacer(LedgeSurfacePlacerConfig.CODEC));
    public static final RegistryObject<Decorator<YOffsetPlacerConfig>> Y_OFFSET_PLACER = createDecorator("y_offset_placer", () -> new OffsetPlacer(YOffsetPlacerConfig.CODEC));
    public static final RegistryObject<Decorator<RangeValidationPlacerConfig>> RANGE_VALIDATION_PLACER = createDecorator("range_validation_placer", () -> new RangeValidationPlacer(RangeValidationPlacerConfig.CODEC));
    public static final RegistryObject<Decorator<WaterIceSurfaceConfig>> WATER_ICE_SURFACE_PLACER = createDecorator("water_ice_surface_placer", () -> new WaterIceSurfacePlacer(WaterIceSurfaceConfig.CODEC));
    public static final RegistryObject<Decorator<NonAirSurfaceLedgePlacerConfig>> NON_AIR_SURFACE_LEDGE_PLACER = createDecorator("non_air_surface_ledge_placer", () -> new NonAirSurfaceLedgePlacer(NonAirSurfaceLedgePlacerConfig.CODEC));

    public static <D extends Decorator<?>> RegistryObject<D> createDecorator(String name, Supplier<? extends D> decorator) {
        return DECORATORS.register(name, decorator);
    }
}
