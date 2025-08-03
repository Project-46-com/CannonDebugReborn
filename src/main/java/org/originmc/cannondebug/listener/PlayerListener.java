package org.originmc.cannondebug.listener;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.User;
import org.originmc.cannondebug.cmd.CmdHistoryID;
import org.originmc.cannondebug.cmd.CommandExecutor;
import org.originmc.cannondebug.utils.DisplayCreatorBuilder;
import xyz.fragmentmc.uiwrapper.FancyMessage;

import java.util.Optional;

public class PlayerListener implements Listener {

    private final CannonDebugRebornPlugin plugin;

    public PlayerListener(CannonDebugRebornPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void createUser(PlayerJoinEvent event) {
        plugin.getUsers().put(event.getPlayer().getUniqueId(), new User(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void deleteUser(PlayerQuitEvent event) {
        plugin.getUsers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void addSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = plugin.getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        plugin.handleSelection(user, block);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void removeSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = plugin.getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        plugin.handleSelection(user, block);
    }

    @EventHandler
    public void interactVisual(PlayerInteractEvent event) {
        if(!event.getHand().equals(EquipmentSlot.HAND)) return;
        if(!event.getAction().isLeftClick() && !event.getAction().isRightClick() || event.getHand() == null) return;
        trace(event.getPlayer())
                .flatMap(display -> DisplayCreatorBuilder.selectionFromEntity(CannonDebugRebornPlugin.getInstance(), display))
                .flatMap(this::createHistoryPager)
                .ifPresent(pager -> {
                    CommandExecutor.send(event.getPlayer(), pager, 0);
                    event.setCancelled(true);
                });
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        trace(event.getPlayer())
                .flatMap(display -> DisplayCreatorBuilder.selectionFromEntity(CannonDebugRebornPlugin.getInstance(), display))
                .flatMap(this::createHistoryPager)
                .ifPresent(pager -> {
                    CommandExecutor.send(event.getPlayer(), pager, 0);
                    event.setCancelled(true);
                });
    }

    private Optional<FancyPager> createHistoryPager(Pair<BlockSelection, Integer> result) {
        var tracker = result.first().getTracker();
        var location = tracker.getLocationHistory().getFirst();
        FancyMessage message = CmdHistoryID.getTickMessage(result.first(), location, tracker, result.right());

        return Optional.of(new FancyPager(
                "History for selection ID: " + result.first().getId(),
                new FancyMessage[]{message}
        ));
    }


    /**
     * Do a ray trace to find colliding BlockDisplays as they don't appear on interact events
     * @param player
     * @return a block display if one was found
     */
    private Optional<BlockDisplay> trace(Player player) {
        Location playerEyeLoc =  player.getEyeLocation();
        Vector direction = playerEyeLoc.getDirection();
        double distance = 4.5;

        RayTraceResult rayTraceResult = playerEyeLoc.getWorld().rayTrace(playerEyeLoc, direction, distance, FluidCollisionMode.NEVER, true, 1, entity -> entity instanceof BlockDisplay);
        if (rayTraceResult == null) return Optional.empty();
        Entity collidingEntity = rayTraceResult.getHitEntity();
        if(collidingEntity == null) return Optional.empty();
        if (!(collidingEntity instanceof BlockDisplay display)) return Optional.empty();
        return Optional.of(display);
    }

}
