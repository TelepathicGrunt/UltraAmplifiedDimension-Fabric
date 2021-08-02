package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.mixin.features.TreeDecoratorTypeAccessor;
import com.telepathicgrunt.ultraamplifieddimension.world.features.treedecorators.DiskGroundDecorator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.function.Supplier;

public class UADTreeDecoratorTypes {
    public static TreeDecoratorType<DiskGroundDecorator> DISK_GROUND_DECORATOR = null;

    public static <B extends TreeDecoratorType<?>> B createTreeDecoratorType(String name, Supplier<B> type) {
        return Registry.register(Registry.TREE_DECORATOR_TYPE, new Identifier(UltraAmplifiedDimension.MODID, name), type.get());
    }

    public static void init(){
        DISK_GROUND_DECORATOR = createTreeDecoratorType("disk_ground_decorator", () -> TreeDecoratorTypeAccessor.uad_createTreeDecoratorType(DiskGroundDecorator.CODEC));
    }
}
