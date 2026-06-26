package com.psycrow.uncraftingtable.registry;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.blockentity.UncraftingTableBlockEntity;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, UncraftingTableMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UncraftingTableBlockEntity>> UNCRAFTING_TABLE =
            BLOCK_ENTITIES.register("uncrafting_table", () -> new BlockEntityType<>(
                    UncraftingTableBlockEntity::new,
                    Set.of(ModBlocks.UNCRAFTING_TABLE.get())));

    private ModBlockEntities() {}
}