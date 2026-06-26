package com.psycrow.uncraftingtable.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRAFTABLE = BUILDER
            .comment("Whether the Uncrafting Table can be crafted from oak planks.")
            .define("general.craftable", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfig() {}
}