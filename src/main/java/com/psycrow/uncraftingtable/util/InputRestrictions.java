package com.psycrow.uncraftingtable.util;

import com.psycrow.uncraftingtable.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class InputRestrictions {
    private InputRestrictions() {}

    public static boolean isAllowed(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        if (ModConfig.TEST_MODE_ONLY_BOOKSHELF.get()) {
            return stack.is(Items.BOOKSHELF);
        }

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) {
            return true;
        }

        String itemIdString = itemId.toString();
        for (String blocked : ModConfig.BLOCKED_INPUT_ITEMS.get()) {
            if (blocked.equals(itemIdString)) {
                UncraftingDebug.log("input blocked: {}", itemIdString);
                return false;
            }
        }

        return true;
    }
}