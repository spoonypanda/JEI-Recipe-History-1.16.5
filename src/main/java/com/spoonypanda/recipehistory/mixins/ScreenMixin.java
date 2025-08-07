package com.spoonypanda.recipehistory.mixins;

import com.spoonypanda.recipehistory.screens.ScreenOverlayRenderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void injectRender(MatrixStack ms, CallbackInfo ci) {

        int mouseX = (int) Minecraft.getInstance().mouseHandler.xpos();
        int mouseY = (int) Minecraft.getInstance().mouseHandler.ypos();
        double scaleX = (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() /
                Minecraft.getInstance().getWindow().getScreenWidth();
        double scaleY = (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() /
                Minecraft.getInstance().getWindow().getScreenHeight();

        int guiX = (int) (mouseX * scaleX);
        int guiY = (int) (mouseY * scaleY);

        Screen self = (Screen)(Object)this;
        ScreenOverlayRenderer.renderHistoryOverlay(self, ms, guiX,guiY, 0f);
    }
}
