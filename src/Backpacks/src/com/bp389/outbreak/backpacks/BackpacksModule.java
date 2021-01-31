package com.bp389.outbreak.backpacks;

import com.bp389.outbreak.OutbreakModule;
import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.backpacks.subcommands.GetSubcommand;
import com.bp389.outbreak.backpacks.subcommands.GiveSubcommand;
import com.bp389.outbreak.util.ChatUtil;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class BackpacksModule extends OutbreakModule {

    private Material backpack;
    private BackpacksDatabase database;

    public BackpacksModule(JavaPlugin plugin,
                           ConfigurationSection config) {
        super(plugin, config, "backpacks");

        database = new BackpacksDatabase(directory);
    }

    @Override
    protected Map<String, OutbreakSubcommand> registerCommands() {
        backpack = Material.getMaterial(config.getInt("item_id", 406));

        return ImmutableMap.of(
                "get", new GetSubcommand(backpack),
                "give", new GiveSubcommand(backpack)
        );
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Saving all backpacks to disk...");
        database.saveAll();
    }

    /*
    EVENTS
     */

    @EventHandler
    public void inventoryClick(final InventoryClickEvent e) {
        if(e.getClick() == ClickType.RIGHT && e.getCurrentItem().getType() == backpack) {
            final Player p = (Player) e.getWhoClicked();
            p.openInventory(database.backpackOf(p));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerDie(final PlayerDeathEvent e) {
        if(config.getBoolean("clear_on_die", true))
            database.clearBackpackOf(e.getEntity());
    }
}
