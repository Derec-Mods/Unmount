package io.github.derec4.unmount;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Unmount.MOD_ID)
public final class Unmount {
    public static final String MOD_ID = "unmount";
    public static final String TOGGLE_KEY = "unmount.autobreak_disabled";
    static final Logger LOGGER = LogUtils.getLogger();

    public Unmount(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, UnmountConfig.SPEC);
        NeoForge.EVENT_BUS.register(new UnmountEvents());
        LOGGER.info("Unmount v1.0.5.1 loaded (NeoForge 1.21.1)");
    }
}
