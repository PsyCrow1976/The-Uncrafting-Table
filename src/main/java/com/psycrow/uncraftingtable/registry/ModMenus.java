package com.psycrow.uncraftingtable.registry;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, UncraftingTableMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<UncraftingTableMenu>> UNCRAFTING_TABLE =
            MENUS.register("uncrafting_table", () -> IMenuTypeExtension.create(UncraftingTableMenu::new));

    private ModMenus() {}
}