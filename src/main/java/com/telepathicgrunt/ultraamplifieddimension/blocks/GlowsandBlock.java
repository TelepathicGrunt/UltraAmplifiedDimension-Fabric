package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.SandBlock;
import net.minecraft.sound.BlockSoundGroup;


public class GlowsandBlock extends SandBlock {

    public GlowsandBlock() {
        super(14406560, Settings.of(Material.AGGREGATE, MapColor.PALE_YELLOW).strength(0.4F).sounds(BlockSoundGroup.SAND).luminance((blockState) -> 15));
    }

}
