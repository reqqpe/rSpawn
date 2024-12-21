package my.reqqpe.rspawn;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Objects;

public class Event implements Listener {
    private final Main plugin;

    public Event(Main plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(plugin.getConfig().getString("permission.tp-bypass", "rspawn.bypass"))) return;
        if (plugin.getConfig().getBoolean("settings.tp-onDeath", false)) {
            if (plugin.getGlobalSpawn() != null) {
                e.setRespawnLocation(plugin.getGlobalSpawn());
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e ){
        Player player = e.getPlayer();
        if (player.hasPermission(plugin.getConfig().getString("permission.tp-bypass", "rspawn.bypass"))) return;
        if (plugin.getConfig().getBoolean("settings.tp-onJoin", false)) {
            if (plugin.getGlobalSpawn() != null) {
                player.teleport(plugin.getGlobalSpawn());
            }
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (player.hasPermission(plugin.getConfig().getString("permission.tp-bypass", "rspawn.bypass"))) return;
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        // Проверка: игрок застрял, если он внутри твёрдого блока,
        // и оба блока (текущий и над головой) твёрдые
        if (plugin.getConfig().getBoolean("settings.tp-onBlock", false)) {
            if (plugin.isStuck(location)) {
                Location safeLocation = plugin.findSafeLocation(location);
                if (safeLocation != null) {
                    ConfigurationSection spawnsConfig = plugin.getConfig().getConfigurationSection("spawns");

                    String regionData = plugin.getRegion(player, spawnsConfig);

                    if (regionData == null) {
                        if (plugin.getGlobalSpawn() != null) {
                            player.teleport(plugin.getGlobalSpawn());
                        }
                        else {
                            player.teleport(safeLocation);
                        }
                    }
                    else {
                        if (plugin.getRegionSpawn(regionData) != null) {
                            player.teleport(Objects.requireNonNull(plugin.getRegionSpawn(regionData)));
                        }
                        else {
                            player.teleport(safeLocation);
                        }
                    }
                }
            }
        }


        if (plugin.getConfig().getBoolean("settings.tp-fall", false)) {
            if (location.getY() < plugin.getConfig().getInt("settings.tp-fall-y", -10)) {
                if (plugin.getGlobalSpawn() != null) {
                    player.teleport(plugin.getGlobalSpawn());
                }
            }
        }
    }
}
