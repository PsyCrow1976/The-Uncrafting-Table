package com.psycrow.uncraftingtable.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.config.ModConfig;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;

public final class ModRecipeEvents {
    private static final Identifier UNCRAFTING_TABLE_RECIPE =
            Identifier.fromNamespaceAndPath(UncraftingTableMod.MOD_ID, "uncrafting_table");

    private ModRecipeEvents() {}

    public static void modifyRecipeJsons(ModifyRecipeJsonsEvent event) {
        if (!ModConfig.CRAFTABLE.get()) {
            event.getRecipeJsons().remove(UNCRAFTING_TABLE_RECIPE);
            return;
        }

        if (ModConfig.USE_CUSTOM_CRAFTING_RECIPE.get()) {
            event.getRecipeJsons().put(UNCRAFTING_TABLE_RECIPE, buildCustomRecipeJson());
        }
    }

    private static JsonObject buildCustomRecipeJson() {
        List<? extends String> patternRows = ModConfig.CUSTOM_CRAFTING_PATTERN.get();
        String ingredient = ModConfig.CUSTOM_CRAFTING_INGREDIENT.get();
        char symbol = detectPatternKey(patternRows);

        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        json.addProperty("category", "misc");

        JsonArray pattern = new JsonArray();
        for (String row : patternRows) {
            pattern.add(row);
        }
        json.add("pattern", pattern);

        JsonObject key = new JsonObject();
        key.addProperty(String.valueOf(symbol), ingredient);
        json.add("key", key);

        JsonObject result = new JsonObject();
        result.addProperty("id", UncraftingTableMod.MOD_ID + ":uncrafting_table");
        result.addProperty("count", 1);
        json.add("result", result);

        return json;
    }

    private static char detectPatternKey(List<? extends String> patternRows) {
        for (String row : patternRows) {
            for (int index = 0; index < row.length(); index++) {
                char symbol = row.charAt(index);
                if (symbol != ' ') {
                    return symbol;
                }
            }
        }

        return 'P';
    }
}