package com.spoonypanda.recipehistory;

import com.spoonypanda.recipehistory.config.Config;
import com.spoonypanda.recipehistory.handlers.InputEventHandler;
import com.spoonypanda.recipehistory.util.JEIHooks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RecipeHistory.MODID)
public class RecipeHistory {

    public static final String MODID = "recipehistory";

    public RecipeHistory() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(JEIHooks.class);
        MinecraftForge.EVENT_BUS.register(new InputEventHandler());
    }

}