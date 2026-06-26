package com.psycrow.uncraftingtable.network;

import com.psycrow.uncraftingtable.UncraftingTableMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UncraftPayload() implements CustomPacketPayload {
    public static final Type<UncraftPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(UncraftingTableMod.MOD_ID, "uncraft"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UncraftPayload> STREAM_CODEC =
            StreamCodec.unit(new UncraftPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}