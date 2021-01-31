package com.bp389.outbreak;

import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.bp389.outbreak.translate.Translator.tr;

public abstract class OutbreakModule implements Listener {
    protected JavaPlugin plugin;
    //configuration directory
    protected File directory;
    protected String name;
    protected ConfigurationSection config;
    protected Map<String, OutbreakSubcommand> commands;

    public OutbreakModule(JavaPlugin plugin, ConfigurationSection config, String name) {
        this.plugin = plugin;
        this.name = name;
        this.config = config;

        directory = new File(Configuration.DIR_ROOT, name + "/");
        directory.mkdirs();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        commands = registerCommands();
    }

    public String getName() {
        return name;
    }

    protected Map<String, OutbreakSubcommand> registerCommands() {
        return new HashMap<>();
    }

    public void processCommand(Player sender, String[] args){
        if (!commands.containsKey(args[0])) {
            ChatUtil.alert(sender, tr("unfound-subcommand"));
            commands.keySet().forEach(
                    command -> ChatUtil.alert(sender, command)
            );
            return;
        }

        OutbreakSubcommand cmd = commands.get(args[0]);

        if (args.length - 1 < cmd.minArguments()) {
            ChatUtil.alert(sender, tr("missing-arguments"));
            Arrays.stream(cmd.getHelp()).forEach(
                    line -> ChatUtil.alert(sender, line)
            );
            return;
        }

        if (!sender.hasPermission(cmd.getPermission())) {
            ChatUtil.alert(sender, tr("permission-denied"));
            return;
        }

        if (!cmd.process(sender, Arrays.copyOfRange(args, 1, args.length))) {
            ChatUtil.alert(sender, tr("invalid-cmd-usage"));
            ChatUtil.alert(sender, "============");
            Arrays.stream(cmd.getHelp()).forEach(
                    line -> ChatUtil.alert(sender, line)
            );
            ChatUtil.alert(sender, "============");
        }
    }

    public void onEnable(){}

    public void onDisable(){}
}
