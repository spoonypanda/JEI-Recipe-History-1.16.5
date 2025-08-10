package com.spoonypanda.recipehistory.util;

import java.util.*;

import com.spoonypanda.recipehistory.RecipeHistory;
import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;
import com.spoonypanda.recipehistory.config.Config;
import com.spoonypanda.recipehistory.mixins.IListOverlayAccessorMixin;
import com.spoonypanda.recipehistory.mixins.RecipesGuiAccessorMixin;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.renderer.Rectangle2d;

public class JEIHooks {
    private static final Deque<HistoryEntry> history = new LinkedList<>();

    public static void updateFromRecipesGui(RecipesGui gui) {
        @SuppressWarnings("unchecked")
        List<RecipeLayout<?>> layouts = ((RecipesGuiAccessorMixin) gui).recipehistory$getRecipeLayouts();
        if (layouts == null || layouts.isEmpty()) return;

        RecipeLayout<?> layout = layouts.get(0);
        IFocus<?> focus = layout.getFocus();
        if (focus == null || focus.getValue() == null || focus.getMode() != IFocus.Mode.OUTPUT) return;

        IJeiRuntime rt = RecipeHistoryJEIPlugin.getRuntime();
        if (rt == null) return;

        IIngredientType<?> type = rt.getIngredientManager().getIngredientType(focus.getValue());
        if (type == null) return;

        Object recipe = layout.getRecipe();
        HistoryEntry newEntry = new HistoryEntry(recipe, focus, type);
        RecipeHistory.LOGGER.info("Attempting to add or update new entry: " + newEntry);
        addOrUpdateHistory(newEntry);

    }

    public static List<HistoryEntry> getRecentlyViewedEntries() {
        return new ArrayList<>(history);
    }

    public static Rectangle2d getJeiItemListBounds() {
        IJeiRuntime runtime = RecipeHistoryJEIPlugin.getRuntime();
        if (runtime == null) return null;
        IIngredientListOverlay overlay = runtime.getIngredientListOverlay();
        if (overlay == null) return null;
        return ((IListOverlayAccessorMixin) overlay).recipehistory$getDisplayArea();
    }

    public static Rectangle2d getJeiItemListReservedArea() {
        Rectangle2d jeiArea = getJeiItemListBounds();
        if (jeiArea == null) return null;

        int rowHeight = 18, spacing = 1;
        int reservedHeight = (rowHeight + spacing) * 2;
        int searchAreaHeight = 27;

        int x = jeiArea.getX();
        int y = jeiArea.getY() + jeiArea.getHeight() - reservedHeight - searchAreaHeight;
        int w = jeiArea.getWidth();

        return new Rectangle2d(x, y, w, reservedHeight);
    }

    public static List<Rectangle2d> getReservedGuiAreas() {
        Rectangle2d a = getJeiItemListReservedArea();
        return a != null ? Collections.singletonList(a) : Collections.emptyList();
    }

    public static void addOrUpdateHistory(HistoryEntry newEntry) {
        if (newEntry == null || newEntry.focus == null || newEntry.focus.getValue() == null) return;
        HistoryEntry last = history.peekLast();
        if (last == null || !last.matches(newEntry)) {
            history.removeIf(e -> e.matches(newEntry));
            history.addLast(newEntry);
            int max = Config.MAX_HISTORY.get();
            while (history.size() > max) history.removeFirst();
        }
    }
}