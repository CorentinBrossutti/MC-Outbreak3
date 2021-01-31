package com.bp389.outbreak.packs;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GroundItem extends LootPackPoint {

    protected LootableArmorStand stand;
    protected JavaPlugin plugin;

    public GroundItem(JavaPlugin plugin, Location location, PackType type) {
        super(plugin, location, type);
        this.plugin = plugin;
    }

    @Override
    public void save(File directory) {
        super.save(directory);
        stand.die();
    }

    @Override
    public void delete() {
        super.delete();
        stand.die();
    }

    @Override
    public String specification() {
        return "ground_item";
    }

    @Override
    public void generate() {
        if(stand == null || stand.dead)
            stand = new LootableArmorStand(this);

        stand.setLoot(type.getRandomWeightedContent(RNG).get());
    }
}
