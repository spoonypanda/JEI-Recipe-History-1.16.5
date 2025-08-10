package com.spoonypanda.recipehistory.mixins;

import mezz.jei.gui.overlay.IngredientListOverlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.Rectangle2d;

@Pseudo
@Mixin(value = IngredientListOverlay.class, remap = false)
public interface IListOverlayAccessorMixin {
    @Accessor("displayArea")
    Rectangle2d recipehistory$getDisplayArea();
}
