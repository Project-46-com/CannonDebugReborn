package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.originmc.cannondebug.CannonDebugRebornPlugin;

public class CmdTracking extends CommandExecutor {

    public CmdTracking(CannonDebugRebornPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // In case a user gives everyone cannondebug.*
        if (!user.getBase().isOp()) {
            return false;
        }

        boolean tracking = !plugin.getConfiguration().alternativeTracking;
        plugin.getConfiguration().setBoolean("alternative-tracking", tracking);
        plugin.getConfiguration().alternativeTracking = tracking;

        if (tracking) {
            sender.sendMessage(ChatColor.GREEN + "Enabled Alternative Tracking");
        } else {
            sender.sendMessage(ChatColor.RED + "Disabled Alternative Tracking");
        }

        return true;
    }


}
