package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.SandBlock;
import net.minecraft.sound.BlockSoundGroup;


public class RedGlowsandBlock extends SandBlock {

    public RedGlowsandBlock() {
        super(11098145, Settings.of(Material.AGGREGATE, MapColor.ORANGE).strength(0.4F).sounds(BlockSoundGroup.SAND).luminance((blockState) -> 15));
    }
}
