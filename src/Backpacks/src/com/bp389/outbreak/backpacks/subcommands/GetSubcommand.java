package com.bp389.outbreak.backpacks.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.backpacks.BackpacksSubcommand;
import com.bp389.outbreak.translate.Translator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GetSubcommand extends BackpacksSubcommand {

    public GetSubcommand(Material backpack) {
        super(backpack, new String[]{
                "get",
                Translator.tr("sc-get-help2")
        });
    }

    @Override
    public String getPermission() {
        return "outbreak.backpacks.get";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        sender.getInventory().addItem(new ItemStack(backpack));
        return true;
    }

    @Override
    public int minArguments() {
        return 0;
    }
}
