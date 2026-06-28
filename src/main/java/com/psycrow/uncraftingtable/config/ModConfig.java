package com.psycrow.uncraftingtable.config;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRAFTABLE = BUILDER
            .comment("Whether the Uncrafting Table crafting recipe is enabled.")
            .define("general.craftable", true);

    public static final ModConfigSpec.BooleanValue USE_CUSTOM_CRAFTING_RECIPE = BUILDER
            .comment("When true, the crafting recipe is built from customCraftingPattern and customCraftingIngredient.")
            .define("general.useCustomCraftingRecipe", true);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_CRAFTING_PATTERN = BUILDER
            .comment("Shaped crafting pattern rows (1-3 rows, 1-3 characters each). Use spaces for empty cells.")
            .defineList(
                    "general.customCraftingPattern",
                    List.of("PPP", "PPP", "PPP"),
                    ModConfig::isValidPatternRow);

    public static final ModConfigSpec.ConfigValue<String> CUSTOM_CRAFTING_INGREDIENT = BUILDER
            .comment("Ingredient for non-space pattern characters (item ID or tag prefixed with #, e.g. minecraft:oak_planks).")
            .define("general.customCraftingIngredient", "minecraft:oak_planks", ModConfig::isValidIngredient);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_INPUT_ITEMS = BUILDER
            .comment("Item IDs that cannot be placed in the input slot (e.g. minecraft:oak_sapling).")
            .defineList(
                    "general.blockedInputItems",
                    List.of("minecraft:oak_sapling"),
                    ModConfig::isValidItemId);

    public static final ModConfigSpec.BooleanValue BLOCK_DAMAGED_TOOLS_AND_WEAPONS = BUILDER
            .comment("When true, tools and weapons with any durability loss cannot be placed in the input slot.")
            .define("general.blockDamagedToolsAndWeapons", true);

    public static final ModConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Write detailed recipe lookup and input-slot logs to latest.log.")
            .define("general.debug", true);

    public static final ModConfigSpec.BooleanValue TEST_MODE_ONLY_BOOKSHELF = BUILDER
            .comment("When true, only minecraft:bookshelf can be placed in the input slot (troubleshooting).")
            .define("general.testModeOnlyBookshelf", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfig() {}

    private static boolean isValidItemId(Object value) {
        return value instanceof String id && Identifier.tryParse(id) != null;
    }

    private static boolean isValidIngredient(Object value) {
        if (!(value instanceof String id)) {
            return false;
        }

        if (id.startsWith("#")) {
            return Identifier.tryParse(id.substring(1)) != null;
        }

        return Identifier.tryParse(id) != null;
    }

    private static boolean isValidPatternRow(Object value) {
        return value instanceof String row && !row.isEmpty() && row.length() <= 3 && row.matches("[A-Z ]+");
    }
}