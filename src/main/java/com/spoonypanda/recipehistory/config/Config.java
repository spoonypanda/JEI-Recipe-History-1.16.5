package com.spoonypanda.recipehistory.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public enum SeparatorStyle {
        DASHED,
        SOLID
    }

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.IntValue MAX_HISTORY;
    public static final ForgeConfigSpec.ConfigValue<String> SEPARATOR_COLOR;
    public static final ForgeConfigSpec.EnumValue<SeparatorStyle> SEPARATOR_STYLE;
    public static final ForgeConfigSpec.BooleanValue DEBUG_COMMAND_TOGGLE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Recipe History Settings").push("recipehistory");
        MAX_HISTORY = builder
                .comment("Maximum number of recipes to track in history")
                .defineInRange("maxHistory", 40, 1, 100);
        SEPARATOR_COLOR = builder
                .comment("Color for the separator for recipe history (e.g., #FFFFFF for white)")
                .define("separatorColor", "#FFFFFF");
        SEPARATOR_STYLE = builder
                .comment("Line Style for the separator. DASHED or SOLID")
                .defineEnum("separatorStyle", SeparatorStyle.DASHED);
        DEBUG_COMMAND_TOGGLE = builder
                .comment("Enable /recipehistory command for troubleshooting true or false.")
                .define("debugToggle", true);

        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static int getStyleColorARGB() {
        String hex = SEPARATOR_COLOR.get();

        try {
            if (hex.startsWith("#")) hex = hex.substring(1);

            int color = (hex.length() == 8)
                    ? (int) Long.parseLong(hex, 16)
                    : 0xFF000000 | Integer.parseInt(hex, 16);

            return color;
        } catch (Exception e) {
            return 0xFFFFFFFF;
        }
    }
}
