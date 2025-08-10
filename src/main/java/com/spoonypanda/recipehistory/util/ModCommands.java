package com.spoonypanda.recipehistory.util;

import com.spoonypanda.recipehistory.RecipeHistory;
import com.spoonypanda.recipehistory.config.Config;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

// This bad boy is just for debugging.
@Mod.EventBusSubscriber(modid = RecipeHistory.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        if (!Config.DEBUG_COMMAND_TOGGLE.get()) return;

        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSource>literal("recipehistory")
                        .executes(ctx -> {
                            CommandSource src = ctx.getSource();
                            src.sendSuccess(new StringTextComponent("JEI Last viewed recipes:"), false);
                            try {
                                List<HistoryEntry> entries = JEIHooks.getRecentlyViewedEntries();
                                if (entries.isEmpty()) {
                                    src.sendSuccess(new StringTextComponent(" (empty)"), false);
                                    return 1;
                                }
                                int i = 1;
                                for (HistoryEntry e : entries) {
                                    if (e == null || e == HistoryEntry.EMPTY || e.focus == null) continue;
                                    Object val = e.focus.getValue();
                                    String mod = e.getModName();
                                    src.sendSuccess(new StringTextComponent(i++ + ": " + String.valueOf(val) + " [" + mod + "]"), false);
                                }
                            } catch (Throwable t) {
                                src.sendFailure(new StringTextComponent("RecipeHistory: client history not available (JEI not ready on this side)."));
                            }
                            return 1;
                        })
        );
    }
}