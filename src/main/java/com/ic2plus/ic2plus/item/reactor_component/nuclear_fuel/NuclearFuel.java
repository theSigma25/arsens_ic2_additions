package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.item.reactor.AbstractDamageableReactorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.function.Supplier;

public abstract class NuclearFuel extends AbstractDamageableReactorComponent {

    public final int cells;
    protected final double powerMultiplier;
    protected final double heatMultiplier;
    protected final double explosionMultiplier;
    protected final int radiationDuration;
    protected final int radiationAmplifier;
    protected final Supplier<ItemStack> depletedProduct;

    public NuclearFuel(String registryName, int cells, int duration, double powerMultiplier, double heatMultiplier, double explosionMultiplier, int radiationDuration, int radiationAmplifier, Supplier<ItemStack> depletedProduct) {
        super(null, duration);
        this.setRegistryName("ic2plus", registryName);
        this.setUnlocalizedName(registryName);
        this.setMaxStackSize(64);
        this.radiationDuration = radiationDuration;
        this.radiationAmplifier = radiationAmplifier;
        this.cells = cells;
        this.powerMultiplier = powerMultiplier;
        this.heatMultiplier = heatMultiplier;
        this.depletedProduct = depletedProduct;
        this.explosionMultiplier = explosionMultiplier;
    }

    protected static int checkPulseable(IReactor reactor, int x, int y, ItemStack stack, int mex, int mey, boolean heatRun) {
        ItemStack other = reactor.getItemAt(x, y);
        return other != null && other.getItem() instanceof IReactorComponent &&
                ((IReactorComponent) other.getItem()).acceptUraniumPulse(other, reactor, stack, x, y, mex, mey, heatRun) ? 1 : 0;
    }

    protected static int triangularNumber(int x) {
        return (x * x + x) / 2;
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return this.getCustomDamage(stack) > 0 ? 1 : 0;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses = 1 + this.cells / 2;

        for (int iteration = 0; iteration < this.cells; iteration++) {
            int pulses = basePulses;
            if (!heatRun) {
                for (int i = 0; i < pulses; i++) {
                    this.acceptUraniumPulse(stack, reactor, stack, x, y, x, y, heatRun);
                }
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

        if (!heatRun) {
            if (this.getCustomDamage(stack) >= this.getMaxCustomDamage(stack) - 1) {
                reactor.setItemAt(x, y, this.getDepletedStack(stack, reactor));
            } else {
                this.applyCustomDamage(stack, 1, null);
            }
        }
    }

    protected int getFinalHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        return heat;
    }

    protected ItemStack getDepletedStack(ItemStack stack, IReactor reactor) {
        if (depletedProduct != null) {
            ItemStack depleted = depletedProduct.get();
            if (!depleted.isEmpty()) {
                return depleted.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    protected void checkHeatAcceptor(IReactor reactor, int x, int y, Collection<ItemStackCoord> heatAcceptors) {
        ItemStack stack = reactor.getItemAt(x, y);
        if (stack != null && stack.getItem() instanceof IReactorComponent &&
                ((IReactorComponent) stack.getItem()).canStoreHeat(stack, reactor, x, y)) {
            heatAcceptors.add(new ItemStackCoord(stack, x, y));
        }
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        if (!heatRun) {
            reactor.addOutput((float) (powerMultiplier));
        }
        return true;
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return (float) (2.0F * explosionMultiplier * this.cells);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                IC2Potion.radiation.applyTo(entityLiving, radiationDuration * 20, radiationAmplifier);
            }
        }
    }

    protected static class ItemStackCoord {
        public final ItemStack stack;
        public final int x;
        public final int y;

        public ItemStackCoord(ItemStack stack, int x, int y) {
            this.stack = stack;
            this.x = x;
            this.y = y;
        }
    }
}