//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ic2plus.ic2plus.item.reactor_component;

import ic2.api.reactor.IReactor;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import net.minecraft.item.ItemStack;

public class ReactorVent extends ItemReactorHeatStorage {
    public final int selfVent;
    public final int reactorVent;

    public ReactorVent(String registryName, int heatStorage, int selfvent, int reactorvent) {
        super(null, heatStorage);
        this.setRegistryName("ic2plus",registryName);
        this.setUnlocalizedName(registryName);
        this.setMaxStackSize(64);
        this.selfVent = selfvent;
        this.reactorVent = reactorvent;
    }

    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (heatRun) {
            if (this.reactorVent > 0) {
                int rheat = reactor.getHeat();
                int reactorDrain = rheat;
                if (rheat > this.reactorVent) {
                    reactorDrain = this.reactorVent;
                }

                rheat -= reactorDrain;
                if (this.alterHeat(stack, reactor, x, y, reactorDrain) > 0) {
                    return;
                }

                reactor.setHeat(rheat);
            }

            int self = this.alterHeat(stack, reactor, x, y, -this.selfVent);
            if (self <= 0) {
                reactor.addEmitHeat(self + this.selfVent);
            }
        }

    }
}
