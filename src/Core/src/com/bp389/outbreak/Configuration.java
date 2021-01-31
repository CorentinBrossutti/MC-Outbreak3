package com.bp389.outbreak;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Configuration {

    public static final YamlConfiguration SETTINGS = new YamlConfiguration();

    public static final File DIR_ROOT = new File("plugins/Outbreak/"),
        DIR_LANGUAGES = new File(DIR_ROOT, "languages/"),
        SETTINGS_FILE = new File(DIR_ROOT, "settings.yml");

    public static void init() throws IOException, InvalidConfigurationException {
        DIR_ROOT.mkdirs();
        if(!SETTINGS_FILE.exists())
            Files.copy(Configuration.class.getResourceAsStream("/settings.yml"), SETTINGS_FILE.toPath());
        SETTINGS.load(SETTINGS_FILE);

        //copy all languages files
        if(DIR_LANGUAGES.mkdirs()) {
            File en = new File(DIR_LANGUAGES, "en.txt"), fr = new File(DIR_LANGUAGES, "fr.txt"), de = new File(
                    DIR_LANGUAGES, "de.txt");
            Files.copy(Configuration.class.getResourceAsStream("/en.txt"), en.toPath());
            Files.copy(Configuration.class.getResourceAsStream("/fr.txt"), fr.toPath());
            Files.copy(Configuration.class.getResourceAsStream("/de.txt"), de.toPath());
        }

        Modules.init();
    }

    public static final class Modules {
        public static final File MODULES_CONFIG_FILE = new File(DIR_ROOT, "modules.yml");

        public static void init() throws IOException {
            if(!MODULES_CONFIG_FILE.exists())
                Files.copy(Configuration.Modules.class.getResourceAsStream("/modules.yml"), MODULES_CONFIG_FILE.toPath());
        }

        public static List<OutbreakModule> getEnabledModules(JavaPlugin plugin)
                throws IOException {
            List<OutbreakModule> modules = new ArrayList<>();
            YamlConfiguration config = new YamlConfiguration();
            try {
                config.load(MODULES_CONFIG_FILE);
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            for(String key : config.getKeys(false)){
                try {
                    Class<? extends OutbreakModule> clazz = (Class<? extends OutbreakModule>)
                            Class.forName("com.bp389.outbreak." + key.toLowerCase() + "." + key + "Module");
                    Constructor<OutbreakModule> constr = (Constructor<OutbreakModule>)
                            clazz.getDeclaredConstructor(JavaPlugin.class, ConfigurationSection.class);
                    ConfigurationSection moduleConf = config.getConfigurationSection(key);
                    if(moduleConf.contains("enabled")){
                        boolean enabled = (boolean)moduleConf.get("enabled");
                        if(!enabled){
                            plugin.getLogger().info("Module " + key + " not to be enabled.");
                            continue;
                        }
                        plugin.getLogger().info("Enabling module " + key);
                    }
                    else
                        plugin.getLogger().warning("Enabled configuration value not found for module " + key + ". Assuming true.");

                    OutbreakModule module = constr.newInstance(plugin, moduleConf);
                    modules.add(module);
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Module " + key + " not found. Skipping.");
                } catch (NoSuchMethodException e) {
                    plugin.getLogger().warning("Module " + key + " : invalid constructor. " +
                                                                   "Constructor must override parent and take JavaPlugin and ConfigurationSection as arguments.");
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    plugin.getLogger().severe("Error while enabling module " + key + ". Unknown, follows stacktrace.");
                    e.printStackTrace();
                } catch (ClassCastException e){
                    plugin.getLogger().severe("Module " + key + " does not extend OutbreakModule.");
                }
            }
            return modules;
        }
    }
}
