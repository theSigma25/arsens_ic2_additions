package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import net.minecraft.item.ItemStack;
import java.util.function.Supplier;

public class ThoriumFuel extends NuclearFuel {
    public ThoriumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                10000,
                0.5,
                0.5,
                0.5,
                8,
                80,
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
}