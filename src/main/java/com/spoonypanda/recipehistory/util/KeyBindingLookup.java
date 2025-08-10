package com.spoonypanda.recipehistory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public final class KeyBindingLookup {
    private static KeyBinding showRecipesKey;
    private static KeyBinding showUsesKey;
    private static KeyBinding toggleBookmarkKey;

    private KeyBindingLookup() {}

    public static KeyBinding showRecipes()    { ensureResolved(); return showRecipesKey; }
    public static KeyBinding showUses()       { ensureResolved(); return showUsesKey; }
    public static KeyBinding toggleBookmark() { ensureResolved(); return toggleBookmarkKey; }

    private static void ensureResolved() {
        if (showRecipesKey != null && showUsesKey != null && toggleBookmarkKey != null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || mc.options.keyMappings == null) return;

        for (KeyBinding k : mc.options.keyMappings) {
            String name = k.getName();
            if (showRecipesKey == null     && "key.jei.showRecipe".equals(name))  showRecipesKey = k;
            else if (showUsesKey == null   && "key.jei.showUses".equals(name))    showUsesKey = k;
            else if (toggleBookmarkKey == null && "key.jei.bookmark".equals(name)) toggleBookmarkKey = k;

            if (showRecipesKey != null && showUsesKey != null && toggleBookmarkKey != null) break;
        }
    }

    public static void rebuild() {
        showRecipesKey = showUsesKey = toggleBookmarkKey = null;
        ensureResolved();
    }
}