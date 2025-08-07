package com.spoonypanda.recipehistory.screens;

import java.util.List;

import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;
import com.spoonypanda.recipehistory.config.Config;
import com.spoonypanda.recipehistory.util.HistoryEntry;
import com.spoonypanda.recipehistory.util.JEIHooks;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.gui.recipes.RecipesGui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ScreenOverlayRenderer {

    public static void renderHistoryOverlay(Screen screen, MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        if (!(screen instanceof ContainerScreen || screen instanceof RecipesGui)) return;

        Rectangle2d bounds = JEIHooks.getJeiItemListReservedArea();
        if (bounds == null) return;

        drawTopBorder(ms, bounds.getX(), bounds.getY(), bounds.getWidth(), 3, 2,
                Config.getStyleColorARGB(),Config.SEPARATOR_STYLE.get());

        IJeiRuntime runtime = RecipeHistoryJEIPlugin.getRuntime();
        if (runtime == null) return;

        List<HistoryEntry> entries = JEIHooks.getRecentlyViewedEntries();
        if (entries.isEmpty()) return;

        int slotSize = 18;
        int columns = bounds.getWidth() / slotSize;
        int maxSlots = (bounds.getHeight() / slotSize) * columns;

        int xStart = bounds.getX();
        int yStart = bounds.getY();

        RenderSystem.enableDepthTest();

        HistoryEntry hoveredEntry = null;
        int hoveredX = -1;
        int hoveredY = -1;
        int i = 0;

        for (int idx = entries.size() - 1; idx >= 0 && i < maxSlots; idx--) {
            HistoryEntry entry = entries.get(idx);
            Object ingredient = entry.focus.getValue();
            IIngredientRenderer renderer = runtime.getIngredientManager().getIngredientRenderer(entry.type);

            int col = i % columns;
            int row = i / columns;
            int x = xStart + col * slotSize;
            int y = yStart + row * slotSize;

            renderer.render(ms, x + 1, y + 1, ingredient);

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                hoveredEntry = entry;
                hoveredX = x;
                hoveredY = y;
            }
            i++;
        }

        if (hoveredEntry != null) {
            Object ingredient = hoveredEntry.focus.getValue();
            IIngredientRenderer renderer = runtime.getIngredientManager().getIngredientRenderer(hoveredEntry.type);
            ITooltipFlag flag = Minecraft.getInstance().options.advancedItemTooltips ?
                    ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
            List<ITextComponent> tooltip = renderer.getTooltip(ingredient, flag);
            tooltip.add(new StringTextComponent(TextFormatting.BLUE + hoveredEntry.getModName()));

            ms.pushPose();
            ms.translate(0, 0, 300);
            AbstractGui.fill(ms, hoveredX, hoveredY, hoveredX + slotSize, hoveredY + slotSize,
                    0x80FFFFFF);
            ms.popPose();

            GuiUtils.drawHoveringText(ms, tooltip, mouseX, mouseY, screen.width, screen.height, -1,
                    Minecraft.getInstance().font);
        }
    }

    private static void drawTopBorder(MatrixStack ms, int x, int y, int width, int dashLength, int gapLength,
                                      int color, Config.SeparatorStyle style) {
        int endX = x + width;

        switch(style) {
            case DASHED:
                for (int i = x; i < endX; i += dashLength + gapLength) {
                    int dashEnd = Math.min(i + dashLength, endX);
                    AbstractGui.fill(ms, i, y, dashEnd, y + 1, color);
                }
                break;
            case SOLID:
            default:
                AbstractGui.fill(ms, x, y, endX, y + 1, color);
                break;
        }
    }

    public static void handleMouseClicked(Screen screen, int mouseX, int mouseY, int button) {
        Rectangle2d bounds = JEIHooks.getJeiItemListReservedArea();
        if (bounds == null || !(screen instanceof ContainerScreen || screen instanceof RecipesGui)) return;

        List<HistoryEntry> entries = JEIHooks.getRecentlyViewedEntries();
        if (entries.isEmpty()) return;

        int slotSize = 18;
        int columns = bounds.getWidth() / slotSize;
        int maxSlots = (bounds.getHeight() / slotSize) * columns;

        int xStart = bounds.getX();
        int yStart = bounds.getY();

        int i = 0;
        for (int idx = entries.size() - 1; idx >= 0 && i < maxSlots; idx--) {
            HistoryEntry entry = entries.get(idx);
            if (entry == null) continue;

            int col = i % columns;
            int row = i / columns;
            int x = xStart + col * slotSize;
            int y = yStart + row * slotSize;

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                IJeiRuntime runtime = RecipeHistoryJEIPlugin.getRuntime();
                if (runtime == null) return;

                IRecipesGui recipesGui = runtime.getRecipesGui();
                IRecipeManager recipeManager = runtime.getRecipeManager();

                IFocus.Mode mode = (button == 1) ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT;
                IFocus<?> focus = recipeManager.createFocus(mode, entry.focus.getValue());
                recipesGui.show(focus);
            }
            i++;
        }
    }

    public static HistoryEntry getHoveredEntryAt(int mouseX, int mouseY) {
        Rectangle2d bounds = JEIHooks.getJeiItemListReservedArea();
        if (bounds == null) return HistoryEntry.EMPTY;

        List<HistoryEntry> entries = JEIHooks.getRecentlyViewedEntries();
        if (entries.isEmpty()) return HistoryEntry.EMPTY;

        int slotSize = 18;
        int columns = bounds.getWidth() / slotSize;
        int maxSlots = (bounds.getHeight() / slotSize) * columns;

        int xStart = bounds.getX();
        int yStart = bounds.getY();

        int i = 0;
        for (int idx = entries.size() - 1; idx >= 0 && i < maxSlots; idx--) {
            HistoryEntry entry = entries.get(idx);
            if (entry == null || entry == HistoryEntry.EMPTY) continue;

            int col = i % columns;
            int row = i / columns;
            int x = xStart + col * slotSize;
            int y = yStart + row * slotSize;

            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) {
                return entry;
            }

            i++;
        }

        return HistoryEntry.EMPTY;
    }
}
