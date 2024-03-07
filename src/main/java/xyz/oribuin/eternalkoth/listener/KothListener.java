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

    private final KothManager manager;

    public KothListener(RosePlugin plugin) {
        this.manager = plugin.getManager(KothManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Zone zone = this.manager.getActiveZone();
        if (zone == null) return;

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        // We don't care about the player moving their camera
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;

        if (zone.getRegion().getPos1() == null || zone.getRegion().getPos2() == null) return;
        if (zone.getRegion().getPos1().getWorld() == null) return;

        if (!zone.getRegion().getPos1().getWorld().getName().equalsIgnoreCase(event.getPlayer().getWorld().getName()))
            return;

        boolean wasInsideZone = zone.getRegion().isInside(from);
        boolean isInsideZone = zone.getRegion().isInside(to);
        if (wasInsideZone == isInsideZone) return;

        // If the player was in a zone and no longer is, they left the zone
        if (wasInsideZone) {
            zone.leave(event.getPlayer());
            manager.setActiveZone(zone);
            return;
        }

        // Reverse of the method above, if they were not in the zone and now are, they entered the zone
        // If the player is inside a zone, they need to be checked for everything
        // Remove invisibility if the setting is enabled
        if (Setting.REMOVE_INVISIBLE.getBoolean() && event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        }

        zone.enter(event.getPlayer());
        manager.setActiveZone(zone);
    }


}
