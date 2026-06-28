package com.psycrow.uncraftingtable.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRAFTABLE = BUILDER
            .comment("Whether the Uncrafting Table can be crafted from oak planks.")
            .define("general.craftable", true);

    public static final ModConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Write detailed recipe lookup and input-slot logs to latest.log.")
            .define("general.debug", true);

    public static final ModConfigSpec.BooleanValue TEST_MODE_ONLY_BOOKSHELF = BUILDER
            .comment("When true, only minecraft:bookshelf can be placed in the input slot (troubleshooting).")
            .define("general.testModeOnlyBookshelf", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfig() {}
}