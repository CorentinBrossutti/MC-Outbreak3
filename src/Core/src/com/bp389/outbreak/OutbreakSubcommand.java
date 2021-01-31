package com.bp389.outbreak;

import org.bukkit.entity.Player;

public abstract class OutbreakSubcommand {
    protected String[] help;

    public OutbreakSubcommand(String[] help) {
        this.help = help;
    }

    public String[] getHelp() {
        return help;
    }

    public abstract String getPermission();

    public abstract boolean process(Player sender, String[] args);

    public abstract int minArguments();
}
