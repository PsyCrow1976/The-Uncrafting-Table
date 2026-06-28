package com.psycrow.uncraftingtable.config;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRAFTABLE = BUILDER
            .comment("Whether the Uncrafting Table can be crafted from 9 oak planks.")
            .define("general.craftable", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_INPUT_ITEMS = BUILDER
            .comment("Item IDs that cannot be placed in the input slot (e.g. minecraft:oak_sapling).")
            .defineList(
                    "general.blockedInputItems",
                    List.of("minecraft:oak_sapling"),
                    ModConfig::isValidItemId);

    public static final ModConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Write detailed recipe lookup and input-slot logs to latest.log.")
            .define("general.debug", true);

    public static final ModConfigSpec.BooleanValue TEST_MODE_ONLY_BOOKSHELF = BUILDER
            .comment("When true, only minecraft:bookshelf can be placed in the input slot (troubleshooting).")
            .define("general.testModeOnlyBookshelf", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfig() {}

    private static boolean isValidItemId(Object value) {
        return value instanceof String id && Identifier.tryParse(id) != null;
    }
}