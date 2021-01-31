package com.bp389.outbreak.ai;

import com.bp389.outbreak.ai.entities.zombie.EnhancedZombie;
import com.bp389.outbreak.util.YamlUtil;
import net.minecraft.server.v1_8_R3.Entity;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * A virtual spawner which greatly increases the spawn of zombies in a given area
 */
public final class VirtualSpawner {
    private static final HashMap<String, VirtualSpawner> SPAWNERS = new HashMap<>();

    private List<EnhancedZombie> zombies = new ArrayList<>();
    private JavaPlugin plugin;
    private Location center;
    private int killCount;
    private String name;
    private int radius, factor, maxCount, killCountThreshold, secondsToReset;
    private boolean active = true, shouldSave = true;
    private File file;

    public VirtualSpawner(JavaPlugin plugin, Location center, String name, int radius, int factor,
                          int maxCount, int killCountThreshold, int secondsToReset) throws SpawnerAlreadyExistsException {
        if(SPAWNERS.containsKey(name))
            throw new SpawnerAlreadyExistsException();

        this.plugin = plugin;
        this.center = center;
        this.name = name;
        this.radius = radius;
        this.factor = factor;
        this.maxCount = maxCount;
        this.killCountThreshold = killCountThreshold;
        this.secondsToReset = secondsToReset;

        SPAWNERS.put(name, this);
    }

    private VirtualSpawner(){}

    public Location getCenter() {
        return center;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getCount() {
        return zombies.size();
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public int getFactor() {
        return factor;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getKillCountThreshold() {
        return killCountThreshold;
    }

    public int getSecondsToReset() {
        return secondsToReset;
    }

    /**
     *
     * @return If the spawner is active
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Activates or deactivates a virtual spawner
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *
     * @return Whether this virtual spawner can spawn more zombies at the moment
     */
    public boolean canSpawnMore(){
        return isActive() && zombies.size() < maxCount && killCount < killCountThreshold;
    }

    /**
     * Adds a zombie attached to this virtual spawner
     */
    public void addZombie(EnhancedZombie zombie){
        if(!canSpawnMore())
            return;
        zombies.add(zombie);
    }

    /**
     * Removes a zombie attached to this spawner
     * @param killed Whether the zombie was killed (if yes, increases the killed count)
     */
    public void removeZombie(EnhancedZombie zombie, boolean killed){
        zombies.remove(zombie);
        if(killed){
            if(++killCount >= killCountThreshold)
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> killCount = 0, secondsToReset * 20L);
        }
    }

    public void delete() {
        if(file != null)
            file.delete();
        shouldSave = false;
        active = false;
        SPAWNERS.remove(name);
    }

    public void unload() {
        zombies.forEach(Entity::die);
        active = false;
    }

    public void unloadAndSave(File directory) throws IOException {
        unload();
        save(directory);
    }

    public void save(File directory) throws IOException {
        if(file != null || !shouldSave)
            return;
        file = new File(directory, "vspwn_" + name + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        YamlUtil.setLocation(config, "center", center);
        config.set("radius", radius);
        config.set("factor", factor);
        config.set("max_count", maxCount);
        config.set("kill_count_threshold", killCountThreshold);
        config.set("seconds_to_reset", secondsToReset);

        config.save(file);
    }

    public static void saveAll(File directory){
        SPAWNERS.values().forEach(vs -> {
            try {
                vs.unloadAndSave(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static VirtualSpawner loadSpawner(JavaPlugin plugin, String name, YamlConfiguration configuration, File source){
        VirtualSpawner virtualSpawner = new VirtualSpawner();
        virtualSpawner.plugin = plugin;
        virtualSpawner.name = name;
        virtualSpawner.center = YamlUtil.getLocation(configuration, "center", null);
        virtualSpawner.radius = configuration.getInt("radius");
        virtualSpawner.factor = configuration.getInt("factor");
        virtualSpawner.maxCount = configuration.getInt("max_count");
        virtualSpawner.killCountThreshold = configuration.getInt("kill_count_threshold");
        virtualSpawner.secondsToReset = configuration.getInt("seconds_to_reset");
        virtualSpawner.file = source;

        SPAWNERS.put(name, virtualSpawner);
        return virtualSpawner;
    }

    public static void loadAll(JavaPlugin plugin, File directory) {
        for(File file : directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("vspwn_") && name.endsWith(".yml");
            }
        })){
            YamlConfiguration yaml = new YamlConfiguration();
            try {
                yaml.load(file);
                loadSpawner(plugin, FilenameUtils.removeExtension(file.getName()).split("_", 2)[1], yaml, file);
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getLogger().severe("Could not load spawner. Follows stacktrace :");
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param from Source location
     * @return The first found nearby spawner, null if there is none
     */
    public static VirtualSpawner getNearbySpawner(Location from){
        for(VirtualSpawner vs : SPAWNERS.values()){
            if(vs.center.distanceSquared(from) <= vs.radius * vs.radius)
                return vs;
        }
        return null;
    }

    public static VirtualSpawner get(String name) {
        return SPAWNERS.get(name);
    }

    public static final class SpawnerAlreadyExistsException extends IllegalArgumentException {}

}
