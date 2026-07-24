package com.ic2plus.ic2plus;

import com.ic2plus.ic2plus.armor.NeutronStarArmor;
import com.ic2plus.ic2plus.block.AmetrineOre;
import com.ic2plus.ic2plus.block.StrangeQuarkBlock;
import com.ic2plus.ic2plus.block.ThoriumOre;

import com.ic2plus.ic2plus.entity.BeamAttack;
import com.ic2plus.ic2plus.entity.NeutronStar;
import com.ic2plus.ic2plus.item.reactor_component.SuperCoolingRod;
import com.ic2plus.ic2plus.item.reactor_component.nuclear_fuel.*;
import com.ic2plus.ic2plus.item.resource.*;
import com.ic2plus.ic2plus.item.reactor_component.ReactorVent;
import ic2.core.IC2DamageSource;
import ic2.core.IC2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber
public class ModRegistry {
    public static Block AMETRINE_ORE = new AmetrineOre();
    public static Item AMETRINE = new SimpleItem("ametrine");
    public static Item AMETRINE_SHARD = new SimpleItem("ametrine_shard");
    public static Item TINY_AMETRINE_SHARD = new SimpleItem("tiny_ametrine_shard");

    public static Block THORIUM_ORE = new ThoriumOre();
    public static Item THORIUM = new RadioactiveItem("thorium", 8,80);
    public static Item SMALL_THORIUM = new RadioactiveItem("small_thorium",8,80);
    public static Item THORIUM_DUST = new SimpleItem("thorium_dust");
    public static Item WASHED_THORIUM_DUST = new SimpleItem("washed_thorium_dust");
    public static Item THORIUM_FUEL = new RadioactiveItem("thorium_fuel",40, 80);
    public static Item THORIUM_ROD = new ThoriumFuel("thorium_rod", 1);
    public static Item DUAL_THORIUM_ROD = new ThoriumFuel("dual_thorium_rod", 2);
    public static Item QUAD_THORIUM_ROD = new ThoriumFuel("quad_thorium_rod", 4);
    public static Item DEPLETED_THORIUM_ROD = new DepletedNuclearFuel("depleted_thorium_rod", 8, 80);
    public static Item DEPLETED_DUAL_THORIUM_ROD = new DepletedNuclearFuel("depleted_dual_thorium_rod", 8, 80);
    public static Item DEPLETED_QUAD_THORIUM_ROD = new DepletedNuclearFuel("depleted_quad_thorium_rod", 8, 80);

    public static Item URANIUM_233 = new RadioactiveItem("uranium_233",180,120);
    public static Item SMALL_URANIUM_233 = new RadioactiveItem("small_uranium_233", 180,120);
    public static Item URANIUM_233_FUEL = new RadioactiveItem("uranium_233_fuel",360,120);
    public static Item URANIUM_233_ROD = new Uranium233Fuel("uranium_233_rod", 1);
    public static Item DUAL_URANIUM_233_ROD = new Uranium233Fuel("dual_uranium_233_rod", 2);
    public static Item QUAD_URANIUM_233_ROD = new Uranium233Fuel("quad_uranium_233_rod", 4);
    public static Item DEPLETED_URANIUM_233_ROD = new DepletedNuclearFuel("depleted_uranium_233_rod", 12, 120);
    public static Item DEPLETED_DUAL_URANIUM_233_ROD = new DepletedNuclearFuel("depleted_dual_uranium_233_rod", 12, 120);
    public static Item DEPLETED_QUAD_URANIUM_233_ROD = new DepletedNuclearFuel("depleted_quad_uranium_233_rod", 12, 120);

    public static Item PLUTONIUM_ROD = new PlutoniumFuel("plutonium_rod",1);
    public static Item DUAL_PLUTONIUM_ROD = new PlutoniumFuel("dual_plutonium_rod",2);
    public static Item QUAD_PLUTONIUM_ROD = new PlutoniumFuel("quad_plutonium_rod",4);
    public static Item DEPLETED_PLUTONIUM_ROD = new DepletedNuclearFuel("depleted_plutonium_rod", 15,150);
    public static Item DEPLETED_DUAL_PLUTONIUM_ROD = new DepletedNuclearFuel("depleted_dual_plutonium_rod", 15,150);
    public static Item DEPLETED_QUAD_PLUTONIUM_ROD = new DepletedNuclearFuel("depleted_quad_plutonium_rod", 15,150);

    public static Item AMERICIUM = new RadioactiveItem("americium",5,50);
    public static Item SMALL_AMERICIUM = new RadioactiveItem("small_americium", 5, 50);
    public static Item AMERICIUM_FUEL = new RadioactiveItem("americium_fuel", 20, 50);
    public static Item AMERICIUM_ROD = new AmericiumFuel("americium_rod",1);
    public static Item DUAL_AMERICIUM_ROD = new AmericiumFuel("dual_americium_rod",2);
    public static Item QUAD_AMERICIUM_ROD = new AmericiumFuel("quad_americium_rod",4);

    public static Item CURIUM = new RadioactiveItem("curium", 300, 250);
    public static Item SMALL_CURIUM = new RadioactiveItem("small_curium", 300, 250);
    public static Item CURIUM_FUEL = new RadioactiveItem("curium_fuel", 600,250);
    public static Item CURIUM_ROD = new CuriumFuel("curium_rod",1);
    public static Item DUAL_CURIUM_ROD = new CuriumFuel("dual_curium_rod",2);
    public static Item QUAD_CURIUM_ROD = new CuriumFuel("quad_curium_rod",4);
    public static Item DEPLETED_CURIUM_ROD = new DepletedNuclearFuel("depleted_curium_rod", 25, 250);
    public static Item DEPLETED_DUAL_CURIUM_ROD = new DepletedNuclearFuel("depleted_dual_curium_rod", 25, 250);
    public static Item DEPLETED_QUAD_CURIUM_ROD = new DepletedNuclearFuel("depleted_quad_curium_rod", 25, 250);

    public static Item CALIFORNIUM = new RadioactiveItem("californium", 180, 150);
    public static Item SMALL_CALIFORNIUM = new RadioactiveItem("small_californium", 180, 150);
    public static Item CALIFORNIUM_ASH = new RadioactiveItem("californium_ash", 15, 150);
    public static Item CALIFORNIUM_FUEL = new RadioactiveItem("californium_fuel", 360,150);
    public static Item CALIFORNIUM_ROD = new CaliforniumFuel("californium_rod",1);
    public static Item DUAL_CALIFORNIUM_ROD = new CaliforniumFuel("dual_californium_rod",2);
    public static Item QUAD_CALIFORNIUM_ROD = new CaliforniumFuel("quad_californium_rod",4);

    public static Item INTALIUM_FUEL=new RadioactiveItem("intalium_fuel", 500, 200);
    public static Item INTALIUM_ROD = new IntaliumFuel("intalium_rod", 1);
    public static Item TRIO_INTALIUM_ROD = new IntaliumFuel("trio_intalium_rod", 3);
    public static Item UNSTABLE_INTALIUM_ROD = new UnstableIntaliumFuel("unstable_intalium_rod",1);

    public static Item UNBIQUADIUM_310=new SimpleItem("unbiquadium_310");
    public static Item SMALL_UNBIQUADIUM_310=new SimpleItem("small_unbiquadium_310");

    public static Item OGANESSON = new RadioactiveItem("oganesson", 1200, 250){
        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase) entity;
                if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                    IC2Potion.radiation.applyTo(entityLiving, radiationDuration*20, radiationAmplifier);
                    entityLiving.attackEntityFrom(IC2DamageSource.radiation, 100);
                }
            }
        }
    };
    public static Item SMALL_OGANESSON = new RadioactiveItem("small_oganesson", 1200, 250){
        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase) entity;
                if (!ItemArmorHazmat.hasCompleteHazmat(entityLiving)) {
                    IC2Potion.radiation.applyTo(entityLiving, radiationDuration*20, radiationAmplifier);
                    entityLiving.attackEntityFrom(IC2DamageSource.radiation, 100);
                }
            }
        }
    };

    public static Item NEUTRON_PASTE = new SimpleItem("neutron_paste"){
        @Override
        public IRarity getForgeRarity(ItemStack stack) {
            return new NeutronRarity();
        }
    };

    public static Item ELITE_HEAT_VENT = new ReactorVent("elite_heat_vent",4000, 48,0);
    public static Item ULTIMATE_HEAT_VENT = new ReactorVent("ultimate_heat_vent",16000, 196,0);
    public static Item SUPER_COOLING_ROD = new SuperCoolingRod();

    public static Item NEUTRON_STAR_HELMET = new NeutronStarArmor("neutron_star_helmet", EntityEquipmentSlot.HEAD);
    public static Item NEUTRON_STAR_CHESTPLATE = new NeutronStarArmor("neutron_star_chestplate", EntityEquipmentSlot.CHEST);
    public static Item NEUTRON_STAR_LEGGINGS = new NeutronStarArmor("neutron_star_leggings", EntityEquipmentSlot.LEGS);
    public static Item NEUTRON_STAR_BOOTS = new NeutronStarArmor("neutron_star_boots", EntityEquipmentSlot.FEET);

    public static Block STRANGE_QUARK_BLOCK = new StrangeQuarkBlock();
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry beamEntry = EntityEntryBuilder.create()
                .entity(BeamAttack.class)
                .id(new ResourceLocation("ic2plus", "beam_attack"), 999) // Уникальный ID
                .name("beam_attack")
                .tracker(128, 1, true)
                .build();

        event.getRegistry().register(beamEntry);
        EntityEntry neutronStarEntry = EntityEntryBuilder.create()
                .entity(NeutronStar.class)
                .id(new ResourceLocation("ic2plus", "neutron_star"), 929) // Уникальный ID
                .name("neutron_star")
                .tracker(512, 1, true)
                .build();

        event.getRegistry().register(neutronStarEntry);
    }
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        registerBlock(event, THORIUM_ORE);
        registerBlock(event, AMETRINE_ORE);
        registerBlock(event, STRANGE_QUARK_BLOCK);
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        registerItem(event, new ItemBlock(AMETRINE_ORE).setRegistryName(AMETRINE_ORE.getRegistryName()));
        registerItem(event, AMETRINE);
        registerItem(event, AMETRINE_SHARD);
        registerItem(event, TINY_AMETRINE_SHARD);

        registerItem(event, new ItemBlock(THORIUM_ORE).setRegistryName(THORIUM_ORE.getRegistryName()));
        registerItem(event, THORIUM);
        registerItem(event, SMALL_THORIUM);
        registerItem(event, THORIUM_DUST);
        registerItem(event, WASHED_THORIUM_DUST);
        registerItem(event, THORIUM_FUEL);
        registerItem(event, THORIUM_ROD);
        registerItem(event, DUAL_THORIUM_ROD);
        registerItem(event, QUAD_THORIUM_ROD);
        registerItem(event, DEPLETED_THORIUM_ROD);
        registerItem(event, DEPLETED_DUAL_THORIUM_ROD);
        registerItem(event, DEPLETED_QUAD_THORIUM_ROD);

        registerItem(event, URANIUM_233);
        registerItem(event, SMALL_URANIUM_233);
        registerItem(event, URANIUM_233_FUEL);
        registerItem(event, URANIUM_233_ROD);
        registerItem(event, DUAL_URANIUM_233_ROD);
        registerItem(event, QUAD_URANIUM_233_ROD);
        registerItem(event, DEPLETED_URANIUM_233_ROD);
        registerItem(event, DEPLETED_DUAL_URANIUM_233_ROD);
        registerItem(event, DEPLETED_QUAD_URANIUM_233_ROD);

        registerItem(event, PLUTONIUM_ROD);
        registerItem(event, DUAL_PLUTONIUM_ROD);
        registerItem(event, QUAD_PLUTONIUM_ROD);
        registerItem(event, DEPLETED_PLUTONIUM_ROD);
        registerItem(event, DEPLETED_DUAL_PLUTONIUM_ROD);
        registerItem(event, DEPLETED_QUAD_PLUTONIUM_ROD);

        registerItem(event, AMERICIUM);
        registerItem(event, SMALL_AMERICIUM);
        registerItem(event, AMERICIUM_FUEL);
        registerItem(event, AMERICIUM_ROD);
        registerItem(event, DUAL_AMERICIUM_ROD);
        registerItem(event, QUAD_AMERICIUM_ROD);

        registerItem(event, CURIUM);
        registerItem(event, SMALL_CURIUM);
        registerItem(event, CURIUM_FUEL);
        registerItem(event, CURIUM_ROD);
        registerItem(event, DUAL_CURIUM_ROD);
        registerItem(event, QUAD_CURIUM_ROD);
        registerItem(event, DEPLETED_CURIUM_ROD);
        registerItem(event, DEPLETED_DUAL_CURIUM_ROD);
        registerItem(event, DEPLETED_QUAD_CURIUM_ROD);

        registerItem(event, CALIFORNIUM);
        registerItem(event, SMALL_CALIFORNIUM);
        registerItem(event, CALIFORNIUM_ASH);
        registerItem(event, CALIFORNIUM_FUEL);
        registerItem(event, CALIFORNIUM_ROD);
        registerItem(event, DUAL_CALIFORNIUM_ROD);
        registerItem(event, QUAD_CALIFORNIUM_ROD);

        registerItem(event, INTALIUM_FUEL);
        registerItem(event, INTALIUM_ROD);
        registerItem(event, TRIO_INTALIUM_ROD);
        registerItem(event, UNSTABLE_INTALIUM_ROD);

        registerItem(event, UNBIQUADIUM_310);
        registerItem(event, SMALL_UNBIQUADIUM_310);

        registerItem(event, OGANESSON);
        registerItem(event, SMALL_OGANESSON);

        registerItem(event, NEUTRON_PASTE);
        registerItem(event, ELITE_HEAT_VENT);
        registerItem(event, ULTIMATE_HEAT_VENT);
        registerItem(event, SUPER_COOLING_ROD);

        registerItem(event, NEUTRON_STAR_HELMET);
        registerItem(event, NEUTRON_STAR_CHESTPLATE);
        registerItem(event, NEUTRON_STAR_LEGGINGS);
        registerItem(event, NEUTRON_STAR_BOOTS);

        registerItem(event, new ItemBlock(STRANGE_QUARK_BLOCK).setRegistryName(STRANGE_QUARK_BLOCK.getRegistryName()));
    }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                BeamAttack.class,
                com.ic2plus.ic2plus.entity.RenderBeamAttack::new
        );
        net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                NeutronStar.class,
                com.ic2plus.ic2plus.entity.RenderNeutronStar::new
        );

        registerModel(Item.getItemFromBlock(AMETRINE_ORE));
        registerModel(AMETRINE);
        registerModel(AMETRINE_SHARD);
        registerModel(TINY_AMETRINE_SHARD);

        registerModel(Item.getItemFromBlock(THORIUM_ORE));
        registerModel(THORIUM);
        registerModel(SMALL_THORIUM);
        registerModel(THORIUM_DUST);
        registerModel(WASHED_THORIUM_DUST);
        registerModel(THORIUM_FUEL);
        registerModel(THORIUM_ROD);
        registerModel(DUAL_THORIUM_ROD);
        registerModel(QUAD_THORIUM_ROD);
        registerModel(DEPLETED_THORIUM_ROD);
        registerModel(DEPLETED_DUAL_THORIUM_ROD);
        registerModel(DEPLETED_QUAD_THORIUM_ROD);

        registerModel(URANIUM_233);
        registerModel(SMALL_URANIUM_233);
        registerModel(URANIUM_233_FUEL);
        registerModel(URANIUM_233_ROD);
        registerModel(DUAL_URANIUM_233_ROD);
        registerModel(QUAD_URANIUM_233_ROD);
        registerModel(DEPLETED_URANIUM_233_ROD);
        registerModel(DEPLETED_DUAL_URANIUM_233_ROD);
        registerModel(DEPLETED_QUAD_URANIUM_233_ROD);

        registerModel(PLUTONIUM_ROD);
        registerModel(DUAL_PLUTONIUM_ROD);
        registerModel(QUAD_PLUTONIUM_ROD);
        registerModel(DEPLETED_PLUTONIUM_ROD);
        registerModel(DEPLETED_DUAL_PLUTONIUM_ROD);
        registerModel(DEPLETED_QUAD_PLUTONIUM_ROD);

        registerModel(AMERICIUM);
        registerModel(SMALL_AMERICIUM);
        registerModel(AMERICIUM_FUEL);
        registerModel(AMERICIUM_ROD);
        registerModel(DUAL_AMERICIUM_ROD);
        registerModel(QUAD_AMERICIUM_ROD);

        registerModel(CURIUM);
        registerModel(SMALL_CURIUM);
        registerModel(CURIUM_FUEL);
        registerModel(CURIUM_ROD);
        registerModel(DUAL_CURIUM_ROD);
        registerModel(QUAD_CURIUM_ROD);
        registerModel(DEPLETED_CURIUM_ROD);
        registerModel(DEPLETED_DUAL_CURIUM_ROD);
        registerModel(DEPLETED_QUAD_CURIUM_ROD);

        registerModel(CALIFORNIUM);
        registerModel(SMALL_CALIFORNIUM);
        registerModel(CALIFORNIUM_ASH);
        registerModel(CALIFORNIUM_FUEL);
        registerModel(CALIFORNIUM_ROD);
        registerModel(DUAL_CALIFORNIUM_ROD);
        registerModel(QUAD_CALIFORNIUM_ROD);

        registerModel(INTALIUM_FUEL);
        registerModel(INTALIUM_ROD);
        registerModel(TRIO_INTALIUM_ROD);
        registerModel(UNSTABLE_INTALIUM_ROD);

        registerModel(UNBIQUADIUM_310);
        registerModel(SMALL_UNBIQUADIUM_310);

        registerModel(OGANESSON);
        registerModel(SMALL_OGANESSON);

        registerModel(NEUTRON_PASTE);
        registerModel(ELITE_HEAT_VENT);
        registerModel(ULTIMATE_HEAT_VENT);
        registerModel(SUPER_COOLING_ROD);

        registerModel(NEUTRON_STAR_HELMET);
        registerModel(NEUTRON_STAR_CHESTPLATE);
        registerModel(NEUTRON_STAR_LEGGINGS);
        registerModel(NEUTRON_STAR_BOOTS);

        registerModel(Item.getItemFromBlock(STRANGE_QUARK_BLOCK));
    }
    private static void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    public static void registerItem(RegistryEvent.Register<Item> event, Item item) {
        event.getRegistry().register(item);
    }

    public static void registerBlock(RegistryEvent.Register<Block> event, Block block) {
        event.getRegistry().register(block);
    }
}