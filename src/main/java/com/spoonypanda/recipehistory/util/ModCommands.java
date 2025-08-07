package com.spoonypanda.recipehistory.util;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.spoonypanda.recipehistory.RecipeHistory;

import com.spoonypanda.recipehistory.config.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// This bad boy is just for debugging.
@Mod.EventBusSubscriber(modid = RecipeHistory.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        if (Config.DEBUG_COMMAND_TOGGLE.get()) {
            event.getDispatcher().register(
                    LiteralArgumentBuilder.<CommandSource>literal("recipehistory")
                            .executes(context -> {
                                CommandSource source = context.getSource();
                                source.sendSuccess(new StringTextComponent("JEI Last viewed recipes:"), false);
                                for (Object recipe : com.spoonypanda.recipehistory.util.JEIHooks.getHistory()) {
                                    source.sendSuccess(new StringTextComponent(recipe.toString()), false);
                                }
                                return 1;
                            })
            );
        }
    }
}