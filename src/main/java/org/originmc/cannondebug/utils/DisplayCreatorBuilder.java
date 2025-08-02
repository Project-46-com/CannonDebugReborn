package org.originmc.cannondebug.utils;

import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugRebornPlugin;
import org.originmc.cannondebug.User;

import java.util.UUID;

@Getter
@Setter
public class DisplayCreatorBuilder {


    private final JavaPlugin plugin;
    private final Player player;
    private final int selectionID;
    private Location location;
    private BlockData blockData = Material.TNT.createBlockData();

    private int lifespan = 80;
    private boolean glowing = true;
    private float scale = 0.98f;
    private int tick = 1;


    public DisplayCreatorBuilder(JavaPlugin plugin, Player player, int selectionID) {
        this.plugin = plugin;
        this.player = player;
        this.selectionID = selectionID;
    }

    public DisplayCreatorBuilder tick(int tick) {
        this.tick = tick;
        return this;
    }

    public DisplayCreatorBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public DisplayCreatorBuilder blockData(BlockData blockData) {
        this.blockData = blockData;
        return this;
    }

    public DisplayCreatorBuilder lifespan(int lifespan){
        this.lifespan = lifespan;
        return this;
    }

    public DisplayCreatorBuilder glowing(boolean glowing){
        this.glowing = glowing;
        return this;
    }

    public DisplayCreatorBuilder scale(float scale){
        this.scale = scale;
        return this;
    }

    private void setPDC(Entity entity) {
        entity.getPersistentDataContainer().set(CannonDebugRebornPlugin.getInstance().getDebugKey(), PersistentDataType.STRING, this.player.getUniqueId() + ":" + this.tick + ":" + this.selectionID);
    }

    public static Pair<BlockSelection, Integer> selectionFromEntity(CannonDebugRebornPlugin plugin, Entity entity) {
        String[] data = entity.getPersistentDataContainer().get(plugin.getDebugKey(), PersistentDataType.STRING).split(":");
        if (data.length != 3) return null;
        UUID uuid = UUID.fromString(data[0]);
        Integer tick = Integer.parseInt(data[1]);
        Integer selectionID = Integer.parseInt(data[2]);

        User user = plugin.getUser(uuid);
       return Pair.of(user.getSelection(selectionID), tick);
    }


    private Transformation transformation;
    private BlockDisplay spawnedDisplay;
    private BukkitTask removeDisplayTask;

    public void build() {
        if (lifespan <= 0) {
            throw new IllegalArgumentException("Visualisation entity must have a lifespan > 0");
        }
        if (location == null || blockData == null) return;

        BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
        blockDisplay.setBlock(blockData);
        blockDisplay.setGlowing(glowing);
        blockDisplay.setNoPhysics(true);
        blockDisplay.setPersistent(false);

        Transformation finalTransform = this.transformation != null
                ? this.transformation
                : new Transformation(
                new Vector3f(-0.5f, 0, -0.5f),
                new Quaternionf(),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        );

        blockDisplay.setTransformation(finalTransform);
        this.spawnedDisplay = blockDisplay;

        setPDC(blockDisplay);

        this.removeDisplayTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            remove();
        }, lifespan);
    }

    public void remove() {
        if (spawnedDisplay != null && !spawnedDisplay.isDead()) {
            this.spawnedDisplay.remove();
            this.removeDisplayTask.cancel();
        }
    }
}
