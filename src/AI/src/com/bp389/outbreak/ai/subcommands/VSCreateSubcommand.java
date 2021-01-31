package com.bp389.outbreak.ai.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.ai.VirtualSpawner;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static com.bp389.outbreak.translate.Translator.tr;

public class VSCreateSubcommand extends OutbreakSubcommand {

    private JavaPlugin plugin;

    public VSCreateSubcommand(JavaPlugin plugin) {
        super(new String[]{
                tr("sc-vscreate-help1"),
                tr("sc-vscreate-help2"),
                tr("sc-vscreate-help3"),
                tr("sc-vscreate-help4"),
                tr("sc-vscreate-help5"),
                tr("sc-vscreate-help6"),
                tr("sc-vscreate-help7"),
                tr("sc-vscreate-help8"),
                tr("sc-vscreate-help9"),
                tr("sc-vscreate-help10")
        });

        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "outbreak.ai.vspawner.create";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        try {
            new VirtualSpawner(plugin,
                               sender.getLocation(),
                               args[0],
                               Integer.parseInt(args[1]),
                               Integer.parseInt(args[2]),
                               Integer.parseInt(args[3]),
                               Integer.parseInt(args[4]),
                               Integer.parseInt(args[5]));
        } catch (VirtualSpawner.SpawnerAlreadyExistsException e) {
            ChatUtil.alert(sender, "Ce spawner existe déjà. Choississez un autre nom.");
        } catch (NumberFormatException e) {
            ChatUtil.alert(sender, "Un des arguments numériques que vous avez donné est erroné. Vérifiez votre saisie.");
        }
        ChatUtil.tell(sender, tr("sc-vscreate-success"));
        return true;
    }

    @Override
    public int minArguments() {
        return 6;
    }
}
