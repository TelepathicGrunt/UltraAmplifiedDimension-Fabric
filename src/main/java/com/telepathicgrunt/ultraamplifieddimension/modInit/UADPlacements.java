package com.telepathicgrunt.ultraamplifieddimension.modInit;


import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.world.decorators.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.Decorator;

import java.util.function.Supplier;

public class UADPlacements {
    public static void init(){}
    
    public static final Decorator<LedgeSurfacePlacerConfig> LEDGE_SURFACE_PLACER = createDecorator("ledge_surface_placer", () -> new LedgeSurfacePlacer(LedgeSurfacePlacerConfig.CODEC));
    public static final Decorator<YOffsetPlacerConfig> Y_OFFSET_PLACER = createDecorator("y_offset_placer", () -> new OffsetPlacer(YOffsetPlacerConfig.CODEC));
    public static final Decorator<RangeValidationPlacerConfig> RANGE_VALIDATION_PLACER = createDecorator("range_validation_placer", () -> new RangeValidationPlacer(RangeValidationPlacerConfig.CODEC));
    public static final Decorator<WaterIceSurfaceConfig> WATER_ICE_SURFACE_PLACER = createDecorator("water_ice_surface_placer", () -> new WaterIceSurfacePlacer(WaterIceSurfaceConfig.CODEC));
    public static final Decorator<NonAirSurfaceLedgePlacerConfig> NON_AIR_SURFACE_LEDGE_PLACER = createDecorator("non_air_surface_ledge_placer", () -> new NonAirSurfaceLedgePlacer(NonAirSurfaceLedgePlacerConfig.CODEC));

    public static <B extends Decorator<?>> B createDecorator(String name, Supplier<B> decorator) {
        return Registry.register(Registry.DECORATOR, new Identifier(UltraAmplifiedDimension.MODID, name), decorator.get());
    }
}
