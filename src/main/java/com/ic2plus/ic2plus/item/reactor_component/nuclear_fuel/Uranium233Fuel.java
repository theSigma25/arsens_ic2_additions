package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class Uranium233Fuel extends NuclearFuel {
    public Uranium233Fuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                60,
                1.3,
                0.7,
                1.6,
                12,
                120,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        if (cells == 2) {
                            return new ItemStack(ModRegistry.DEPLETED_DUAL_URANIUM_233_ROD);
                        } else if (cells == 4) {
                            return new ItemStack(ModRegistry.DEPLETED_QUAD_URANIUM_233_ROD);
                        } else {
                            return new ItemStack(ModRegistry.DEPLETED_URANIUM_233_ROD);
                        }
                    }
                }
        );
    }
}
