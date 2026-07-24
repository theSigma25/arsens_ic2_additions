package com.ic2plus.ic2plus.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.api.item.IMetalArmor;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.IPseudoDamageItem;
import ic2.core.util.LogCategory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ISpecialArmor;

import java.util.List;

public abstract class ElectricArmor extends ItemArmor implements ISpecialArmor, IPseudoDamageItem, IElectricItem, IItemHudInfo, IMetalArmor {
    protected final double maxCharge;
    protected final int tier;
    protected final double transferLimit;
    protected final String textureName;

    public ElectricArmor(String registryName, String textureName, EntityEquipmentSlot armorType, double maxCharge, double transferLimit, int tier) {
        super(ArmorMaterial.DIAMOND, -1, armorType);
        this.setRegistryName("ic2plus", registryName);
        this.setUnlocalizedName(registryName);
        this.textureName = textureName;
        this.maxCharge = maxCharge;
        this.tier = tier;
        this.transferLimit = transferLimit;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        int suffix1 = (this.armorType == EntityEquipmentSlot.LEGS) ? 2 : 1;
        String suffix2 = type != null && this.hasOverlayTexture() ? "_overlay" : "";
        return "ic2plus:textures/armor/" + this.textureName + '_' + suffix1 + suffix2 + ".png";
    }

    protected boolean hasOverlayTexture() {
        return false;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab)) {
            ElectricItemManager.addChargeVariants(this, subItems);
        }
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        int prev = this.getDamage(stack);
        if (damage != prev && BaseElectricItem.logIncorrectItemDamaging) {
            IC2.log.warn(LogCategory.Armor, new Throwable(), "Detected invalid armor damage application (%d):", damage - prev);
        }
    }


    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }


    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable()) {
            return new ISpecialArmor.ArmorProperties(0, 0.0D, 0);
        } else {
            double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
            int energyPerDamage = this.getEnergyPerDamage();
            int damageLimit = Integer.MAX_VALUE;

            if (energyPerDamage > 0) {
                damageLimit = (int) Math.min(damageLimit, 25.0F * ic2.api.item.ElectricItem.manager.getCharge(armor) / energyPerDamage);
            }

            return new ISpecialArmor.ArmorProperties(0, absorptionRatio, damageLimit);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        if (ElectricItem.manager.getCharge(armor) >= this.getEnergyPerDamage()) {
            return (int) Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
        }
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        ElectricItem.manager.discharge(stack, (damage * this.getEnergyPerDamage()), Integer.MAX_VALUE, true, false, false);
    }

    public abstract double getDamageAbsorptionRatio();

    public abstract int getEnergyPerDamage();

    protected final double getBaseAbsorptionRatio() {
        switch (this.armorType) {
            case HEAD:
                return 0.15D;
            case CHEST:
                return 0.40D;
            case LEGS:
                return 0.30D;
            case FEET:
                return 0.15D;
            default:
                return 0.0D;
        }
    }


    @Override
    public void setStackDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }


    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack stack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return this.transferLimit;
    }


    @Override
    public java.util.List<String> getHudInfo(ItemStack stack, boolean advanced) {
        List<String> info = new java.util.LinkedList<>();
        info.add(ic2.api.item.ElectricItem.manager.getToolTip(stack));
        info.add(Localization.translate("ic2.item.tooltip.PowerTier", this.tier));
        return info;
    }


    @Override
    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
        return true;
    }
}
