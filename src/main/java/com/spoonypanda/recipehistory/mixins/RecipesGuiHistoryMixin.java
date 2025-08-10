package com.spoonypanda.recipehistory.mixins;

import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;
import com.spoonypanda.recipehistory.util.HistoryEntry;
import com.spoonypanda.recipehistory.util.JEIHooks;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(value = RecipesGui.class, remap = false)
public abstract class RecipesGuiHistoryMixin {
    @Shadow
    private List<RecipeLayout<?>> recipeLayouts;

    @Inject(method = "updateLayout", at = @At("TAIL"))
    private void recipehistory$afterUpdateLayout(CallbackInfo ci) {
        if (recipeLayouts == null || recipeLayouts.isEmpty()) return;

        RecipeLayout<?> layout = recipeLayouts.get(0);
        IFocus<?> focus = layout.getFocus();
        if (focus == null || focus.getValue() == null) return;
        if (focus.getMode() != IFocus.Mode.OUTPUT) return;

        IJeiRuntime rt = RecipeHistoryJEIPlugin.getRuntime();
        if (rt == null) return;

        IIngredientType<?> type = rt.getIngredientManager().getIngredientType(focus.getValue());
        if (type == null) return;

        JEIHooks.addOrUpdateHistory(new HistoryEntry(layout.getRecipe(), focus, type));
    }
}
