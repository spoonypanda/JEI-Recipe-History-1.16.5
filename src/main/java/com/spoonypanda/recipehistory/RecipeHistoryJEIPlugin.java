package com.spoonypanda.recipehistory;

import com.spoonypanda.recipehistory.handlers.JEIScreenGUIHandler;
import com.spoonypanda.recipehistory.handlers.RecipeScreenGUIHandler;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class RecipeHistoryJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation("yourmodid", "jei_plugin");
    private static IJeiRuntime jeiRuntime;

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        System.out.println("Registering All Recipe History Handlers");
        registration.addGuiContainerHandler(
                (Class<? extends ContainerScreen<?>>)(Class<?>) ContainerScreen.class,
                new JEIScreenGUIHandler()
        );

        registration.addGlobalGuiHandler(new RecipeScreenGUIHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        jeiRuntime = runtime;

    }

    public static IJeiRuntime getRuntime() {
        return jeiRuntime;
    }

}