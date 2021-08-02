package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.SnowyBlock;
import net.minecraft.sound.BlockSoundGroup;


public class GlowpodzolBlock extends SnowyBlock {

    public GlowpodzolBlock() {
        super(Settings.of(Material.SOIL, MapColor.SPRUCE_BROWN).strength(0.4F).sounds(BlockSoundGroup.GRAVEL).luminance((blockState) -> 15));
    }
}
