package xyz.oribuin.eternalkoth.task;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.KothManager;

public class CaptureTask extends BukkitRunnable {

    private final RosePlugin plugin;
    private KothManager manager;

    public CaptureTask(RosePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // TODO: Get the active koth zone (Potential Support for multiple zones)
        Zone zone = this.manager.getActiveZone();
        if (zone == null) return;

        if (zone.isCaptured()) {
            zone.capture();
        }
    }

}
