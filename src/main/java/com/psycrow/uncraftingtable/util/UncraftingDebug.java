package com.psycrow.uncraftingtable.util;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.config.ModConfig;

public final class UncraftingDebug {
    private UncraftingDebug() {}

    public static void log(String message, Object... arguments) {
        if (ModConfig.DEBUG.get()) {
            UncraftingTableMod.LOGGER.info("[UncraftingTable] " + message, arguments);
        }
    }
}