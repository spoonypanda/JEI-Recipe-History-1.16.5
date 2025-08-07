package com.spoonypanda.recipehistory.util;

import java.lang.reflect.Field;
import java.util.*;

import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;
import com.spoonypanda.recipehistory.config.Config;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.api.runtime.IIngredientListOverlay;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class JEIHooks {

    private static Field logicField;
    private static final Deque<HistoryEntry> history = new LinkedList<>();

    static {
        try {
            logicField = RecipesGui.class.getDeclaredField("logic");
            logicField.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Rectangle2d jeiItemListArea = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        jeiItemListArea = getJeiItemListBounds();
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen instanceof RecipesGui) {
            RecipesGui gui = (RecipesGui) mc.screen;
            RecipeGuiLogic logic = getLogic(gui);
            if (logic != null) {
                List<RecipeLayout<?>> layouts = logic.getRecipeLayouts(0, 0, 0);
                if (!layouts.isEmpty()) {
                    RecipeLayout<?> layout = layouts.get(0);
                    IFocus<?> focus = layout.getFocus();
                    Object recipe = layout.getRecipe();

                    if (focus == null || focus.getMode() != IFocus.Mode.OUTPUT) return;

                    HistoryEntry lastEntry = history.peekLast();
                    IIngredientType<?> type = RecipeHistoryJEIPlugin.getRuntime().getIngredientManager().getIngredientType(focus.getValue());

                    if (type == null) {
                        System.out.println("[RecipeHistory] Unknown focus type: " +
                                focus.getValue().getClass().getName());
                        return;
                    }

                    HistoryEntry newEntry = new HistoryEntry(recipe, focus, type);

                    if (lastEntry == null || !lastEntry.matches(newEntry)) {
                        history.removeIf(e -> e.matches(newEntry));
                        history.addLast(newEntry);

                        int max = Config.MAX_HISTORY.get();
                        while (history.size() > max) history.removeFirst();
                    }
                }
            }
        }
    }

    private static RecipeGuiLogic getLogic(RecipesGui gui) {
        try {
            return (RecipeGuiLogic) logicField.get(gui);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Object> getHistory() {
        return new ArrayList<>(history);
    }

    public static Rectangle2d getJeiItemListBounds() {
        try {
            IJeiRuntime runtime = RecipeHistoryJEIPlugin.getRuntime();
            if (runtime == null) return null;

            IIngredientListOverlay overlay = runtime.getIngredientListOverlay();
            if (overlay == null) return null;

            Field areaField = overlay.getClass().getDeclaredField("displayArea");
            areaField.setAccessible(true);

            Object areaObj = areaField.get(overlay);
            if (areaObj instanceof Rectangle2d) {
                return (Rectangle2d) areaObj;
            }

        } catch (Exception e) {
            System.err.println("Failed to access JEI item panel bounds:");
            e.printStackTrace();
        }
        return null;
    }

    public static Rectangle2d getJeiItemListArea() {
        return jeiItemListArea;
    }

    public static Rectangle2d getJeiItemListReservedArea() {
        Rectangle2d jeiArea = getJeiItemListArea();
        if (jeiArea == null) return null;

        int rowHeight = 18;
        int spacing = 1;
        int reservedHeight = (rowHeight + spacing) * 2;
        int searchAreaHeight = 27;

        int x = jeiArea.getX();
        int y = jeiArea.getY() + jeiArea.getHeight() - reservedHeight - searchAreaHeight;
        int width = jeiArea.getWidth();

        return new Rectangle2d(x, y, width, reservedHeight);
    }

    public static List<Rectangle2d> getReservedGuiAreas() {
        Rectangle2d area = getJeiItemListReservedArea();
        return area != null ? Collections.singletonList(area) : Collections.emptyList();
    }

    public static List<HistoryEntry> getRecentlyViewedEntries() {
        return new ArrayList<>(history);
    }
}