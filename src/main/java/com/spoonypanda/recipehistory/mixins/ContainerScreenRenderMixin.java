package com.spoonypanda.recipehistory.mixins;

import com.spoonypanda.recipehistory.screens.ScreenOverlayRenderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenRenderMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void recipehistory$render(MatrixStack ms, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ScreenOverlayRenderer.renderHistoryOverlay(
                (Screen)(Object)this, ms, mouseX, mouseY, partialTicks
        );
    }
}
