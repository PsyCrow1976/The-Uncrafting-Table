package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import com.psycrow.uncraftingtable.menu.UncraftingTableMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(UncraftingTableMod.MOD_ID);

        registrar.playToServer(
                CycleRecipePayload.TYPE,
                CycleRecipePayload.STREAM_CODEC,
                ModNetworking::handleCycleRecipe);

        registrar.playToServer(
                UncraftPayload.TYPE,
                UncraftPayload.STREAM_CODEC,
                ModNetworking::handleUncraft);
    }

    private static void handleCycleRecipe(CycleRecipePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                AbstractContainerMenu menu = serverPlayer.containerMenu;
                if (menu instanceof UncraftingTableMenu uncraftingTableMenu) {
                    UncraftHandler.cycleRecipe(serverPlayer, uncraftingTableMenu);
                }
            }
        });
    }

    private static void handleUncraft(UncraftPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                AbstractContainerMenu menu = serverPlayer.containerMenu;
                if (menu instanceof UncraftingTableMenu uncraftingTableMenu) {
                    UncraftHandler.tryUncraft(serverPlayer, uncraftingTableMenu);
                }
            }
        });
    }
}