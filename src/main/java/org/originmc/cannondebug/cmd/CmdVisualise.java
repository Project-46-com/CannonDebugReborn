package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.utils.DisplayCreatorBuilder;
import org.originmc.cannondebug.utils.NumberUtils;

public class CmdVisualise extends CommandExecutor {

    public CmdVisualise(CannonDebugRebornPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }


    // c v id <tick> <duration>
    @Override
    public boolean perform() {

        if(!(sender instanceof Player player)) return false;
        // Do nothing if not enough arguments.
        if (args.length == 1) return false;

        int id = Math.abs(NumberUtils.parseInt(args[1]));

        BlockSelection selection = user.getSelection(id);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You have input an invalid id!");
            return true;
        }

        EntityTracker tracker = selection.getTracker();


        Integer tick = -1; // All ticks
        Integer lifespan = 80;
        if (args.length == 3) {
            tick = Math.abs(NumberUtils.parseInt(args[2]));
        }
        if (args.length == 4) {
            lifespan = Math.abs(NumberUtils.parseInt(args[3]));
        }

        DisplayCreatorBuilder builder = new DisplayCreatorBuilder(plugin, player, selection.getId()).material(
                tracker.getEntityType() == EntityType.FALLING_BLOCK ? Material.SAND : Material.TNT
        ).lifespan(lifespan);

        if(tick == -1) {
            for (int i = 0; i < tracker.getLocationHistory().size(); i++) {
                builder.location(tracker.getLocationHistory().get(i)).tick(i).build();
            }
        } else {
            Location location = tracker.getLocationHistory().get(tick);
            builder.location(location).tick(tick).build();
        }

        return true;
    }

}
