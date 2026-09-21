package io.github.derec4.unmount;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

final class UnmountConfig {
    static boolean AUTO_BREAK_ENABLED = true;
    static boolean COLLISION_ENABLED = false;
    static double COLLISION_BULLET_SPEED_THRESHOLD = 0.5;

    private UnmountConfig() {
    }

    static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("unmount.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Data data = new Data();

        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.writeString(path, gson.toJson(data));
            } else {
                Data loaded = gson.fromJson(Files.readString(path), Data.class);
                if (loaded != null) {
                    data = loaded;
                }
            }
        } catch (IOException e) {
            Unmount.LOGGER.warn("Failed to load unmount.json, using defaults", e);
        }

        if (data.autoBreak != null) {
            AUTO_BREAK_ENABLED = data.autoBreak.enabled;
        }
        if (data.collision != null) {
            COLLISION_ENABLED = data.collision.enabled;
            COLLISION_BULLET_SPEED_THRESHOLD = Math.max(0.0, data.collision.bulletSpeedThreshold);
        }
    }

    private static final class Data {
        @SerializedName("auto-break")
        AutoBreak autoBreak = new AutoBreak();
        Collision collision = new Collision();
    }

    private static final class AutoBreak {
        boolean enabled = true;
    }

    private static final class Collision {
        boolean enabled = false;
        double bulletSpeedThreshold = 0.5;
    }
}
