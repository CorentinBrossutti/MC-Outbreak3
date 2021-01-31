package com.bp389.outbreak.ai.entities.zombie;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A zombie archetype with defined characteristics
 */
public final class ZombieArchetype {

    private static final Random PICKER = new Random();
    private static final List<ZombieArchetype> WEIGHTED_ARCHETYPES = new ArrayList<>();

    public boolean fearsSunlight, villager;
    public double speedmult, damage, health;
    public int persistence;

    private ZombieArchetype(){}

    /**
     * Loads an archetype from a configuration file
     * @return The archetype
     */
    public static ZombieArchetype fromConfig(YamlConfiguration config){
        ZombieArchetype archetype = new ZombieArchetype();
        archetype.fearsSunlight = config.getBoolean("fears_sunlight", false);
        archetype.villager = config.getBoolean("villager", false);
        archetype.speedmult = config.getDouble("speedmult", 1.0);
        archetype.damage = config.getDouble("damage", 3.0);
        archetype.health = config.getDouble("health", 20.0);
        archetype.persistence = config.getInt("persistence", 5);

        for (int i = 0; i < config.getInt("weight", 1); i++)
            WEIGHTED_ARCHETYPES.add(archetype);

        return archetype;
    }

    /**
     *
     * @return An instance of a standard, default zombie archetype
     */
    public static ZombieArchetype baseArchetype(){
        ZombieArchetype archetype = new ZombieArchetype();
        archetype.fearsSunlight = false;
        archetype.villager = false;
        archetype.speedmult = 1.0;
        archetype.damage = 3.0;
        archetype.health = 20.0;
        archetype.persistence = 5;

        return archetype;
    }

    /**
     * Loads all zombie archetypes from all files in a given directory
     */
    public static void loadAll(File directory){
        try{
            for(File file : directory.listFiles((dir, name) -> name.startsWith("zombie_") && name.endsWith(".yml"))){
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.load(file);
                fromConfig(yaml);

            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return A random (with weights) zombie archetype
     */
    public static ZombieArchetype randomWeighted(){
        if(WEIGHTED_ARCHETYPES.isEmpty())
            return baseArchetype();
        return WEIGHTED_ARCHETYPES.get(PICKER.nextInt(WEIGHTED_ARCHETYPES.size()));
    }

}
