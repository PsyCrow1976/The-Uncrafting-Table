package com.psycrow.uncraftingtable.registry;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.block.UncraftingTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(UncraftingTableMod.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UncraftingTableMod.MOD_ID);

    public static final DeferredBlock<UncraftingTableBlock> UNCRAFTING_TABLE = BLOCKS.registerBlock(
            "uncrafting_table",
            UncraftingTableBlock::new,
            properties -> properties
                    .mapColor(MapColor.WOOD)
                    .strength(2.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion());

    public static final DeferredItem<BlockItem> UNCRAFTING_TABLE_ITEM =
            ITEMS.registerSimpleBlockItem("uncrafting_table", UNCRAFTING_TABLE);

    private ModBlocks() {}
}