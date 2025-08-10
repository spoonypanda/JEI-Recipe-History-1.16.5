package com.spoonypanda.recipehistory.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.spoonypanda.recipehistory.RecipeHistory;
import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;
import com.spoonypanda.recipehistory.mixins.BookmarkListInvokerMixin;
import com.spoonypanda.recipehistory.mixins.BookmarkOverlayAccessorMixin;
import com.spoonypanda.recipehistory.screens.ScreenOverlayRenderer;
import com.spoonypanda.recipehistory.util.HistoryEntry;
import com.spoonypanda.recipehistory.util.KeyBindingLookup;

import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.IBookmarkOverlay;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;

import mezz.jei.bookmarks.BookmarkList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = RecipeHistory.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InputEventHandler {
    @SubscribeEvent
    public static void onGuiClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
        Screen screen = event.getGui();
        int mouseX = (int) event.getMouseX();
        int mouseY = (int) event.getMouseY();
        int button = event.getButton();
        if (ScreenOverlayRenderer.handleMouseClicked(screen, mouseX, mouseY, button)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        int keyCode = event.getKeyCode();

        Minecraft mc = Minecraft.getInstance();
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        HistoryEntry hovered = ScreenOverlayRenderer.getHoveredEntryAt((int) mouseX, (int) mouseY);

        if (hovered != HistoryEntry.EMPTY) {

            if (KeyBindingLookup.showRecipes() != null && keyCode == KeyBindingLookup.showRecipes().getKey().getValue()) {
                showJei(hovered, IFocus.Mode.OUTPUT);
                event.setCanceled(true);
            } else if (KeyBindingLookup.showUses() != null && keyCode == KeyBindingLookup.showUses().getKey().getValue()) {
                showJei(hovered, IFocus.Mode.INPUT);
                event.setCanceled(true);
            } else if (KeyBindingLookup.toggleBookmark() != null && keyCode == KeyBindingLookup.toggleBookmark().getKey().getValue()) {
                toggleBookmark(hovered);
                event.setCanceled(true);
            }
        }
    }

    private static void showJei(HistoryEntry entry, IFocus.Mode mode) {
        IJeiRuntime runtime = RecipeHistoryJEIPlugin.getRuntime();
        if (runtime == null || entry == null || entry.focus == null || entry.type == null) return;

        IRecipesGui recipesGui = runtime.getRecipesGui();
        IRecipeManager recipeManager = runtime.getRecipeManager();

        IFocus<?> newFocus = recipeManager.createFocus(mode, entry.focus.getValue());
        recipesGui.show(newFocus);
    }

    public static void toggleBookmark(HistoryEntry entry) {
        if (entry == null || entry.focus == null) return;

        IJeiRuntime rt = RecipeHistoryJEIPlugin.getRuntime();
        if (rt == null) return;

        IBookmarkOverlay overlay = rt.getBookmarkOverlay();
        if (overlay == null) return;

        Object ingredient = entry.focus.getValue();
        if (ingredient == null) return;

        BookmarkList list = ((BookmarkOverlayAccessorMixin) overlay).recipehistory$getBookmarkList();
        BookmarkListInvokerMixin inv = (BookmarkListInvokerMixin) (Object) list;

        if (inv.recipehistory$contains(ingredient)) {
            inv.recipehistory$remove(ingredient);
        } else {
            inv.recipehistory$add(ingredient);
        }
    }
}
