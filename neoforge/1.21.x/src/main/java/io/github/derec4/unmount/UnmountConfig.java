package io.github.derec4.unmount;

import net.neoforged.neoforge.common.ModConfigSpec;

final class UnmountConfig {
    static final ModConfigSpec SPEC;

    static final ModConfigSpec.BooleanValue AUTO_BREAK_ENABLED;
    static final ModConfigSpec.BooleanValue COLLISION_ENABLED;
    static final ModConfigSpec.DoubleValue COLLISION_BULLET_SPEED_THRESHOLD;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("auto-break");
        AUTO_BREAK_ENABLED = builder
                .comment("If true, minecarts automatically break when players dismount. Toggle per player with /togglecart.")
                .define("enabled", true);
        builder.pop();

        builder.push("collision");
        COLLISION_ENABLED = builder
                .comment("If true, a fast minecart that hits an empty vanilla minecart will snowplow it and keep moving.")
                .define("enabled", false);
        COLLISION_BULLET_SPEED_THRESHOLD = builder
                .comment("Speed needed to trigger the snowplow behavior.")
                .defineInRange("bulletSpeedThreshold", 0.5, 0.0, 16.0);
        builder.pop();

        SPEC = builder.build();
    }

    private UnmountConfig() {
    }
}
