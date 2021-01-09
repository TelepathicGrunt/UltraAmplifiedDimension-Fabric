package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.mixin.blocks.BlockTagsAccessor;
import com.telepathicgrunt.ultraamplifieddimension.mixin.items.ItemsTagsAccessor;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class UADTags {
    // All tag wrappers need to be made at mod init.
    public static void tagInit(){}

    public static final Tag.Identified<Block> PORTAL_ACTIVATION_ITEMS = ItemsTagsAccessor.callRegister(UltraAmplifiedDimension.MODID + ":portal_activation_items");

    public static final Tag.Identified<Block> PORTAL_CORNER_BLOCKS = BlockTagsAccessor.callRegister(UltraAmplifiedDimension.MODID + ":portal_corner_blocks");
    public static final Tag.Identified<Block> PORTAL_NON_CORNER_BLOCKS = BlockTagsAccessor.callRegister(UltraAmplifiedDimension.MODID+":portal_non_corner_blocks");
    public static final Tag.Identified<Block> TERRACOTTA_BLOCKS = BlockTagsAccessor.callRegister(UltraAmplifiedDimension.MODID+":terracotta");
}
