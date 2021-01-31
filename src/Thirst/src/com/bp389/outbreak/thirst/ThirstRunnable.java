package com.bp389.outbreak.thirst;

import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import static com.bp389.outbreak.translate.Translator.tr;

public strictfp final class ThirstRunnable extends BukkitRunnable {

    private JavaPlugin plugin;
    private Player player;
    private float percentPerTick;
    private int deathDelay;

    private boolean dehydrationImpending;

    public ThirstRunnable(JavaPlugin plugin, Player player, float percentPerTick, int deathDelay) {
        this.plugin = plugin;
        this.player = player;
        this.percentPerTick = percentPerTick;
        this.deathDelay = deathDelay;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        } else if (player.isDead())
            return;

        float result = ThirstModule.defill(player, percentPerTick);
        if(result <= 0F){
            if (!dehydrationImpending) {
                ChatUtil.alert(player, tr("dehydrated"));
                dehydrationImpending = true;
                new DieOfThirst().runTaskLater(plugin, deathDelay * 20L);
            }
            return;
        }
        else if(result <= 0.3F)
            ChatUtil.warn(player, tr("thirsty"));
        dehydrationImpending = false;
    }

    private final class DieOfThirst extends BukkitRunnable {

        @Override
        public void run() {
            if(player.getExp() == 0){
                ChatUtil.alert(player, tr("died-thirst"));
                player.setHealth(0);
            }
        }
    }
}
