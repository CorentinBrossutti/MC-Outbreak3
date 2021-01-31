package com.bp389.outbreak.packs;

import com.bp389.outbreak.util.YamlUtil;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract mother class for all loot points (respawnable objects, ground items...)
 */
public abstract class LootPackPoint {

    public static final List<LootPackPoint> POINTS = new ArrayList<>();
    protected static final Random RNG = new Random();

    protected Location location;
    protected PackType type;
    protected File file;
    protected boolean shouldSave = true;
    protected RespawnTask respawnTask;

    public LootPackPoint(JavaPlugin plugin, Location location, PackType type) {
        this.location = location;
        this.type = type;

        respawnTask = new RespawnTask();
        respawnTask.runTaskTimer(plugin, 0, 5 * 20L);
    }

    public Location getLocation() {
        return location;
    }

    public PackType getType() {
        return type;
    }

    public void delete() {
        respawnTask.cancel();
        if(file != null)
            file.delete();
        shouldSave = false;
    }

    public void save(File directory) {
        if (file != null || !shouldSave)
            return;

        file = new File(directory, "lpp_" + directory.listFiles().length + ".yml");

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("what", specification());
        YamlUtil.setLocation(configuration, "location", location);
        configuration.set("pack_type", type.name);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return The specification of this loot pack point
     */
    public abstract String specification();

    /**
     * Generate this loot point
     */
    public abstract void generate();

    //modify here to add new points type
    public static LootPackPoint getInstance(JavaPlugin plugin, String category, Location location, PackType type) {
        LootPackPoint lootPackPoint = null;
        switch (category) {
            case "chest":
                lootPackPoint = new RespawnableChest(plugin, location, type);
                break;
            case "ground_item":
                lootPackPoint = new GroundItem(plugin, location, type);
        }
        if(lootPackPoint != null)
            POINTS.add(lootPackPoint);
        return lootPackPoint;
    }

    /**
     * Loads a loot-pack-point from a yaml configuration
     * Be careful to not load the same points twice
     */
    static LootPackPoint loadYaml(JavaPlugin plugin, YamlConfiguration configuration) {
        LootPackPoint out = getInstance(plugin,
                                        configuration.getString("what"),
                                        YamlUtil.getLocation(configuration, "location", null),
                                        PackType.get(configuration.getString("pack_type")));
        if(out == null)
            plugin.getLogger().warning("Invalid / unrecognized pack category : " + configuration.getString("what"));
        return out;
    }

    /**
     * Loads a loot-pack-point from its yaml configuration save file
     * Be careful to not load the same points twice
     */
    public static LootPackPoint loadFile(JavaPlugin plugin, File file)
            throws IOException, InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(file);

        LootPackPoint lpp = loadYaml(plugin, configuration);
        lpp.file = file;
        POINTS.add(lpp);

        return lpp;
    }

    /**
     * Loads all loot-pack-points from all yaml save files in the given directory
     * Beware to not load the same points twice
     */
    public static void loadDir(JavaPlugin plugin, File directory) {
        try {
            for (File file : directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("lpp_") && name.endsWith(".yml");
                }
            }))
                loadFile(plugin, file);
        } catch (Exception e) {
            plugin.getLogger().severe("Unable to load loot pack points from directory " + directory.getName() + ". Follows stacktrace : ");
            e.printStackTrace();
        }

    }

    private final class RespawnTask extends BukkitRunnable {

        @Override
        public void run() {
            generate();
        }
    }

}
