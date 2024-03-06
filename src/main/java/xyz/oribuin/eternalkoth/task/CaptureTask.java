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
        this.manager = this.plugin.getManager(KothManager.class);
    }

    @Override
    public void run() {
        // TODO: Get the active koth zone (Potential Support for multiple zones)
        Zone zone = this.manager.getActiveZone();
        if (zone == null) return;

        // Cancel the zone if the time to capture is up
        if (zone.getRemainingTime() <= 0) {
            manager.cancel();
            return;
        }

        // Capture the zone if the total progress is 100%
        if (zone.isCaptured()) {
            zone.capture();

            this.manager.setActiveZone(null);
        }

    }

}
