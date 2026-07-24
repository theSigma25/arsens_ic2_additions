package com.ic2plus.ic2plus.item.resource;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class SimpleItem extends Item {
    public SimpleItem(String name) {
        this.setRegistryName("ic2plus",name);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }
}
