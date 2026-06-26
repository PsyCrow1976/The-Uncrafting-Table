package com.psycrow.uncraftingtable.condition;

import com.mojang.serialization.MapCodec;
import com.psycrow.uncraftingtable.config.ModConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public record CraftableCondition() implements ICondition {
    public static final MapCodec<CraftableCondition> CODEC = MapCodec.unit(new CraftableCondition());

    @Override
    public boolean test(IContext context) {
        return ModConfig.CRAFTABLE.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}