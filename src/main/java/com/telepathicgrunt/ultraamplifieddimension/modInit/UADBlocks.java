package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import com.telepathicgrunt.ultraamplifieddimension.blocks.AmplifiedPortalBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusBodyBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusCornerBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.BigCactusMainBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.CoarseGlowdirtBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowdirtBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowgrassBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowmyceliumBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowpodzolBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowsandBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.GlowstoneOreBlock;
import com.telepathicgrunt.ultraamplifieddimension.blocks.RedGlowsandBlock;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;


public class UADBlocks {

    public static Block AMPLIFIED_PORTAL = null;
    public static Block GLOWSTONE_ORE = null;
    public static Block COARSE_GLOWDIRT = null;
    public static Block GLOWDIRT = null;
    public static Block GLOWGRASS_BLOCK = null;
    public static Block GLOWMYCELIUM = null;
    public static Block GLOWPODZOL = null;
    public static Block GLOWSAND = null;
    public static Block RED_GLOWSAND = null;
    public static Block BIG_CACTUS_BODY_BLOCK = null;
    public static Block BIG_CACTUS_CORNER_BLOCK = null;
    public static Block BIG_CACTUS_MAIN_BLOCK = null;

    //creative tab to hold our block items
    public static final ItemGroup ULTRA_AMPLIFIED_TAB = FabricItemGroupBuilder.build(
            new Identifier(UltraAmplifiedDimension.MODID, "main_tab"),
            () -> new ItemStack(AMPLIFIED_PORTAL)
    );

    public static Block createBlock(String name, Supplier<Block> blockSupplier) {
        Block block = blockSupplier.get();
        Identifier blockID = new Identifier(UltraAmplifiedDimension.MODID, name);

        Registry.register(Registry.BLOCK, blockID, block);
        Registry.register(Registry.ITEM, blockID, new BlockItem(block, new Item.Settings().group(ULTRA_AMPLIFIED_TAB)));

        return block;
    }

    public static void init(){
        AMPLIFIED_PORTAL = createBlock("amplified_portal", AmplifiedPortalBlock::new);
        GLOWSTONE_ORE = createBlock("glowstone_ore", GlowstoneOreBlock::new);
        COARSE_GLOWDIRT = createBlock("coarse_glowdirt", CoarseGlowdirtBlock::new);
        GLOWDIRT = createBlock("glowdirt", GlowdirtBlock::new);
        GLOWGRASS_BLOCK = createBlock("glowgrass_block", GlowgrassBlock::new);
        GLOWMYCELIUM = createBlock("glowmycelium", GlowmyceliumBlock::new);
        GLOWPODZOL = createBlock("glowpodzol", GlowpodzolBlock::new);
        GLOWSAND = createBlock("glowsand", GlowsandBlock::new);
        RED_GLOWSAND = createBlock("red_glowsand", RedGlowsandBlock::new);
        BIG_CACTUS_BODY_BLOCK = createBlock("big_cactus_body_block", BigCactusBodyBlock::new);
        BIG_CACTUS_CORNER_BLOCK = createBlock("big_cactus_corner_block", BigCactusCornerBlock::new);
        BIG_CACTUS_MAIN_BLOCK = createBlock("big_cactus_main_block", BigCactusMainBlock::new);
    }
}