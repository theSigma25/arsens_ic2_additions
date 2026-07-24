package com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DepletedNuclearFuel extends Item implements IReactorComponent {
    protected final int radiationDuration;
    protected final int radiationAmplifier;

    public DepletedNuclearFuel(String registryName, int radiationDuration, int radiationAmplifier) {
        super();
        this.radiationAmplifier = radiationAmplifier;
        this.radiationDuration = radiationDuration;
        this.setMaxStackSize(64);
        this.setRegistryName(registryName);
        this.setUnlocalizedName(registryName);
        this.setCreativeTab(IC2.tabIC2);
    }

    public boolean canBePlacedIn(ItemStack stack, IReactor reactor) {
        return false;
    }

    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
    }

    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatRun) {
        return false;
    }

    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return false;
    }

    public int getMaxHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return 0;
    }

    public int getCurrentHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return 0;
    }

    public int alterHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        return 0;
    }

    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return 0.0F;
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
}