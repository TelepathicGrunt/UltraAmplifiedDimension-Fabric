package com.telepathicgrunt.ultraamplifieddimension.mixin.features;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeDecoratorType.class)
public interface TreeDecoratorTypeAccessor {

    @Invoker("<init>")
    static <P extends TreeDecorator> TreeDecoratorType<P> uad_createTreeDecoratorType(Codec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
