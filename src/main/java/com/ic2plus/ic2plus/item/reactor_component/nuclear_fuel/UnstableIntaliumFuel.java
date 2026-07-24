package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import com.ic2plus.ic2plus.ModRegistry;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.ic2plus.ic2plus.ModRegistry.NEUTRON_PASTE;

public class    UnstableIntaliumFuel extends NuclearFuel {
    private static final int[][] OFFSETS = {
            {-2, 1},  {-2, 0},  {-2, -1},
            {-1, -2}, {0, -2},  {1, -2},
            {2, -1},  {2, 0},   {2, 1},
            {1, 2},   {0, 2},   {-1, 2}
    };
    public UnstableIntaliumFuel(String registryName, int cells) {
        super(
                registryName,
                cells,
                60,
                100,
                30,
                1000,
                300,
                255,
                null
        );
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (!reactor.produceEnergy()) return;

        int basePulses = 1;
        int pulses = basePulses;

        if (!heatRun) {
            this.acceptUraniumPulse(stack, reactor, stack, x, y, x, y, heatRun);
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
        if (!heatRun) {
            if (this.getCustomDamage(stack) >= this.getMaxCustomDamage(stack) - 1) {
                reactor.explode();
                World world = reactor.getWorldObj();
                BlockPos position = reactor.getPosition();
                world.spawnEntity(new EntityItem(world, position.getX()+0.5, position.getY()+0.5, position.getZ()+0.5, new ItemStack(NEUTRON_PASTE)));
            } else {
                this.applyCustomDamage(stack, 1, null);
            }
        }
    }
    @Override
    protected int getFinalHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        double multiplier = Math.exp(((double) stack.getItemDamage()/stack.getMaxDamage())*7);
        return (int) (heat*multiplier);
    }
    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        double multiplier = Math.exp((double) stack.getItemDamage()/stack.getMaxDamage()*7);
        if (youX == pulseX && youY == pulseY) {
            if (!heatRun) {
                reactor.addOutput((float) (powerMultiplier*multiplier));
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
            reactor.addOutput((float) (powerMultiplier*multiplier));
        }
        return true;
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                IC2Potion.radiation.applyTo(entityLiving, radiationDuration*20, radiationAmplifier);
                entityLiving.addPotionEffect(new PotionEffect(MobEffects.WITHER, 6000,4));
                entityLiving.attackEntityFrom(DamageSource.WITHER, 100);
            }
        }
    }
}
