package com.bp389.outbreak.packs.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.packs.PacksModule;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.bp389.outbreak.translate.Translator.tr;

public final class DeleteSubcommand extends OutbreakSubcommand {

    private PacksModule packsModule;

    public DeleteSubcommand(PacksModule module) {
        super(new String[]{
                "delete",
                tr("sc-delete-help2"),
                tr("sc-delete-help3"),
                tr("sc-delete-help4")
        });

        this.packsModule = module;
    }

    @Override
    public String getPermission() {
        return "outbreak.packs.delete";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        UUID uuid = sender.getUniqueId();
        if (packsModule.editingPlayers.contains(uuid)) {
            ChatUtil.tell(sender, tr("not-deleting"));
            packsModule.editingPlayers.remove(uuid);
        } else {
            ChatUtil.tell(sender, tr("deleting"));
            packsModule.editingPlayers.add(uuid);
        }
        return true;
    }

    @Override
    public int minArguments() {
        return 0;
    }
}
