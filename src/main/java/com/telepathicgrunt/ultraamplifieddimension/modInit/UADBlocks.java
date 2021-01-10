package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.blocks.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;


public class UADBlocks {
    public static void init(){}

    public static final Block AMPLIFIED_PORTAL = createBlock("amplified_portal", AmplifiedPortalBlock::new);
    public static final Block GLOWSTONE_ORE = createBlock("glowstone_ore", GlowstoneOreBlock::new);
    public static final Block COARSE_GLOWDIRT = createBlock("coarse_glowdirt", CoarseGlowdirtBlock::new);
    public static final Block GLOWDIRT = createBlock("glowdirt", GlowdirtBlock::new);
    public static final Block GLOWGRASS_BLOCK = createBlock("glowgrass_block", GlowgrassBlock::new);
    public static final Block GLOWMYCELIUM = createBlock("glowmycelium", GlowmyceliumBlock::new);
    public static final Block GLOWPODZOL = createBlock("glowpodzol", GlowpodzolBlock::new);
    public static final Block GLOWSAND = createBlock("glowsand", GlowsandBlock::new);
    public static final Block RED_GLOWSAND = createBlock("red_glowsand", RedGlowsandBlock::new);
    public static final Block BIG_CACTUS_BODY_BLOCK = createBlock("big_cactus_body_block", BigCactusBodyBlock::new);
    public static final Block BIG_CACTUS_CORNER_BLOCK = createBlock("big_cactus_corner_block", BigCactusCornerBlock::new);
    public static final Block BIG_CACTUS_MAIN_BLOCK = createBlock("big_cactus_main_block", BigCactusMainBlock::new);

    //creative tab to hold our block items
    public static final ItemGroup ULTRAMAPLIFIED = new ItemGroup(ItemGroup.GROUPS.length, UltraAmplifiedDimension.MODID) {
        @Override
        @Environment(EnvType.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(AMPLIFIED_PORTAL);
        }
    };
    
    public static Block createBlock(String name, Supplier<Block> blockSupplier) {
        Block block = blockSupplier.get();
        Registry.register(Registry.BLOCK, new Identifier(UltraAmplifiedDimension.MODID, name), block);
        Registry.register(Registry.ITEM, new Identifier(UltraAmplifiedDimension.MODID, name), new BlockItem(block, new Item.Settings().group(ULTRAMAPLIFIED)));
        return block;
    }
}