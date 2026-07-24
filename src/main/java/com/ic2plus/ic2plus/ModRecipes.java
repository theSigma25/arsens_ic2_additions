package com.ic2plus.ic2plus;

import ic2.api.recipe.Recipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static ic2.api.recipe.Recipes.inputFactory;

public class ModRecipes {
    public static void Init() {
        NBTTagCompound heat3000 = new NBTTagCompound();
        heat3000.setInteger("minHeat", 3000);
        Recipes.centrifuge.addRecipe(
                inputFactory.forStack(new ItemStack(ModRegistry.THORIUM_DUST)), heat3000, false,
                new ItemStack[]{
                        new ItemStack(ModRegistry.THORIUM, 5),
                        new ItemStack(
                                net.minecraft.item.Item.getByNameOrId("ic2:dust"),
                                2,
                                15
                        )
                }
        );
        Recipes.centrifuge.addRecipe(
                inputFactory.forStack(new ItemStack(ModRegistry.WASHED_THORIUM_DUST)), heat3000, false,
                new ItemStack[]{
                        new ItemStack(ModRegistry.THORIUM, 7),
                }
        );
        NBTTagCompound water1000 = new NBTTagCompound();
        water1000.setInteger("amount", 1000);
        Recipes.oreWashing.addRecipe(
                inputFactory.forStack(new ItemStack(ModRegistry.THORIUM_DUST)), water1000, false,
                new ItemStack[]{
                        new ItemStack(ModRegistry.WASHED_THORIUM_DUST),
                        new ItemStack(
                                Item.getByNameOrId("ic2:dust"),
                                2,
                                15
                        )
                }
        );
        Recipes.cannerBottle.addRecipe(
                inputFactory.forStack(new ItemStack(Item.getByNameOrId("ic2:crafting"), 1, 9)),
                inputFactory.forStack(new ItemStack(ModRegistry.THORIUM_FUEL)),
                new ItemStack(ModRegistry.THORIUM_ROD)
        );
        Recipes.cannerBottle.addRecipe(
                inputFactory.forStack(new ItemStack(Item.getByNameOrId("ic2:crafting"), 1, 9)),
                inputFactory.forStack(new ItemStack(ModRegistry.URANIUM_233)),
                new ItemStack(ModRegistry.URANIUM_233_ROD)
        );
        Recipes.cannerBottle.addRecipe(
                inputFactory.forStack(new ItemStack(Item.getByNameOrId("ic2:crafting"), 1, 9)),
                inputFactory.forStack(new ItemStack(ModRegistry.AMERICIUM)),
                new ItemStack(ModRegistry.AMERICIUM_ROD)
        );
        Recipes.cannerBottle.addRecipe(
                inputFactory.forStack(new ItemStack(Item.getByNameOrId("ic2:crafting"), 1, 9)),
                inputFactory.forStack(new ItemStack(ModRegistry.CALIFORNIUM)),
                new ItemStack(ModRegistry.CALIFORNIUM_ROD)
        );
    }
}