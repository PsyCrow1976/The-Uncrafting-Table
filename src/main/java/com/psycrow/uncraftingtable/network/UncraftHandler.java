package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import net.minecraft.server.level.ServerPlayer;

/**
 * Uncraft handling disabled until recipe functionality is re-enabled.
 */
public final class UncraftHandler {
    private UncraftHandler() {}

    public static void tryUncraft(ServerPlayer player, UncraftingTableMenu menu) {
        // disabled
    }

    public static void cycleRecipe(ServerPlayer player, UncraftingTableMenu menu) {
        // disabled
    }

    /*
    private static boolean canFitAll(Inventory inventory, List<ItemStack> outputs) { ... }
    private static boolean addSimulated(Inventory inventory, ItemStack stack) { ... }
    */
}