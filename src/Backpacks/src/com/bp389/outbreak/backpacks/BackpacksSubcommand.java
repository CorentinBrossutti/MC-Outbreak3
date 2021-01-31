package com.bp389.outbreak.backpacks;

import com.bp389.outbreak.OutbreakSubcommand;
import org.bukkit.Material;

public abstract class BackpacksSubcommand extends OutbreakSubcommand {

    protected Material backpack;

    public BackpacksSubcommand(Material backpack, String[] help) {
        super(help);

        this.backpack = backpack;
    }
}
