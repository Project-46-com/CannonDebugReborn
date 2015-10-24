/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.originmc.cdebug.cmd;

import mkremins.fanciful.FancyMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.originmc.cdebug.BlockSelection;
import org.originmc.cdebug.CannonDebug;
import org.originmc.cdebug.EntityTracker;
import org.originmc.cdebug.FancyPager;
import org.originmc.cdebug.utils.EntityUtils;
import org.originmc.cdebug.utils.NumberUtils;

import java.util.ArrayList;

import static org.bukkit.ChatColor.*;

public final class CmdHistoryTick extends CommandExecutor {

    public CmdHistoryTick(CannonDebug plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Do nothing if the command has invalid arguments.
        if (args.length == 2) return false;

        // Do nothing if the user input an invalid id.
        ArrayList<FancyMessage> lines = new ArrayList<>();
        int tick = Math.abs(NumberUtils.parseInt(args[2]));
        for (BlockSelection selection : user.getSelections()) {
            // Do nothing if tracker is null.
            EntityTracker tracker = selection.getTracker();
            if (tracker == null) {
                continue;
            }

            // Do nothing if tracker is not within this current server tick.
            if (tracker.getSpawnTick() > tick || (tracker.getDeathTick() != -1 && tracker.getDeathTick() < tick)) {
                continue;
            }

            // Generate a new fancy message line to add to the pager.
            int relativeTick = (int) (tick - tracker.getSpawnTick());
            Location initial = tracker.getLocationHistory().get(0);
            Location location = tracker.getLocationHistory().get(relativeTick);
            Vector velocity = tracker.getVelocityHistory().get(relativeTick);
            lines.add(new FancyMessage("ID: " + selection.getId() + " ")
                            .color(GRAY)
                            .formattedTooltip(
                                    new FancyMessage("Click for all history on this ID.")
                                            .color(DARK_AQUA)
                                            .style(BOLD),

                                    new FancyMessage("Spawned tick: ")
                                            .color(YELLOW)
                                            .then("" + tracker.getSpawnTick())
                                            .color(AQUA),

                                    new FancyMessage("Death tick: ")
                                            .color(YELLOW)
                                            .then((tracker.getDeathTick() == -1 ? "Still alive" : "" + tracker.getDeathTick()))
                                            .color(RED),

                                    new FancyMessage("Cached tick: ")
                                            .color(YELLOW)
                                            .then("" + plugin.getCurrentTick())
                                            .color(GREEN),

                                    new FancyMessage("Initial Location: ")
                                            .color(YELLOW)
                                            .then(initial.getBlockX() + " " + initial.getBlockY() + " " + initial.getBlockZ())
                                            .color(GRAY)
                            )

                            .command("/cannondebug h i " + selection.getId())

                            .then(EntityUtils.getFriendlyName(tracker.getEntityType()))
                            .color(YELLOW)

                            .then(" | ")
                            .color(DARK_GRAY)

                            .then("Hover for location and velocity")
                            .color(WHITE)
                            .formattedTooltip(
                                    new FancyMessage("LOCATION").color(YELLOW).style(BOLD),
                                    new FancyMessage("X: ").color(WHITE).then("" + location.getX()).color(RED),
                                    new FancyMessage("Y: ").color(WHITE).then("" + location.getY()).color(RED),
                                    new FancyMessage("Z: ").color(WHITE).then("" + location.getZ()).color(RED),
                                    new FancyMessage(""),
                                    new FancyMessage("VELOCITY").color(YELLOW).style(BOLD),
                                    new FancyMessage("X: ").color(WHITE).then("" + velocity.getX()).color(RED),
                                    new FancyMessage("Y: ").color(WHITE).then("" + velocity.getY()).color(RED),
                                    new FancyMessage("Z: ").color(WHITE).then("" + velocity.getZ()).color(RED)
                            )
            );
        }

        // Send user the pager messages.
        FancyPager pager = new FancyPager("History for server tick: " + tick, lines.toArray(new FancyMessage[lines.size()]));
        send(pager, 0);
        return true;
    }

}