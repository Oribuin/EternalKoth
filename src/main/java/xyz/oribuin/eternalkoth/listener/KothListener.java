package xyz.oribuin.eternalkoth.listener;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalkoth.manager.KothManager;

public class KothListener implements Listener {

    private final RosePlugin plugin;
    private final KothManager manager;

    public KothListener(RosePlugin plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager(KothManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Zone zone = this.manager.getActiveZone();
        if (zone == null) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        // We don't care about the player moving their camera
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;

        boolean wasInsideZone = zone.getRegion().isInside(from);
        boolean isInsideZone = zone.getRegion().isInside(to);

        // If the player was in a zone and nolonger is, they left the zone
        if (wasInsideZone && !isInsideZone) {
            zone.leave(event.getPlayer());
            return;
        }

        // If the player is inside a zone, they need to be checked for everything
        if (isInsideZone) {

            // Remove invisibility if the setting is enabled
            if (Setting.REMOVE_INVISIBLE.getBoolean() && event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
                event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);

            zone.enter(event.getPlayer());
        }
    }


}
