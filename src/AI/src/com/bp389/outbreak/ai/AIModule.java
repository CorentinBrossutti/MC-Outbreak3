package com.bp389.outbreak.ai;

import com.bp389.outbreak.OutbreakModule;
import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.ai.entities.CustomEntityType;
import com.bp389.outbreak.ai.entities.zombie.EnhancedZombie;
import com.bp389.outbreak.ai.entities.zombie.ZombieArchetype;
import com.bp389.outbreak.ai.subcommands.VSCheckSubcommand;
import com.bp389.outbreak.ai.subcommands.VSCreateSubcommand;
import com.bp389.outbreak.ai.subcommands.VSDeleteSubcommand;
import com.bp389.outbreak.util.EntityUtil;
import com.google.common.collect.ImmutableMap;
import com.shampaggon.crackshot.events.WeaponShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public final class AIModule extends OutbreakModule {
    private File spawnerDir, archetypeDir;
    private boolean vspawnersInit;
    private double explosionDetectionRadius, gunfireDetectionRadius;

    public AIModule(JavaPlugin plugin, ConfigurationSection config) {
        super(plugin, config, "AI");

        spawnerDir = new File(directory, "vspawners/");
        archetypeDir = new File(directory, "archetypes/");

        spawnerDir.mkdirs();

        //embedded archetypes
        try {
            if(archetypeDir.mkdirs()){
                Files.copy(AIModule.class.getResourceAsStream("/EXAMPLE.yml"), new File(archetypeDir, "EXAMPLE.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_juggernaut.yml"), new File(archetypeDir, "zombie_juggernaut.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_nightchaser.yml"), new File(archetypeDir, "zombie_nightchaser.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_nightterror.yml"), new File(archetypeDir, "zombie_nightterror.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_runner.yml"), new File(archetypeDir, "zombie_runner.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_standard.yml"), new File(archetypeDir, "zombie_standard.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_striker.yml"), new File(archetypeDir, "zombie_striker.yml").toPath());
                Files.copy(AIModule.class.getResourceAsStream("/zombie_undying.yml"), new File(archetypeDir, "zombie_undying.yml").toPath());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Unable to copy embedded archetypes. Follows stacktrace :");
            e.printStackTrace();
        }

        explosionDetectionRadius = config.getDouble("hearing.explosion_detection_radius", 50.0);
        gunfireDetectionRadius = config.getDouble("hearing.gunfire_detection_radius", 35.0);
    }

    @Override
    protected Map<String, OutbreakSubcommand> registerCommands() {
        return ImmutableMap.of(
                "vscreate", new VSCreateSubcommand(plugin),
                "vscheck", new VSCheckSubcommand(),
                "vsdelete", new VSDeleteSubcommand()
        );
    }

    @Override
    public void onEnable() {
        plugin.getLogger().info("Registering entities...");
        CustomEntityType.registerEntities();

        plugin.getLogger().info("Registering zombies-related stuff...");
        EnhancedZombie.PLUGIN = plugin;
        EnhancedZombie.Config.setValues(config.getConfigurationSection("zombies"));
        ZombieArchetype.loadAll(archetypeDir);
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Deregistering entities");
        CustomEntityType.unregisterEntities();

        plugin.getLogger().info("Saving virtual spawners...");
        VirtualSpawner.saveAll(spawnerDir);
    }

    /*
    EVENTS
     */

    @EventHandler
    public void entityExplode(EntityExplodeEvent e) {
        CraftEntity ce = (CraftEntity)e.getEntity();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            EntityUtil.getNearby(ce.getHandle(), EnhancedZombie.class, explosionDetectionRadius).forEach(
                    zombie -> zombie.move(e.getLocation())
            );
        });
    }

    @EventHandler
    public void weaponShoot(WeaponShootEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
           EntityUtil.getNearby(((CraftPlayer)e.getPlayer()).getHandle(), EnhancedZombie.class, gunfireDetectionRadius).forEach(
                   zombie -> zombie.move(e.getPlayer().getLocation())
           );
        });
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        if(vspawnersInit)
            return;
        plugin.getLogger().info("Creating and starting virtual spawners on first player login.");
        VirtualSpawner.loadAll(plugin, spawnerDir);
        vspawnersInit = true;
    }
}
