package com.psycrow.uncraftingtable;

import com.mojang.logging.LogUtils;
import com.psycrow.uncraftingtable.condition.CraftableCondition;
import com.psycrow.uncraftingtable.config.ModConfig;
import com.psycrow.uncraftingtable.network.ModNetworking;
import com.psycrow.uncraftingtable.registry.ModBlockEntities;
import com.psycrow.uncraftingtable.registry.ModBlocks;
import com.psycrow.uncraftingtable.registry.ModMenus;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(UncraftingTableMod.MOD_ID)
public class UncraftingTableMod {
    public static final String MOD_ID = "uncraftingtable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public UncraftingTableMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);

        modEventBus.addListener(ModNetworking::registerPayloads);
        modEventBus.addListener(this::registerConditions);

        modContainer.registerConfig(Type.COMMON, ModConfig.SPEC);

        modEventBus.addListener(this::addCreative);
    }

    private void registerConditions(RegisterEvent event) {
        event.register(
                NeoForgeRegistries.Keys.CONDITION_CODECS,
                helper -> helper.register(Identifier.fromNamespaceAndPath(MOD_ID, "craftable"), CraftableCondition.CODEC));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.UNCRAFTING_TABLE_ITEM);
        }
    }
}