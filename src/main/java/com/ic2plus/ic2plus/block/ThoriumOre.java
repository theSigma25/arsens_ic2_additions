package com.ic2plus.ic2plus.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class ThoriumOre extends Block {
    public ThoriumOre(){
        super(Material.ROCK);
        this.setRegistryName("ic2plus", "thorium_ore");
        this.setHardness(3.0F);
        this.setResistance(3.0F);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe",2);
    }
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }
    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
}
