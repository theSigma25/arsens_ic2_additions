package com.ic2plus.ic2plus;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION,dependencies = Main.DEPENENCIES)
public class Main
{
    public static final String MODID = "ic2plus";
    public static final String NAME = "ic2+";
    public static final String VERSION = "1.0";
    public static final String DEPENENCIES = "required-after:ic2";

    private static Logger logger;
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("ic2+ is alive! :3");
        WorldGen.register();
        ModRecipes.Init();
    }
}
