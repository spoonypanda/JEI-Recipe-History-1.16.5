package com.spoonypanda.recipehistory;

import com.spoonypanda.recipehistory.handlers.JEIScreenGUIHandler;
import com.spoonypanda.recipehistory.handlers.RecipeScreenGUIHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;


@JeiPlugin
public class RecipeHistoryJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation("recipehistory", "jei_plugin");

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        RecipeHistory.LOGGER.info("Loading all Recipe History handlers.");
        reg.addGlobalGuiHandler(new RecipeScreenGUIHandler());

        @SuppressWarnings({ "rawtypes", "unchecked" })
        Class<? extends ContainerScreen<?>> cls = (Class) ContainerScreen.class;
        reg.addGuiContainerHandler(cls, new JEIScreenGUIHandler<>());
    }

    private static @Nullable IJeiRuntime RUNTIME;

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RUNTIME = runtime;
    }

    public static @Nullable IJeiRuntime getRuntime() { return RUNTIME; }

}