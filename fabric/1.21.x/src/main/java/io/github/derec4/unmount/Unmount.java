package io.github.derec4.unmount;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Unmount implements ModInitializer {
    public static final String MOD_ID = "unmount";
    public static final String TOGGLE_KEY = "unmount.autobreak_disabled";
    static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        UnmountConfig.load();
        UnmountEvents.register();
        LOGGER.info("Unmount v1.0.5.1 loaded (Fabric 1.21.1)");
    }
}
