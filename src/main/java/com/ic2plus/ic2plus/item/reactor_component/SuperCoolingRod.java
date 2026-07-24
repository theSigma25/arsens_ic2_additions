//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ic2plus.ic2plus.item.reactor_component;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.item.reactor.AbstractReactorComponent;
import net.minecraft.item.ItemStack;

public class SuperCoolingRod extends AbstractReactorComponent {
    private final int sideVent = 24;
    private static final int[][] OFFSETS = {
            {-1, 1}, {0, 1}, {1, 1},
            {1, 0}, {1, -1}, {0, -1},
            {-1, -1}, {-1, 0},
            {-2, 1},  {-2, 0},  {-2, -1},
            {-1, -2}, {0, -2},  {1, -2},
            {2, -1},  {2, 0},   {2, 1},
            {1, 2},   {0, 2},   {-1, 2},
            {-1, 3}, {0, 3}, {1, 3},
            {2, 2}, {3, 1}, {3, 0},
            {3, -1}, {2, -2}, {1, -3},
            {0, -3}, {-1, -3}, {-2, -2},
            {-3, -1}, {-3, 0}, {-3, 1},
            {-2, 2}
    };
    public SuperCoolingRod() {
        super(null);
        String name = "super_cooling_rod";
        this.setRegistryName("ic2plus",name);
        this.setUnlocalizedName(name);
        this.setMaxStackSize(64);
    }
    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatRun) {
        if (heatRun) {
            for (int[] offset : OFFSETS){
                this.cool(reactor, x + offset[0], y + offset[1]);
            }
        }
    }
    private void cool(IReactor reactor, int x, int y) {
        ItemStack stack = reactor.getItemAt(x, y);
        if (stack != null && stack.getItem() instanceof IReactorComponent) {
            IReactorComponent comp = (IReactorComponent)stack.getItem();
            if (comp.canStoreHeat(stack, reactor, x, y)) {
                int self = comp.alterHeat(stack, reactor, x, y, -this.sideVent);
                if (self <= 0) {
                    reactor.addEmitHeat(self + this.sideVent);
                }
            }
        }
    }
}
