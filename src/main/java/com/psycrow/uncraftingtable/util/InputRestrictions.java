package com.psycrow.uncraftingtable.util;

import com.psycrow.uncraftingtable.config.ModConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
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

        if (ModConfig.BLOCK_DAMAGED_TOOLS_AND_WEAPONS.get() && isToolOrWeapon(stack) && stack.isDamaged()) {
            UncraftingDebug.log("input blocked: damaged tool/weapon {}", itemIdString);
            return false;
        }

        return true;
    }

    private static boolean isToolOrWeapon(ItemStack stack) {
        return stack.has(DataComponents.TOOL)
                || stack.has(DataComponents.WEAPON)
                || stack.has(DataComponents.PIERCING_WEAPON)
                || stack.has(DataComponents.KINETIC_WEAPON)
                || stack.has(DataComponents.BLOCKS_ATTACKS)
                || stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.AXES)
                || stack.is(ItemTags.SHOVELS)
                || stack.is(ItemTags.HOES)
                || stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.BOW_ENCHANTABLE)
                || stack.is(ItemTags.CROSSBOW_ENCHANTABLE)
                || stack.is(ItemTags.TRIDENT_ENCHANTABLE)
                || stack.is(ItemTags.FISHING_ENCHANTABLE);
    }
}