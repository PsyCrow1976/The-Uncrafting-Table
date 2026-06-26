package com.psycrow.uncraftingtable;

import com.psycrow.uncraftingtable.client.UncraftingTableScreen;
import com.psycrow.uncraftingtable.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = UncraftingTableMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = UncraftingTableMod.MOD_ID, value = Dist.CLIENT)
public class UncraftingTableClient {
    public UncraftingTableClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        UncraftingTableMod.LOGGER.info("Uncrafting Table client setup");
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.UNCRAFTING_TABLE.get(), UncraftingTableScreen::new);
    }
}