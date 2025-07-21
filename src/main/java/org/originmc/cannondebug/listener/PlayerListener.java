package org.originmc.cannondebug.listener;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.block.Block;
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
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.User;
import org.originmc.cannondebug.cmd.CmdHistoryID;
import org.originmc.cannondebug.cmd.CommandExecutor;
import org.originmc.cannondebug.utils.DisplayCreatorBuilder;
import xyz.fragmentmc.uiwrapper.FancyMessage;

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
    public void interactVisual(PlayerInteractEntityEvent event) {
        System.out.println("here");
        Entity entity = event.getRightClicked();
        Pair<BlockSelection, Integer> result = DisplayCreatorBuilder.selectionFromEntity(CannonDebugRebornPlugin.getInstance(), entity);

        FancyMessage message = CmdHistoryID.getTickMessage(result.first(), result.first().getTracker().getLocationHistory().getFirst(), result.first().getTracker(), result.right());

        FancyPager pager = new FancyPager("History for selection ID: " + result.first().getId(), new FancyMessage[]{message});
        CommandExecutor.send(event.getPlayer(), pager, 0);
    }

}
