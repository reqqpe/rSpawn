package my.reqqpe.rspawn.CommandPlugin;

import my.reqqpe.rspawn.Main;
import my.reqqpe.rspawn.Parser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class spawn implements CommandExecutor {
    private final Main plugin;

    public spawn(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!Parser.CheckPermission("spawn", commandSender, plugin)) return true;


            // Логика для команды /spawn (телепортировать игрока на глобальный спавн)

        if (args.length == 0) {

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Только игроки могут использовать эту команду без указания игрока.");
                return true;
            }
            Player player = (Player) commandSender;
            ConfigurationSection spawnsConfig = plugin.getConfig().getConfigurationSection("spawns");

            String regionData = plugin.getRegion(player, spawnsConfig);

            if (regionData == null) {
                if (plugin.getGlobalSpawn() != null) {
                    player.teleport(plugin.getGlobalSpawn());
                    Parser.sendMessage("teleport-accept", player, plugin);
                }
                else {
                    Parser.sendMessage("teleport-cancel", player, plugin);
                }
            }
            else {
                if (plugin.getRegionSpawn(regionData) != null) {
                    player.teleport(plugin.getRegionSpawn(regionData.toString()));
                    Parser.sendMessage("teleport-accept", player, plugin);
                }
                else {
                    Parser.sendMessage("teleport-cancel", player, plugin);
                }
            }
            return true;

            // Логика для команды /spawn <region>

        }
        else if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Только игроки могут использовать эту команду без указания другого игрока.");
                return true;
            }

            Player player = (Player) commandSender;
            String region = args[0];


            if (plugin.getRegionSpawn(region) != null) {
                player.teleport(plugin.getRegionSpawn(region));
            }

            // Логика для команды /spawn <region> <player>

        }
        else if (args.length == 2) {
            String region = args[0];
            String targetPlayerName = args[1];

            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                commandSender.sendMessage("Игрок с именем " + targetPlayerName + " не найден.");
                return true;
            }

            // Логика для команды /spawn <region> <player>
            if (plugin.getRegionSpawn(region) != null) {
                Bukkit.getPlayer(targetPlayerName).teleport(plugin.getRegionSpawn(region));
            }
            else {
                commandSender.sendMessage("Указанный регион не был найден");
            }
            commandSender.sendMessage("Игрок " + targetPlayerName + " будет телепортирован на спавн региона: " + region);
            targetPlayer.sendMessage("Вы были телепортированы на спавн региона: " + region + " по запросу игрока " + commandSender.getName() + ".");
            // TODO: Добавьте логику для телепортации targetPlayer в спавн региона

        } else {
            // Если аргументов слишком много
            commandSender.sendMessage("Использование: /spawn [region] [player]");
        }


        return false;
    }
}
