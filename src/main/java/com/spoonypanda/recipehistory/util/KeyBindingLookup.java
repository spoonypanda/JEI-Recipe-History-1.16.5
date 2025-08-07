package com.spoonypanda.recipehistory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindingLookup {

    public static KeyBinding getKeyBindingByTranslationKey(String translationKey) {
        for (KeyBinding key : Minecraft.getInstance().options.keyMappings) {
            if (key.getName().equals(translationKey)) {
                return key;
            }
        }
        return null;
    }
}