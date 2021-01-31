package com.bp389.outbreak.thirst;

import com.bp389.outbreak.OutbreakModule;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class ThirstModule extends OutbreakModule {
    private float refillValue, percentPerTick;
    private int secondsBetweenTicks, deathDelay;

    public ThirstModule(JavaPlugin plugin, ConfigurationSection config) {
        super(plugin, config, "thirst");

        refillValue = (float)config.getDouble("refill_value", 0.8);
        percentPerTick = (float)config.getDouble("percent_per_tick", 0.05);
        secondsBetweenTicks = config.getInt("seconds_between_ticks", 75);
        deathDelay = config.getInt("death_delay", 30);
    }

    public static float refill(Player player, float amount) {
        float result = Math.min(player.getExp() + amount, 0.99F);
        player.setExp(result);
        return result;
    }

    public static float defill(Player player, float amount){
        float result = Math.max(player.getExp() - amount, 0F);
        player.setExp(result);
        return result;
    }

    /*
    EVENTS
     */

    @EventHandler
    public void playerDrink(final PlayerItemConsumeEvent e) {
        if(e.getItem().getType() == Material.POTION) {
            ChatUtil.tell(e.getPlayer(), Translator.tr("has-drunk"));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 0));
            refill(e.getPlayer(), refillValue);
        }
    }

    @EventHandler
    public void expPicked(final PlayerExpChangeEvent e) {
        e.setAmount(0);
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent e) {
        if(!e.getPlayer().hasPlayedBefore())
            refill(e.getPlayer(), 1);

        if(!e.getPlayer().hasPermission("outbreak.thirst.no"))
            new ThirstRunnable(plugin, e.getPlayer(), percentPerTick, deathDelay).
                    runTaskTimer(plugin, secondsBetweenTicks * 20L, secondsBetweenTicks * 20L);
    }

    @EventHandler
    public void playerDie(final PlayerDeathEvent e) {
        //refill thirst when player respawns
        //respawn event does not work
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            while(e.getEntity().isDead());
            Bukkit.getScheduler().runTaskLater(plugin, () -> refill(e.getEntity(), 1), 20L);
        });
    }
}
