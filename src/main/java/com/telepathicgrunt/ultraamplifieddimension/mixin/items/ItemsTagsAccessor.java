package com.telepathicgrunt.ultraamplifieddimension.mixin.items;

import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemTags.class)
public interface ItemsTagsAccessor {
    @Invoker
    static Tag.Identified<Block> callRegister(String id) {
        throw new UnsupportedOperationException();
    }
}
