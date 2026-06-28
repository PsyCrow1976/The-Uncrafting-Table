package com.psycrow.uncraftingtable.util;

import com.psycrow.uncraftingtable.config.ModConfig;
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

        return true;
    }
}