package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class UADTags {
    // All tag wrappers need to be made at mod init.
    public static void tagInit(){}

    public static final Tag<Item> PORTAL_ACTIVATION_ITEMS = TagRegistry.item(new Identifier(UltraAmplifiedDimension.MODID, "portal_activation_items"));

    public static final Tag<Block> PORTAL_CORNER_BLOCKS = TagRegistry.block(new Identifier(UltraAmplifiedDimension.MODID, "portal_corner_blocks"));
    public static final Tag<Block> PORTAL_CENTER_BLOCKS = TagRegistry.block(new Identifier(UltraAmplifiedDimension.MODID, "portal_center_blocks"));
    public static final Tag<Block> PORTAL_NON_CORNER_BLOCKS = TagRegistry.block(new Identifier(UltraAmplifiedDimension.MODID, "portal_non_corner_blocks"));
    public static final Tag<Block> TERRACOTTA_BLOCKS = TagRegistry.block(new Identifier(UltraAmplifiedDimension.MODID, "terracotta"));
    public static final Tag<Block> COMMON_DIRT_BLOCKS = TagRegistry.block(new Identifier("c:dirt"));
}
