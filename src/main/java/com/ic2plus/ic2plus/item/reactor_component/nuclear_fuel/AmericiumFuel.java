package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class AmericiumFuel extends NuclearFuel {
    public AmericiumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                10,
                0.5,
                0.5,
                0.5,
                5,
                50,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        if (cells == 2) {
                            return new ItemStack(ModRegistry.DUAL_CURIUM_ROD);
                        } else if (cells == 4) {
                            return new ItemStack(ModRegistry.QUAD_CURIUM_ROD);
                        } else {
                            return new ItemStack(ModRegistry.CURIUM_ROD);
                        }
                    }
                }
        );
    }

    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses = 0;

        for (int iteration = 0; iteration < this.cells; iteration++) {
            int pulses = basePulses;
            if (!heatRun) {
                pulses += checkPulseable(reactor, x - 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x + 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y - 1, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y + 1, stack, x, y, heatRun);
            } else {
                pulses = basePulses
                        + checkPulseable(reactor, x - 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x + 1, y, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y - 1, stack, x, y, heatRun)
                        + checkPulseable(reactor, x, y + 1, stack, x, y, heatRun);

                int heat = (int) (triangularNumber(pulses) * 4 * heatMultiplier);
                heat = this.getFinalHeat(stack, reactor, x, y, heat);

                Queue<ItemStackCoord> heatAcceptors = new ArrayDeque<>();
                this.checkHeatAcceptor(reactor, x - 1, y, heatAcceptors);
                this.checkHeatAcceptor(reactor, x + 1, y, heatAcceptors);
                this.checkHeatAcceptor(reactor, x, y - 1, heatAcceptors);
                this.checkHeatAcceptor(reactor, x, y + 1, heatAcceptors);

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
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        if (!heatRun) {
            reactor.addOutput((float) powerMultiplier);
            if (this.getCustomDamage(stack) >= this.getMaxCustomDamage(stack) - 1) {
                reactor.setItemAt(youX, youY, this.getDepletedStack(stack, reactor));
            } else {
                this.applyCustomDamage(stack, 1, null);
            }
        }
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - (double) getCustomDamage(stack) / getMaxCustomDamage(stack);
    }
}
