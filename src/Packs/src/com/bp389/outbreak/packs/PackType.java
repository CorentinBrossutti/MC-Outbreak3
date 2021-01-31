package com.bp389.outbreak.packs;

import com.bp389.outbreak.packs.items.AbstractPackItem;
import com.bp389.outbreak.packs.items.PackItem;
import com.bp389.outbreak.packs.items.PackWeapon;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

public class PackType {

    public static final HashMap<String, PackType> TYPES = new HashMap<>();

    public AbstractPackItem[] contents;
    public int respawnDelay;
    public String name;

    protected List<AbstractPackItem> weightedContents;

    private PackType(){}

    public PackType(AbstractPackItem[] contents, int respawnDelay) {
        this.contents = contents;
        this.respawnDelay = respawnDelay;

        createWeightedContents();
    }

    private void createWeightedContents() {
        weightedContents = new ArrayList<>();
        Arrays.stream(contents).forEach(
                content -> {
                    for (int i = 0; i < content.rarity.weightValue; i++) {
                        weightedContents.add(content);
                    }
                }
        );
    }

    public AbstractPackItem getRandomWeightedContent(Random rng) {
        return weightedContents.get(rng.nextInt(weightedContents.size() - 1));
    }

    /**
     * Load a pack type from a yaml configuration
     * Note that it does not register it and the pack type has no name
     * not recommended for direct use
     */
    static PackType loadYaml(JavaPlugin plugin, YamlConfiguration configuration){


        PackType type = new PackType();
        type.respawnDelay = configuration.getInt("respawn_delay", 600);

        ConfigurationSection contents = configuration.getConfigurationSection("contents"), temp;
        Set<String> keys = contents.getKeys(false);

        int i = 0;
        type.contents = new AbstractPackItem[keys.size()];

        for (String key : keys)
            type.contents[i++] = PackItem.fromConfig(plugin, contents.getConfigurationSection(key));

        type.createWeightedContents();
        return type;
    }

    /**
     * Load a pack type from a yaml configuration yaml
     * It also registers it and consequently returns any already registered pack type
     */
    public static PackType loadFile(JavaPlugin plugin,  File file) throws IOException, InvalidConfigurationException {
        String name = FilenameUtils.removeExtension(file.getName()).split("_", 2)[1];
        if(TYPES.containsKey(name))
            return TYPES.get(name);

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(file);

        PackType packType = loadYaml(plugin, configuration);
        packType.name = name;
        TYPES.put(name, packType);

        return packType;
    }

    /**
     * Loads and registers all pack types from configuration files in the given directory
     * @param plugin Used to log errors
     */
    public static void loadDir(JavaPlugin plugin, File directory) {
        try {
            for (File file : directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("pack_") && name.endsWith(".yml");
                }
            }))
                loadFile(plugin, file);
        } catch (Exception e) {
            plugin.getLogger().severe("Unable to load pack types from directory + " + directory.getName() + ". Follows stacktrace : ");
            e.printStackTrace();
        }
    }

    /**
     * Useful for configuration related stuff
     * @return A pack type from its name if it is registered, null otherwise
     */
    public static PackType get(String name) {
        return TYPES.get(name);
    }
}