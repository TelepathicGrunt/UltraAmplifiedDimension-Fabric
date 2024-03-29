package com.telepathicgrunt.ultraamplifieddimension.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;


public class GlowstoneOreBlock extends Block {

    public GlowstoneOreBlock() {
        super(Settings.of(Material.STONE, MapColor.STONE_GRAY).sounds(BlockSoundGroup.STONE).requiresTool().luminance((blockState) -> 15).strength(1.3F, 5.8F));
    }

}
