package com.bp389.outbreak.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ChatUtil {

    public static void tell(Player player, String msg){
        player.sendMessage(ChatColor.GREEN + msg);
    }

    public static void warn(Player player, String msg){
        player.sendMessage(ChatColor.GOLD + msg);
    }

    public static void alert(Player player, String msg) {
        player.sendMessage(ChatColor.RED + msg);
    }
}
