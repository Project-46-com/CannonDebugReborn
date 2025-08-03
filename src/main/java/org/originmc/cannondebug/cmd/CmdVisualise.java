package org.originmc.cannondebug.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TNT;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemType;
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

        boolean allSelections = args[1].equalsIgnoreCase("all");

        int id = Math.abs(NumberUtils.parseInt(args[1]));


        int tick = -1;
        int lifespan = 80;

        // Optional arguments
        if (args.length >= 3) {
            for (int i = 2; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("t:")) {
                    tick = Math.abs(NumberUtils.parseInt(arg.substring(2)));
                }
                if (arg.startsWith("l:") || arg.startsWith("d:")) {
                    lifespan = NumberUtils.parseInt(arg.substring(2));
                }
            }
        }

        // All selections
        if (id == 0 && allSelections) {
            for (BlockSelection selection : user.getSelections()) {
                display(selection, tick, lifespan, plugin, player);
            }
            return true;
        }

        // Specific selection
        BlockSelection selection = user.getSelection(id);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You have input an invalid id!");
            return true;
        }
        display(selection, tick, lifespan, plugin, player);
        return true;
    }


    private void display(BlockSelection selection, int tick, int lifespan,  CannonDebugRebornPlugin plugin, Player player) {
        EntityTracker tracker = selection.getTracker();
        if(tick == -1) {
            for (int i = 0; i < tracker.getLocationHistory().size(); i++) {
                new DisplayCreatorBuilder(plugin, player, selection.getId())
                        .blockData(tracker.getBlockData())
                        .lifespan(lifespan)
                        .location(tracker.getLocationHistory().get(i))
                        .tick(i)
                        .build();
            }
        } else {
            Location location = tracker.getLocationHistory().get(tick);
            new DisplayCreatorBuilder(plugin, player, selection.getId()).blockData(tracker.getBlockData())
                    .lifespan(lifespan).location(location).tick(tick).build();
        }
    }



}
