package com.bp389.outbreak.ai.subcommands;

import com.bp389.outbreak.OutbreakSubcommand;
import com.bp389.outbreak.ai.VirtualSpawner;
import com.bp389.outbreak.translate.Translator;
import com.bp389.outbreak.util.ChatUtil;
import org.bukkit.entity.Player;

import static com.bp389.outbreak.translate.Translator.*;
import static com.bp389.outbreak.util.ChatUtil.*;

public class VSCheckSubcommand extends OutbreakSubcommand {

    public VSCheckSubcommand() {
        super(new String[]{
                tr("sc-vscheck-help1"),
                tr("sc-vscheck-help2")
        });
    }

    @Override
    public String getPermission() {
        return "outbreak.ai.vspawner.check";
    }

    @Override
    public boolean process(Player sender, String[] args) {
        VirtualSpawner virtualSpawner = VirtualSpawner.get(args[0]);
        if (virtualSpawner == null) {
            alert(sender, tr("unfound-vspawner"));
            return true;
        }
        tell(sender, "===== " + tr("sc-vscheck-result-header") + virtualSpawner.getName());
        tell(sender, tr("sc-vscheck-result-location") + virtualSpawner.getCenter());
        tell(sender, tr("sc-vscheck-result-radius") + virtualSpawner.getRadius());
        tell(sender, tr("sc-vscheck-result-factor") + virtualSpawner.getFactor());
        tell(sender, tr("sc-vscheck-result-zmax") + virtualSpawner.getMaxCount());
        tell(sender, tr("sc-vscheck-result-zcount") + virtualSpawner.getCount());
        tell(sender, tr("sc-vscheck-result-status") +
                (virtualSpawner.canSpawnMore() ? tr("sc-vscheck-result-status-active") : tr("sc-vscheck-result-status-inactive")));
        tell(sender, tr("sc-vscheck-result-cdkills") + virtualSpawner.getKillCountThreshold());
        tell(sender, tr("sc-vscheck-result-kills") + virtualSpawner.getKillCount());
        tell(sender, tr("sc-vscheck-result-cdvalue") + virtualSpawner.getSecondsToReset());
        tell(sender, "=====");
        return true;
    }

    @Override
    public int minArguments() {
        return 1;
    }
}
