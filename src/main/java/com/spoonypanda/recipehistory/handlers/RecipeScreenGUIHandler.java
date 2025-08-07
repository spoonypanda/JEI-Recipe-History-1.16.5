package com.spoonypanda.recipehistory.handlers;

import java.util.Collection;
import java.util.Collections;

import com.spoonypanda.recipehistory.util.JEIHooks;

import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.gui.recipes.RecipesGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;

public class RecipeScreenGUIHandler implements IGlobalGuiHandler {
    @Override
    public Collection<Rectangle2d> getGuiExtraAreas() {
        if (!(Minecraft.getInstance().screen instanceof RecipesGui)) {
            return Collections.emptyList();
        }

        Rectangle2d reserved = JEIHooks.getJeiItemListReservedArea();
        return reserved == null ? Collections.emptyList() : Collections.singletonList(reserved);
    }
}