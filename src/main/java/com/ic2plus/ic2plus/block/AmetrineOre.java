package com.ic2plus.ic2plus.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

import static com.ic2plus.ic2plus.ModRegistry.AMETRINE;

public class AmetrineOre extends Block {
    public AmetrineOre() {
        super(Material.ROCK);
        this.setRegistryName("ic2plus", "ametrine_ore");
        this.setHardness(6.0F);
        this.setResistance(4.5F);
        this.setSoundType(SoundType.STONE);
        this.setHarvestLevel("pickaxe", 3);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return AMETRINE;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, int fortune) {
        return 3000 + RANDOM.nextInt(2000);
    }
}
