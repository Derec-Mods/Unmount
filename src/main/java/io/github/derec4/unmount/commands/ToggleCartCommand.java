package io.github.derec4.unmount.commands;

import io.github.derec4.unmount.pdc.PDCManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ToggleCartCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            // Get the player's data container
            boolean disabled = PDCManager.toggle(player.getUniqueId());
            player.sendMessage(disabled ? "§cMinecart auto-break is now DISABLED." : "§aMinecart auto-break is now ENABLED.");
            return true;
        }

        if (!sender.hasPermission("unmount.togglecart.others")) {
            sender.sendMessage("§cYou don't have permission to use this.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Set<UUID> disabled = PDCManager.getDisabledPlayers();
            if (disabled.isEmpty()) {
                sender.sendMessage("§aNo players currently have minecart auto-break disabled.");
                return true;
            }

            sender.sendMessage("§ePlayers with auto-break disabled (" + disabled.size() + "):");
            for (UUID id : disabled) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(id);
                sender.sendMessage("§7- §f" + (op.getName() != null ? op.getName() : id.toString()) + (op.isOnline() ? " §a(Online)" : " §c(Offline)"));
            }
            return true;
        }

        boolean check = args[0].equalsIgnoreCase("check");
        String targetName = check ? (args.length > 1 ? args[1] : null) : args[0];
        if (targetName == null) {
            sender.sendMessage("§cUsage: /togglecart check <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            target = Bukkit.getOfflinePlayer(targetName);
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        String name = target.getName() != null ? target.getName() : targetName;
        boolean disabled = check ? PDCManager.isAutoBreakDisabled(target.getUniqueId()) : PDCManager.toggle(target.getUniqueId());
        sender.sendMessage("§eMinecart auto-break for §f" + name + "§e is " + (check ? "" : "now ") + (disabled ? "§cDISABLED" : "§aENABLED") + "§e.");

        if (!check && target.isOnline() && !target.equals(sender)) {
            ((Player) target).sendMessage("§eYour minecart auto-break was toggled " + (disabled ? "§cDISABLED" : "§aENABLED") + "§e by §f" + sender.getName() + "§e.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("unmount.togglecart.others")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("list", "check"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                options.add(p.getName());
            }
            return options.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return Collections.emptyList();
    }
}
