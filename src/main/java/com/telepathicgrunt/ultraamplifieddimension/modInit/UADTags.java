package com.telepathicgrunt.ultraamplifieddimension.modInit;

import com.telepathicgrunt.ultraamplifieddimension.UltraAmplifiedDimension;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class UADTags {
    // All tag wrappers need to be made at mod init.
    public static void tagInit(){}

    public static final Tag.Identified<Block> TERRACOTTA_BLOCKS = BlockTags.register(UltraAmplifiedDimension.MODID+":terracotta");
}
