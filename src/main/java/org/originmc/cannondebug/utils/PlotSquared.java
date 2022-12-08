package org.originmc.cannondebug.utils;

import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlotSquared {

    private static PlotAPI plotAPI = null;

    static {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");

        if (plugin != null && plugin.isEnabled()) {
            plotAPI = new PlotAPI();
        }
    }

    public static boolean isEnabled() {
        return plotAPI != null;
    }

    public static boolean isPlotTrusted(Player player, Location location) {
        if (location.getWorld() == null) return true;
        com.plotsquared.core.location.Location loc = bukkitToPlotLocation(location);
        if (loc == null) return true;
        PlotArea plotArea = plotAPI.getPlotSquared().getPlotAreaManager().getPlotArea(loc);
        if (plotArea != null) {
            Plot plot = plotArea.getPlot(loc);
            if (plot != null) {
                return player != null && (plot.getOwners().contains(player.getUniqueId()) || plot.getTrusted().contains(player.getUniqueId()));
            }
        }
        return !plotAPI.getPlotSquared().getPlotAreaManager().hasPlotArea(location.getWorld().getName());
    }

    public static com.plotsquared.core.location.Location bukkitToPlotLocation(Location location) {
        if (location.getWorld() != null)
            return com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        else return null;
    }
}
