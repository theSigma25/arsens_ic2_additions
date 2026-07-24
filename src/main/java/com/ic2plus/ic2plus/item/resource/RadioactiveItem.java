package com.ic2plus.ic2plus.item.resource;

import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class RadioactiveItem extends Item {
    protected final int radiationAmplifier;
    protected final int radiationDuration;
    public RadioactiveItem(String name, int radiationDuraion, int radiationAmplifier) {
        this.setRegistryName("ic2plus",name);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MATERIALS);
        this.radiationAmplifier=radiationAmplifier;
        this.radiationDuration=radiationDuraion;
    }
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                IC2Potion.radiation.applyTo(entityLiving, radiationDuration*20, radiationAmplifier);
            }
        }
    }
}
