package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.function.Supplier;

public class CaliforniumFuel extends NuclearFuel {
    private static final Random random = new Random();

    public CaliforniumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                50,
                30,
                20,
                200,
                15,
                150,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        int amount = 0;
                        for (int iteration = 0; iteration < cells * 4; iteration++) {
                            if (random.nextInt(3) == 0) amount++;
                        }
                        return new ItemStack(ModRegistry.CALIFORNIUM_ASH, amount);
                    }
                }
        );
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        double multiplier = Math.exp(((double) stack.getItemDamage() / stack.getMaxDamage()) * 4);
        if (!heatRun) {
            reactor.addOutput((float) (powerMultiplier * multiplier));
        }
        return true;
    }

    @Override
    protected int getFinalHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        double multiplier = Math.exp(((double) stack.getItemDamage() / stack.getMaxDamage()) * 4);
        return (int) (heat * multiplier);
    }
}
