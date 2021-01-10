package com.telepathicgrunt.ultraamplifieddimension.mixin.items;

import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemTags.class)
public interface ItemsTagsAccessor {
    @Invoker
    static Tag.Identified<Item> callRegister(String id) {
        throw new UnsupportedOperationException();
    }
}
