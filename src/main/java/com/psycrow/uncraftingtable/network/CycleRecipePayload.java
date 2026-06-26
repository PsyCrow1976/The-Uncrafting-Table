package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CycleRecipePayload() implements CustomPacketPayload {
    public static final Type<CycleRecipePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(UncraftingTableMod.MOD_ID, "cycle_recipe"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CycleRecipePayload> STREAM_CODEC =
            StreamCodec.unit(new CycleRecipePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}