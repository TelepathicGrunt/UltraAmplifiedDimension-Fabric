package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;


public class CoarseGlowdirtBlock extends Block {

    public CoarseGlowdirtBlock() {
        super(Settings.of(Material.SOIL, MapColor.DIRT_BROWN).strength(0.4F).sounds(BlockSoundGroup.GRAVEL).luminance((blockState) -> 15));
    }

}
