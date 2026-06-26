package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {}

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Uncraft / cycle payloads disabled until recipe functionality is re-enabled
        /*
        PayloadRegistrar registrar = event.registrar(UncraftingTableMod.MOD_ID);

        registrar.playToServer(
                CycleRecipePayload.TYPE,
                CycleRecipePayload.STREAM_CODEC,
                ModNetworking::handleCycleRecipe);

        registrar.playToServer(
                UncraftPayload.TYPE,
                UncraftPayload.STREAM_CODEC,
                ModNetworking::handleUncraft);
        */
    }
}