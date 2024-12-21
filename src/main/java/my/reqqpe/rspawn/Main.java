package my.reqqpe.rspawn;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import my.reqqpe.rspawn.CommandPlugin.rspawn;
import my.reqqpe.rspawn.CommandPlugin.spawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getCommand("rspawn").setExecutor(new rspawn(this));
        getCommand("spawn").setExecutor(new spawn(this));
        getServer().getPluginManager().registerEvents(new Event(this), this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
    }

    public Location getGlobalSpawn() {
        if (!getConfig().contains("spawns.global")) return null;

        World world = Bukkit.getWorld(getConfig().getString("spawns.global.world"));
        double x = getConfig().getDouble("spawns.global.x");
        double y = getConfig().getDouble("spawns.global.y");
        double z = getConfig().getDouble("spawns.global.z");
        float yaw = (float) getConfig().getDouble("spawns.global.yaw");
        float pitch = (float) getConfig().getDouble("spawns.global.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public Location getRegionSpawn(String region) {
        String path = "spawns.regions." + region;
        if (!getConfig().contains(path)) {getLogger().info("region null"); return null;}

        World world = Bukkit.getWorld(getConfig().getString(path + ".world"));
        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float yaw = (float) getConfig().getDouble(path + ".yaw");
        float pitch = (float) getConfig().getDouble(path + ".pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    public boolean isStuck(Location location) {
        // Проверяем текущий блок и блок над головой
        boolean inSolidBlock = location.getBlock().getType().isSolid();
        boolean headBlocked = location.clone().add(0, 1, 0).getBlock().getType().isSolid();

        // Ноги игрока должны быть НЕ в твёрдом блоке
        boolean feetFree = !location.clone().add(0, -1, 0).getBlock().getType().isSolid();

        return inSolidBlock && headBlocked && feetFree;
    }

    /**
     * Поиск безопасного местоположения выше текущей позиции
     */
    public Location findSafeLocation(Location location) {
        Location safeLocation = location.clone();

        // Проверяем блоки выше текущей позиции (до 10 блоков вверх)
        int max = getConfig().getInt("settings.tp-maxBlock", 10);
        for (int i = 0; i < max; i++) {
            safeLocation.add(0, 1, 0);

            // Условие для безопасного места: текущий блок не твёрдый, а над ним пустое пространство
            if (!safeLocation.getBlock().getType().isSolid() &&
                    !safeLocation.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                return safeLocation;
            }
        }

        // Если безопасное место не найдено
        return null;
    }

    public boolean checkRegionInServer(String regionName) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        boolean hasregion = false;

        for (World world : getServer().getWorlds()) {
            RegionManager regions = container.get(BukkitAdapter.adapt(world));
            if (regions !=null) {
                if (regions.hasRegion(regionName)) {
                    hasregion = true;
                }
            }
        }
        return hasregion;
    }
    public String getRegion(Player player, ConfigurationSection section) {
        Location location = player.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

        if (regionManager == null) {
            getLogger().info("Нет регионов в этом мире");
            return null; // Нет регионов в этом мире
        }

        // Получить регионы, покрывающие текущее местоположение игрока
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        // Получить список регионов из конфигурации
        ConfigurationSection regionsConfig = section.getConfigurationSection("regions");
        if (regionsConfig == null) {
            getLogger().info("Ни одного региона не настроено в конфигурации");
            return null; // Регионы не настроены
        }

        Set<String> configuredRegions = regionsConfig.getKeys(false);
        // Проверить регионы по приоритету
        for (ProtectedRegion region : regionSet) {
            if (configuredRegions.contains(region.getId())) {
                // Возвращаем данные региона
                return region.getId();

            }
        }

        return null; // Игрок не в разрешенном регионе
    }
}
