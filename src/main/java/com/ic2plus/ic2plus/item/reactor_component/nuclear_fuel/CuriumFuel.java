package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class CuriumFuel extends NuclearFuel {
    private static final int[][] OFFSETS = {
            {-1, 1},  {0, 1},  {1, 1},
            {1, 0}, {1, -1},  {0, -1},
            {-1, -1},  {-1, 0}
    };
    public CuriumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                60,
                0.5,
                0.5,
                1,
                25,
                250,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        if (cells == 2) {
                            return new ItemStack(ModRegistry.DEPLETED_DUAL_CURIUM_ROD);
                        } else if (cells == 4) {
                            return new ItemStack(ModRegistry.DEPLETED_QUAD_CURIUM_ROD);
                        } else {
                            return new ItemStack(ModRegistry.DEPLETED_CURIUM_ROD);
                        }
                    }
                }
        );
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses=cells*2-1;

        for (int iteration = 0; iteration < this.cells; ++iteration) {
            int pulses = basePulses;
            if (!heatRun) {
                for (int i = 0; i < pulses; ++i) {
                    this.acceptUraniumPulse(stack, reactor, stack, x, y, x, y, heatRun);
                }
                for (int[] offset : OFFSETS) {
                    pulses += checkPulseable(reactor, x + offset[0], y + offset[1], stack, x, y, heatRun);
                    pulses += checkPulseable(reactor, x + offset[0], y + offset[1], stack, x, y, heatRun);
                }
            } else {
                for (int[] offset : OFFSETS) {
                    pulses += checkPulseable(reactor, x + offset[0], y + offset[1], stack, x, y, heatRun);
                    pulses += checkPulseable(reactor, x + offset[0], y + offset[1], stack, x, y, heatRun);
                }
                int heat = (int) (triangularNumber(pulses) * 4 * heatMultiplier);
                heat = this.getFinalHeat(stack, reactor, x, y, heat);

                Queue<ItemStackCoord> heatAcceptors = new ArrayDeque<>();
                for (int[] offset : OFFSETS) {
                    this.checkHeatAcceptor(reactor, x + offset[0], y + offset[1], heatAcceptors);
                }
                while (!heatAcceptors.isEmpty() && heat > 0) {
                    int dheat = heat / heatAcceptors.size();
                    heat -= dheat;
                    ItemStackCoord acceptor = heatAcceptors.remove();
                    IReactorComponent acceptorComp = (IReactorComponent) acceptor.stack.getItem();
                    dheat = acceptorComp.alterHeat(acceptor.stack, reactor, acceptor.x, acceptor.y, dheat);
                    heat += dheat;
                }
                if (heat > 0) {
                    reactor.addHeat(heat);
                }
            }
        }
        if (!heatRun) {
            if (this.getCustomDamage(stack) >= this.getMaxCustomDamage(stack) - 1) {
                reactor.setItemAt(x, y, this.getDepletedStack(stack, reactor));
            } else {
                this.applyCustomDamage(stack, 1, null);
            }
        }
    }
}
