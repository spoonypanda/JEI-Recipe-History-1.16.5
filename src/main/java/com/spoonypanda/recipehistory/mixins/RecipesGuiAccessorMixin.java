package com.spoonypanda.recipehistory.mixins;

import java.util.List;

import com.spoonypanda.recipehistory.util.JEIHooks;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = RecipesGui.class, remap = false)
public interface RecipesGuiAccessorMixin {
    @Accessor("logic")
    IRecipeGuiLogic recipehistory$getLogic();

    @Accessor("recipeLayouts")
    List<RecipeLayout<?>> recipehistory$getRecipeLayouts();
}
