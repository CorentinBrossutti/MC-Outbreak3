package com.bp389.outbreak.ai.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.ai.VirtualSpawner;
import org.bukkit.entity.Player;

import static com.bp389.outbreak.translate.Translator.tr;
import static com.bp389.outbreak.util.ChatUtil.alert;
import static com.bp389.outbreak.util.ChatUtil.tell;

public class VSDeleteSubcommand extends OutbreakSubcommand {

    public VSDeleteSubcommand() {
        super(new String[]{
                tr("sc-vsdelete-help1"),
                tr("sc-vsdelete-help2")
        });
    }

    @Override
    public String getPermission() {
        return "outbreak.ai.vspawner.delete";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        VirtualSpawner virtualSpawner = VirtualSpawner.get(args[0]);
        if (virtualSpawner == null) {
            alert(sender, tr("unfound-vspawner"));
            return true;
        }
        virtualSpawner.delete();
        tell(sender, tr("sc-vsdelete-success"));
        return true;
    }

    @Override
    public int minArguments() {
        return 1;
    }
}
