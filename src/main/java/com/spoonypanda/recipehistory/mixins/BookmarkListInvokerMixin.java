package com.spoonypanda.recipehistory.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "mezz.jei.bookmarks.BookmarkList", remap = false)
public interface BookmarkListInvokerMixin {
    @Invoker("contains") boolean recipehistory$contains(Object ingredient);
    @Invoker("add")      boolean recipehistory$add(Object ingredient);
    @Invoker("remove")   boolean recipehistory$remove(Object ingredient);
}