package com.ic2plus.ic2plus;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Random;

public class WorldGen implements IWorldGenerator {

    public static void register() {
        GameRegistry.registerWorldGenerator(new WorldGen(), 3);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world,
                         IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

        switch (world.provider.getDimension()) {

            case 0:
                runGenerator(
                        ModRegistry.THORIUM_ORE.getDefaultState(),
                        world,
                        rand,
                        chunkX,
                        chunkZ,
                        8,
                        2,
                        0,
                        45
                );
                break;

            case 1:
                runGeneratorEnd(
                        ModRegistry.AMETRINE_ORE.getDefaultState(),
                        world,
                        rand,
                        chunkX,
                        chunkZ,
                        1,
                        1
                );
                break;
        }
    }

    private void runGenerator(IBlockState block, World world, Random rand,
                              int chunkX, int chunkZ,
                              int veinSize, int chances,
                              int minY, int maxY) {

        int heightDiff = maxY - minY;

        for (int i = 0; i < chances; i++) {

            int x = chunkX * 16 + rand.nextInt(16);
            int y = minY + rand.nextInt(heightDiff);
            int z = chunkZ * 16 + rand.nextInt(16);

            BlockPos pos = new BlockPos(x, y, z);

            new WorldGenMinable(block, veinSize).generate(world, rand, pos);
        }
    }

    private void runGeneratorEnd(IBlockState block, World world, Random rand,
                                 int chunkX, int chunkZ,
                                 int veinSize, int chances) {

        for (int i = 0; i < chances; i++) {

            int x = chunkX * 16 + rand.nextInt(16);
            int y = 20 + rand.nextInt(60);
            int z = chunkZ * 16 + rand.nextInt(16);

            BlockPos pos = new BlockPos(x, y, z);

            new WorldGenMinable(
                    block,
                    veinSize,
                    state -> state.getBlock() == Blocks.END_STONE
            ).generate(world, rand, pos);
        }
    }
}