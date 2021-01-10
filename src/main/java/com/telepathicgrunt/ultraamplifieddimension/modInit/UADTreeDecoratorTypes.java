package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.mixin.features.TreeDecoratorTypeAccessor;
import com.telepathicgrunt.ultraamplifieddimension.world.features.treedecorators.DiskGroundDecorator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.tree.TreeDecoratorType;

import java.util.function.Supplier;

public class UADTreeDecoratorTypes {
    public static void init(){}

    public static final TreeDecoratorType<DiskGroundDecorator> DISK_GROUND_DECORATOR = createTreeDecoratorType("disk_ground_decorator", () -> TreeDecoratorTypeAccessor.createTreeDecoratorType(DiskGroundDecorator.CODEC));

    public static <B extends TreeDecoratorType<?>> B createTreeDecoratorType(String name, Supplier<B> type) {
        return Registry.register(Registry.TREE_DECORATOR_TYPE, new Identifier(UltraAmplifiedDimension.MODID, name), type.get());
    }
}
