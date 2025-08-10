package com.spoonypanda.recipehistory.mixins;

import com.spoonypanda.recipehistory.screens.ScreenOverlayRenderer;
import com.spoonypanda.recipehistory.util.JEIHooks;

import mezz.jei.gui.recipes.RecipesGui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = RecipesGui.class, remap = false)
public abstract class RecipesGuiRenderMixin {

    @Inject(method = {"render", "func_230430_a_"}, at = @At("TAIL"), remap = false, require = 0)
    private void recipehistory$afterRender(MatrixStack ms, int mouseX, int mouseY, float pt, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenOverlayRenderer.renderHistoryOverlay(self, ms, mouseX, mouseY, pt);
    }

    @Inject(method = "onStateChange", at = @At("TAIL"), remap = false, require = 0)
    private void recipehistory$afterStateChange(CallbackInfo ci) {
        JEIHooks.updateFromRecipesGui((RecipesGui)(Object)this);
    }
}
