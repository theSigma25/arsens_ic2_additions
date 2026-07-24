package com.ic2plus.ic2plus;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;

public class NeutronRarity implements IRarity {
    @Override
    public TextFormatting getColor() {
        return TextFormatting.DARK_BLUE;
    }

    @Override
    public String getName() {
        return "Neutron";
    }
}
