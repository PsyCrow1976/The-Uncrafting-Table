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

        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        json.addProperty("category", "misc");

        JsonArray pattern = new JsonArray();
        for (String row : patternRows) {
            pattern.add(row);
        }
        json.add("pattern", pattern);
        json.add("key", buildRecipeKey());

        JsonObject result = new JsonObject();
        result.addProperty("id", UncraftingTableMod.MOD_ID + ":uncrafting_table");
        result.addProperty("count", 1);
        json.add("result", result);

        return json;
    }

    private static JsonObject buildRecipeKey() {
        JsonObject key = new JsonObject();

        for (String entry : ModConfig.CUSTOM_CRAFTING_KEYS.get()) {
            int separator = entry.indexOf('=');
            if (separator == 1) {
                key.addProperty(entry.substring(0, 1), entry.substring(separator + 1));
            }
        }

        return key;
    }
}