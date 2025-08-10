package com.spoonypanda.recipehistory.mixins;

import mezz.jei.bookmarks.BookmarkList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "mezz.jei.gui.overlay.bookmarks.BookmarkOverlay", remap = false)
public interface BookmarkOverlayAccessorMixin {
    @Accessor("bookmarkList")
    BookmarkList recipehistory$getBookmarkList();
}