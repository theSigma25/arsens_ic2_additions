package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class PlutoniumFuel extends NuclearFuel{
    public PlutoniumFuel(String registryName, int cells){
        super(
                registryName,
                cells,
                80,
                1,
                2,
                5,
                15,
                150,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        if (cells == 2) {
                            return new ItemStack(ModRegistry.DEPLETED_DUAL_THORIUM_ROD);
                        } else if (cells == 4) {
                            return new ItemStack(ModRegistry.DEPLETED_QUAD_THORIUM_ROD);
                        } else {
                            return new ItemStack(ModRegistry.DEPLETED_THORIUM_ROD);
                        }
                    }
                }
        );
    }
    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        double multiplier = Math.exp(((double) reactor.getHeat() / reactor.getMaxHeat()));
        if (!heatRun) {
            reactor.addOutput((float) (powerMultiplier*multiplier));
        }
        return true;
    }
    @Override
    protected int getFinalHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        double multiplier = Math.exp(((double) reactor.getHeat() / reactor.getMaxHeat()) * 3);
        return (int) (heat*multiplier);
    }
}
