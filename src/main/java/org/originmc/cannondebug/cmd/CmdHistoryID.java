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

package org.originmc.cannondebug.cmd;

import com.plotsquared.core.plot.flag.implementations.IceFormFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.utils.EnumUtils;
import org.originmc.cannondebug.utils.NumberUtils;
import xyz.fragmentmc.uiwrapper.FancyMessage;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public final class CmdHistoryID extends CommandExecutor {

    public CmdHistoryID(CannonDebugRebornPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Do nothing if the command has invalid arguments.
        if (args.length == 2) return false;

        // Do nothing if the user input an invalid id.
        int id = Math.abs(NumberUtils.parseInt(args[2]));
        BlockSelection selection = user.getSelection(id);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You have input an invalid id!");
            return true;
        }

        // Generate a new fancy message line to add to the pager.
        List<FancyMessage> lines = new ArrayList<>();
        EntityTracker tracker = selection.getTracker();
        int lifespan = tracker.getLocationHistory().size();
        Location initial = tracker.getLocationHistory().get(0);
        for (int i = 0; i < lifespan; i++) {
            Location location = tracker.getLocationHistory().get(i);
            Vector velocity = tracker.getVelocityHistory().get(i);
            FancyMessage message = new FancyMessage("Tick: " + i + " ")
                    .color(GRAY)
                    .formattedTooltip(
                            new FancyMessage("Click for all history on this tick.")
                                    .color(DARK_AQUA)
                                    .style(BOLD),

                            new FancyMessage("Server tick: ")
                                    .color(YELLOW)
                                    .then("" + (tracker.getSpawnTick() + i))
                                    .color(LIGHT_PURPLE),

                            new FancyMessage("Spawned tick: ")
                                    .color(YELLOW)
                                    .then("" + tracker.getSpawnTick())
                                    .color(AQUA),

                            new FancyMessage("Order: ")
                                    .color(YELLOW)
                                    .then("" + selection.getOrder())
                                    .color(LIGHT_PURPLE),

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

                    .command("/cannondebug h t " + (tracker.getSpawnTick() + i))

                    .then(EnumUtils.getFriendlyName(tracker.getEntityType()))
                    .color(YELLOW)

                    .then(" | ")
                    .color(DARK_GRAY)

                    .then("Hover for location and velocity")
                    .color(WHITE)
                    .formattedTooltip(
                            new FancyMessage("Click here to teleport to this location").color(DARK_AQUA).style(BOLD),
                            new FancyMessage("LOCATION").color(YELLOW).style(BOLD),
                            new FancyMessage("X: ").color(WHITE).then("" + location.getX()).color(RED),
                            new FancyMessage("Y: ").color(WHITE).then("" + location.getY()).color(RED).then(" (Eye: " + (location.getY() + 0.833) + ")").color(GRAY),
                            new FancyMessage("Z: ").color(WHITE).then("" + location.getZ()).color(RED),
                            new FancyMessage(""),
                            new FancyMessage("VELOCITY").color(YELLOW).style(BOLD),
                            new FancyMessage("X: ").color(WHITE).then("" + velocity.getX()).color(RED),
                            new FancyMessage("Y: ").color(WHITE).then("" + velocity.getY()).color(RED),
                            new FancyMessage("Z: ").color(WHITE).then("" + velocity.getZ()).color(RED)
                    )
                    .command("/c tp " + location.getX() + " " + location.getY() + " " + location.getZ())

                    .then(" | [")
                    .color(DARK_GRAY)

                    .then("P")
                    .formattedTooltip(
                            new FancyMessage("Clicking this will allow you to copy the location."),
                            new FancyMessage("It will suggest a command from there you can copy it with ")
                                    .color(WHITE)
                                    .then("CTRL + C")
                                    .color(LIGHT_PURPLE)
                    )
                    .color(AQUA)
                    .suggest(location.getX() + " " + location.getY() + " " + location.getZ())

                    .then("M")
                    .formattedTooltip(
                            new FancyMessage("Clicking this will allow you to copy the velocity."),
                            new FancyMessage("It will suggest a command from there you can copy it with ")
                                    .color(WHITE)
                                    .then("CTRL + C")
                                    .color(AQUA)
                    )
                    .color(LIGHT_PURPLE)
                    .suggest(velocity.getX() + " " + velocity.getY() + " " + velocity.getZ());

            if (NumberUtils.isInsideCube(location.getX()) && NumberUtils.isInsideCube(location.getZ()) ||
                    Math.abs(velocity.getX()) != 0.0 && NumberUtils.isInsideCube(location.getX()) ||
                    Math.abs(velocity.getZ()) != 0.0 && NumberUtils.isInsideCube(location.getZ())) {
                message.then("X")
                        .color(GREEN)
                        .formattedTooltip(new FancyMessage("This location is within a block on the x or z axis"));
            }

            if (NumberUtils.isInsideCube(location.getY() + (double) 0.49F)) {
                message.then("Y")
                        .color(RED)
                        .formattedTooltip(new FancyMessage("This location is within a block on y axis"));
            }

            if (Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY() + velocity.getZ() * velocity.getZ()) >= 8.0D) {
                message.then("M")
                        .color(YELLOW)
                        .formattedTooltip(new FancyMessage("This entity is moving fast [>= 8.0, cannot swing]"));
            }

            message.then("]")
                    .color(DARK_GRAY);

            lines.add(message);
        }

        // Send user the pager messages.
        FancyPager pager = new FancyPager("History for selection ID: " + id, lines.toArray(new FancyMessage[lines.size()]));
        send(pager, 0);
        return true;
    }
}
