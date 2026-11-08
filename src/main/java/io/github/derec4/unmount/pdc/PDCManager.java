package io.github.derec4.unmount.pdc;

import io.github.derec4.unmount.Unmount;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PDCManager {
    // forgot i made this lmaoooo will edit later
    private static final Set<UUID> disabledPlayers = new HashSet<>();
    private static NamespacedKey toggleKey;
    private static File storageFile;

    private PDCManager() {
    }

    public static void init(Unmount plugin) {
        toggleKey = new NamespacedKey(plugin, "autobreak_disabled");
        plugin.getDataFolder().mkdirs();
        storageFile = new File(plugin.getDataFolder(), "disabled_players.txt");
        load();
    }

    public static void load() {
        disabledPlayers.clear();
        if (storageFile == null || !storageFile.exists()) {
            return;
        }
        try {
            for (String line : Files.readAllLines(storageFile.toPath())) {
                if (!line.isBlank()) {
                    try {
                        disabledPlayers.add(UUID.fromString(line.trim()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        if (storageFile == null) {
            return;
        }
        try {
            Files.write(storageFile.toPath(), disabledPlayers.stream().map(UUID::toString).toList());
        } catch (IOException ignored) {
        }
    }

    public static boolean isAutoBreakDisabled(UUID uuid) {
        return disabledPlayers.contains(uuid);
    }

    public static boolean toggle(UUID uuid) {
        boolean nowDisabled;
        if (disabledPlayers.contains(uuid)) {
            disabledPlayers.remove(uuid);
            nowDisabled = false;
        } else {
            disabledPlayers.add(uuid);
            nowDisabled = true;
        }
        save();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && toggleKey != null) {
            var pdc = player.getPersistentDataContainer();
            if (nowDisabled) {
                pdc.set(toggleKey, PersistentDataType.BYTE, (byte) 1);
            } else {
                pdc.remove(toggleKey);
            }
        }

        return nowDisabled;
    }

    public static Set<UUID> getDisabledPlayers() {
        return Collections.unmodifiableSet(disabledPlayers);
    }
}
