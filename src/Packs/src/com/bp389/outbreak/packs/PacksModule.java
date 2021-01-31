package com.bp389.outbreak.packs;

import com.bp389.outbreak.OutbreakModule;
import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.packs.subcommands.CreateSubcommand;
import com.bp389.outbreak.packs.subcommands.DeleteSubcommand;
import com.bp389.outbreak.util.ChatUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static com.bp389.outbreak.translate.Translator.tr;

public final class PacksModule extends OutbreakModule {

    private File lppDir, packTypesDir;
    private boolean pointsInit;

    public final List<UUID> editingPlayers = new ArrayList<>();

    public PacksModule(JavaPlugin plugin, ConfigurationSection config) {
        super(plugin, config, "packs");

        lppDir = new File(directory, "loot_pack_points");
        packTypesDir = new File(directory, "pack_types");

        lppDir.mkdirs();
        if (packTypesDir.mkdirs()) {
            try {
                Files.copy(PacksModule.class.getResourceAsStream("/EXAMPLE.yml"),
                           new File(packTypesDir, "EXAMPLE.yml").toPath());
                Files.copy(PacksModule.class.getResourceAsStream("/pack_kitchen.yml"),
                           new File(packTypesDir, "pack_kitchen.yml").toPath());
                Files.copy(PacksModule.class.getResourceAsStream("/RARITY_HELP.txt"),
                           new File(packTypesDir, "RARITY_HELP.txt").toPath());
            } catch (IOException e) {
                plugin.getLogger().warning("Unable to copy embedded pack types and archetypes. Follows stacktrace : ");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Map<String, OutbreakSubcommand> registerCommands() {
        return ImmutableMap.of(
                "create", new CreateSubcommand(plugin),
                "delete", new DeleteSubcommand(this)
        );
    }

    @Override
    public void onEnable() {
        plugin.getLogger().info("Loading pack types");
        PackType.loadDir(plugin, packTypesDir);
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Saving up loot-pack-points");
        LootPackPoint.POINTS.forEach(lpp -> lpp.save(lppDir));
    }

    /*
    EVENTS
     */

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        if(pointsInit)
            return;
        plugin.getLogger().info("Loading loot-pack-points at first player connection");
        LootPackPoint.loadDir(plugin, lppDir);
        pointsInit = true;
    }

    @EventHandler
    public void playerBreakChest(BlockBreakEvent event) {
        if(!editingPlayers.contains(event.getPlayer().getUniqueId()))
            return;
        for (LootPackPoint lpp : LootPackPoint.POINTS) {
            if (lpp instanceof RespawnableChest && lpp.getLocation().equals(event.getBlock().getLocation())) {
                ChatUtil.tell(event.getPlayer(), tr("chest-deleted"));
                lpp.delete();
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void playerDeleteGroundItem(PlayerArmorStandManipulateEvent event) {
        if(!editingPlayers.contains(event.getPlayer().getUniqueId()))
            return;
        EntityArmorStand stand = ((CraftArmorStand)event.getRightClicked()).getHandle();
        if(!(stand instanceof LootableArmorStand))
            return;

        ((LootableArmorStand)stand).getPoint().delete();
        ChatUtil.tell(event.getPlayer(), tr("ground-item-deleted"));
        event.setCancelled(true);
    }
}
