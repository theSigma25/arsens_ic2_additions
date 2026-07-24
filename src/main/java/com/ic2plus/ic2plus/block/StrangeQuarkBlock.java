package com.ic2plus.ic2plus.block;

import com.ic2plus.ic2plus.ModRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class StrangeQuarkBlock extends Block {

    public static final PropertyInteger ENERGY =
            PropertyInteger.create("energy", 0, 15);

    public StrangeQuarkBlock() {
        super(Material.IRON);
        setRegistryName("ic2plus", "strange_quark_block");
        setBlockUnbreakable();
        setResistance(6000000F);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(ENERGY, 15));
    }


    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        int energy = state.getValue(ENERGY);
        if (energy <= 0) {
            world.setBlockToAir(pos);
            return;
        }
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos target = pos.offset(side);
            float hardness = world.getBlockState(target).getBlockHardness(world, target);
            if ((hardness > 0) && (world.getBlockState(target).getBlock() != ModRegistry.STRANGE_QUARK_BLOCK)) {
                world.setBlockState(
                        target,
                        getDefaultState().withProperty(ENERGY, energy - 1)
                );
                world.scheduleUpdate(target, this, 5);
            }
        }
        world.setBlockToAir(pos);
        return;
    }


    @Override
    protected net.minecraft.block.state.BlockStateContainer createBlockState() {
        return new net.minecraft.block.state.BlockStateContainer(this, ENERGY);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ENERGY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ENERGY, meta);
    }

    @Override
    public int tickRate(World world) {
        return 1;
    }
}