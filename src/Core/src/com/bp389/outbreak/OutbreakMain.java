package com.bp389.outbreak;

import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class OutbreakMain extends JavaPlugin {

    /**
     * The main plugin. Do not mess.
     */
    public static final HashMap<String, OutbreakModule> MODULES = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            Configuration.init();
            Translator.load(new File(Configuration.DIR_LANGUAGES, Configuration.SETTINGS.getString("language", "en") + ".txt"));
            Configuration.Modules.getEnabledModules(this).forEach(module -> {
                module.onEnable();
                MODULES.put(module.getName().toLowerCase(), module);
            });
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("Unable to startup plugin due to unexpected error. Follows stacktrace :");
            e.printStackTrace();
            getLogger().severe("Outbreak will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        for(OutbreakModule module : MODULES.values())
            module.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("outbreak"))
            return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    ChatColor.RED + Translator.tr("connect-as-player"));
            return true;
        }

        if(args.length < 2)
            return false;
        String module = args[0].toLowerCase();
        Player player = (Player) sender;
        if (module.equalsIgnoreCase("core")) {
            return true;
        } else if (!MODULES.containsKey(module)) {
            ChatUtil.warn(player, Translator.tr("unfound-module"));
            ChatUtil.warn(player, "core");
            MODULES.keySet().forEach(mname -> ChatUtil.warn(player, mname));
            return true;
        }

        MODULES.get(args[0].toLowerCase()).processCommand((Player) sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }
}
