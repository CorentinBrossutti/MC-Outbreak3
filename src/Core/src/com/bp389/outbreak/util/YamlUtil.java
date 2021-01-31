package com.bp389.outbreak.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class YamlUtil {

    /**
     * Quickly get a value from a yaml file
     * @param path Path of the value
     */
    public static Object quickGet(File file, String path, Object def){
        if(!file.getName().endsWith(".yml"))
            return def;

        YamlConfiguration conf = new YamlConfiguration();
        try {
            conf.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            return def;
        }
        return conf.get(path, def);
    }

    /**
     * Quickly get a value from a yaml file
     * @param path Path of the value
     */
    public static Object quickGet(File file, String path){
        return quickGet(file, path, null);
    }

    /**
     * Quickly saves a value to a yaml file
     * @param path Target path
     */
    public static void quickSave(File file, String path, Object object){
        YamlConfiguration conf = new YamlConfiguration();
        conf.set(path, object);
        try {
            if(!file.exists())
                file.createNewFile();
            conf.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a location in a configuration section because Bukkit does not natively support it
     * @param path Target path. Set null to get root config as target
     */
    public static void setLocation(ConfigurationSection config, String path, Location location){
        ConfigurationSection target = path == null ? config : (config.contains(path) ? config.getConfigurationSection(path) : config.createSection(path));
        target.set("world", location.getWorld().getUID().toString());
        target.set("x", location.getX());
        target.set("y", location.getY());
        target.set("z", location.getZ());
        target.set("yaw", location.getYaw());
        target.set("pitch", location.getPitch());
    }

    /**
     * Gets a location from its given path in config
     * @param path Source path. If null, it will take the given config section as root.
     * @param world If null, will attempt to retrieve the world from its UID in config ; otherwise it will use the one provided
     * @return Extracted location
     */
    public static Location getLocation(ConfigurationSection config, String path, World world){
        ConfigurationSection source = path == null ? config : config.getConfigurationSection(path);
        return new Location(
                world == null ? Bukkit.getWorld(UUID.fromString(source.getString("world"))) : world,
                source.getDouble("x"),
                source.getDouble("y"),
                source.getDouble("z"),
                (float) source.getDouble("yaw"),
                (float) source.getDouble("pitch")
        );
    }
}