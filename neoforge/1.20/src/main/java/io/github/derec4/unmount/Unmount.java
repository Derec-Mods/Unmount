package io.github.derec4.unmount;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Unmount.MOD_ID)
public final class Unmount {
    public static final String MOD_ID = "unmount";
    public static final String TOGGLE_KEY = "unmount.autobreak_disabled";
    static final Logger LOGGER = LogUtils.getLogger();

    public Unmount(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, UnmountConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(new UnmountEvents());
        LOGGER.info("Unmount v1.0.5.1 loaded (Forge 1.20.1)");
    }
}
