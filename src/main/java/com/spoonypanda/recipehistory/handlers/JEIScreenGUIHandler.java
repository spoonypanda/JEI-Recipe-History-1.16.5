package com.spoonypanda.recipehistory.handlers;

import java.util.List;

import com.spoonypanda.recipehistory.util.JEIHooks;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;



public class JEIScreenGUIHandler implements IGuiContainerHandler<ContainerScreen<?>> {
    @Override
    public List<Rectangle2d> getGuiExtraAreas(ContainerScreen<?> screen) {
        return JEIHooks.getReservedGuiAreas();
    }
}
