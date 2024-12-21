package my.reqqpe.rspawn.CommandPlugin;

import my.reqqpe.rspawn.Main;
import my.reqqpe.rspawn.Parser;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class rspawn implements CommandExecutor {
    private final Main plugin;

    public rspawn(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!Parser.CheckPermission("default", commandSender, plugin)) return true;
        if (args.length < 1) {
            Parser.sendMessage("command-usage", commandSender, plugin);
            return true;
        }

        switch (args[0]) {
            case "setspawn":
                if (!Parser.CheckPermission("setspawn", commandSender, plugin)) break;

                if (!(commandSender instanceof Player)) {
                    Parser.sendMessage("command-usage-player", commandSender, plugin);
                    break;
                }

                Player player = (Player) commandSender;

                if (args.length < 2) {
                    Parser.sendMessage("command-usage", player, plugin);
                    break;
                }

                String type = args[1].toLowerCase();

                if (type.equalsIgnoreCase("g") || type.equalsIgnoreCase("global")) {
                    saveGlobalSpawn(player.getLocation());
                    Parser.sendMessage("set-g-spawn", player, plugin);
                    break;
                }

                if (type.equalsIgnoreCase("l") || type.equalsIgnoreCase("local")) {

                    if (args.length < 3) {
                        Parser.sendMessage("command-usage", player, plugin);
                        break;
                    }
                    String region = args[2];

                    if (!plugin.checkRegionInServer(region)) {
                        Parser.sendMessage("region-no", player, plugin);
                        break;
                    }

                    saveRegionSpawn(region, player.getLocation());
                    Parser.sendMessage("set-l-spawn", player, plugin);
                    break;
                }
                else {
                    Parser.sendMessage("command-usage", player, plugin);
                }
                break;

            case "reload":
                if (!Parser.CheckPermission("reload", commandSender, plugin)) break;

                plugin.saveDefaultConfig();
                plugin.reloadConfig();
                Parser.sendMessage("reload", commandSender, plugin);

                break;

            case "help":
                if (!Parser.CheckPermission("help", commandSender, plugin)) break;

                for (String message : plugin.getConfig().getStringList("messages.help")) {
                    if (message != null) {
                        commandSender.sendMessage(Parser.color(message));
                    }
                }
                break;
            case "spawn":
                if (!Parser.CheckPermission("spawn", commandSender, plugin)) return true;
            default:
                throw new IllegalStateException("Unexpected value: " + args[0]);
        }

        return false;
    }
    private void saveGlobalSpawn(Location location) {
        plugin.getConfig().set("spawns.global.world", location.getWorld().getName());
        plugin.getConfig().set("spawns.global.x", location.getX());
        plugin.getConfig().set("spawns.global.y", location.getY());
        plugin.getConfig().set("spawns.global.z", location.getZ());
        plugin.getConfig().set("spawns.global.yaw", location.getYaw());
        plugin.getConfig().set("spawns.global.pitch", location.getPitch());
        plugin.saveConfig();
    }

    private void saveRegionSpawn(String region, Location location) {
        String path = "spawns.regions." + region;
        plugin.getConfig().set(path + ".world", location.getWorld().getName());
        plugin.getConfig().set(path + ".x", location.getX());
        plugin.getConfig().set(path + ".y", location.getY());
        plugin.getConfig().set(path + ".z", location.getZ());
        plugin.getConfig().set(path + ".yaw", location.getYaw());
        plugin.getConfig().set(path + ".pitch", location.getPitch());
        plugin.saveConfig();
    }
}
