package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.NumberUtils;
import org.originmc.cannondebug.utils.PlotSquared;

public class CmdTp extends CommandExecutor {

    public CmdTp(CannonDebugRebornPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        if (args.length != 4) return false;

        Location location = new Location(
                user.getBase().getWorld(),
                NumberUtils.parseDouble(args[1]),
                NumberUtils.parseDouble(args[2]),
                NumberUtils.parseDouble(args[3])
        );

        // First we need to verify this user has tracked an entity to the provided location
        boolean found = false;
        for (BlockSelection selection : user.getSelections()) {
            EntityTracker entityTracker = selection.getTracker();

            if (entityTracker != null && entityTracker.getLocationHistory().contains(location)) {
                found = true;
                break;
            }
        }

        // If we found it in the history teleport there
        if (found) {
            Player player = user.getBase();

            if ((!PlotSquared.isEnabled() || PlotSquared.isPlotTrusted(user.getBase(), location))) {
                Location current = player.getLocation();
                location.setPitch(current.getPitch());
                location.setYaw(current.getYaw());
                user.getBase().teleport(location);
            } else {
                player.sendMessage(ChatColor.RED + "You cannot teleport here.");
            }

            return true;
        }

        return false;
    }


}
