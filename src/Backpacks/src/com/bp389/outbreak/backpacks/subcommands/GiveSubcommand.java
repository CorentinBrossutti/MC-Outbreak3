package com.bp389.outbreak.backpacks.subcommands;

import com.bp389.outbreak.backpacks.BackpacksSubcommand;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.bp389.outbreak.translate.Translator.*;

public final class GiveSubcommand extends BackpacksSubcommand {

    public GiveSubcommand(Material backpack) {
        super(backpack, new String[]{
                tr("sc-give-help1"),
                tr("sc-give-help2")
        });
    }

    @Override
    public String getPermission() {
        return "outbreak.backpack.give";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            ChatUtil.alert(sender, tr("unfound-playername"));
            return true;
        }
        target.getInventory().addItem(new ItemStack(backpack));
        return true;
    }

    @Override
    public int minArguments() {
        return 1;
    }
}
