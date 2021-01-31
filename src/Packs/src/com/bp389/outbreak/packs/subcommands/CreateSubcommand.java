package com.bp389.outbreak.packs.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.packs.LootPackPoint;
import com.bp389.outbreak.packs.PackType;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

import static com.bp389.outbreak.translate.Translator.tr;

public final class CreateSubcommand extends OutbreakSubcommand {
    private JavaPlugin plugin;

    public CreateSubcommand(JavaPlugin plugin) {
        super(new String[]{
                tr("sc-create-help1"),
                tr("sc-create-help2"),
                tr("sc-create-help3"),
                tr("sc-create-help4"),
        });
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "outbreak.packs.create";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        Block target = sender.getTargetBlock((Set<Material>) null, 10);
        if (target == null) {
            ChatUtil.alert(sender, tr("sc-create-cursor-error"));
            return true;
        }

        PackType packType = PackType.get(args[1]);
        if (packType == null) {
            ChatUtil.alert(sender, tr("sc-create-unfound-ptype"));
            PackType.TYPES.keySet().forEach(
                    type -> ChatUtil.alert(sender, type)
            );
            return true;
        }

        Location loc = target.getLocation();
        loc.setYaw(sender.getLocation().getYaw());
        loc.setPitch(sender.getLocation().getPitch());
        if (LootPackPoint.getInstance(plugin, args[0], loc, packType) == null)
            ChatUtil.alert(sender, tr("sc-create-invalid-category"));
        return true;
    }

    @Override
    public int minArguments() {
        return 2;
    }
}
