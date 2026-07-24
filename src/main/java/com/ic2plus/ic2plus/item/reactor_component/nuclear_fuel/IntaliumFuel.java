package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class    IntaliumFuel extends NuclearFuel {
    private static final int[][] OFFSETS = {
            {-2, 1},  {-2, 0},  {-2, -1},
            {-1, -2}, {0, -2},  {1, -2},
            {2, -1},  {2, 0},   {2, 1},
            {1, 2},   {0, 2},   {-1, 2}
    };
    public IntaliumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                200000,
                50,
                15,
                20,
                20,
                200,
                new Supplier<ItemStack>() {
                    @Override
                    public ItemStack get() {
                        if (cells == 1) {
                            return new ItemStack(ModRegistry.DEPLETED_DUAL_URANIUM_233_ROD);
                        } else {
                            return new ItemStack(ModRegistry.DEPLETED_QUAD_URANIUM_233_ROD);
                        }
                    }
                }
        );
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses = this.cells;

        for (int iteration = 0; iteration < this.cells; ++iteration) {
            int pulses = basePulses;
            if (!heatRun) {
                for (int i = 0; i < pulses; ++i) {
                    this.acceptUraniumPulse(stack, reactor, stack, x, y, x, y, heatRun);
                }
                for (int[] offset : OFFSETS) {
                    pulses += checkPulseable(reactor, x + offset[0], y + offset[1], stack, x, y, heatRun);
                }
            } else {
                for (int[] offset : OFFSETS) {
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
    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        if (youX == pulseX && youY == pulseY) {
            if (!heatRun) {
                reactor.addOutput((float) (powerMultiplier));
            }
            return true;
        }
        int OffsetX = pulseX - youX;
        int OffsetY = pulseY - youY;
        boolean isValidOffset = false;
        for (int[] offset : OFFSETS) {
            if (offset[0] == OffsetX && offset[1] == OffsetY) {
                isValidOffset = true;
                break;
            }
        }
        if (!isValidOffset) {
            return false;
        }
        if (!heatRun) {
            reactor.addOutput((float) (powerMultiplier));
        }
        return true;
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                IC2Potion.radiation.applyTo(entityLiving, radiationDuration*20, radiationAmplifier);
                entityLiving.addPotionEffect(new PotionEffect(MobEffects.WITHER, 400,4));
            }
        }
    }
}
