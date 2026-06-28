package com.psycrow.uncraftingtable.config;

import java.util.Collections;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRAFTABLE = BUILDER
            .comment("Whether the Uncrafting Table crafting recipe is enabled.")
            .define("general.craftable", true);

    public static final ModConfigSpec.BooleanValue USE_CUSTOM_CRAFTING_RECIPE = BUILDER
            .comment("When true, the crafting recipe is built from customCraftingPattern and customCraftingKeys.")
            .define("general.useCustomCraftingRecipe", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CRAFTING_PATTERN = BUILDER
            .comment("Shaped crafting pattern rows (1-3 rows, 1-3 characters each). Use spaces for empty cells.")
            .defineList(
                    "general.customCraftingPattern",
                    List.of("POP", "ODO", "POP"),
                    ModConfig::isValidPatternRow);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CRAFTING_KEYS = BUILDER
            .comment("Pattern key to ingredient mappings (e.g. P=minecraft:oak_planks). Item IDs or #tags.")
            .defineList(
                    "general.customCraftingKeys",
                    List.of(
                            "P=minecraft:oak_planks",
                            "O=minecraft:obsidian",
                            "D=minecraft:diamond"),
                    ModConfig::isValidCraftingKeyEntry);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_INPUT_ITEMS = BUILDER
            .comment("Item IDs that cannot be placed in the input slot (e.g. minecraft:oak_sapling).")
            .defineList("general.blockedInputItems", Collections.emptyList(), ModConfig::isValidItemId);

    public static final ModConfigSpec.BooleanValue BLOCK_DAMAGED_TOOLS_AND_WEAPONS = BUILDER
            .comment("When true, tools and weapons with any durability loss cannot be placed in the input slot.")
            .define("general.blockDamagedToolsAndWeapons", true);

    public static final ModConfigSpec.BooleanValue DEBUG;
    public static final ModConfigSpec.BooleanValue TEST_MODE_ONLY_BOOKSHELF;

    static {
        BUILDER.push("Debugging");

        DEBUG = BUILDER
                .comment("Write detailed recipe lookup and input-slot logs to latest.log.")
                .define("debug", true);

        TEST_MODE_ONLY_BOOKSHELF = BUILDER
                .comment("When true, only minecraft:bookshelf can be placed in the input slot (troubleshooting).")
                .define("testModeOnlyBookshelf", false);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfig() {}

    private static boolean isValidItemId(Object value) {
        return value instanceof String id && Identifier.tryParse(id) != null;
    }

    private static boolean isValidIngredient(String id) {
        if (id.startsWith("#")) {
            return Identifier.tryParse(id.substring(1)) != null;
        }

        return Identifier.tryParse(id) != null;
    }

    private static boolean isValidCraftingKeyEntry(Object value) {
        if (!(value instanceof String entry)) {
            return false;
        }

        int separator = entry.indexOf('=');
        if (separator != 1 || entry.charAt(0) == ' ') {
            return false;
        }

        return isValidIngredient(entry.substring(separator + 1));
    }

    private static boolean isValidPatternRow(Object value) {
        return value instanceof String row && !row.isEmpty() && row.length() <= 3 && row.matches("[A-Z ]+");
    }
}