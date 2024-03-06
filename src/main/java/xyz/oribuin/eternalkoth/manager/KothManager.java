package xyz.oribuin.eternalkoth.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalkoth.koth.Region;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.listener.KothListener;
import xyz.oribuin.eternalkoth.manager.ConfigurationManager.Setting;
import xyz.oribuin.eternalkoth.task.CaptureTask;
import xyz.oribuin.eternalkoth.util.FileUtils;
import xyz.oribuin.eternalkoth.util.KothUtils;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KothManager extends Manager {

    private final Map<String, Zone> cachedZones = new HashMap<>(); // These are not the active zones but data for the configs.

    private Zone activeZone;
    private BukkitTask kothTask;
    private Listener kothListener;
    private long lastKothTime = System.currentTimeMillis();

    public KothManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.loadZones();

        if (!Setting.AUTO_START.getBoolean()) return;

        long delay = Duration.ofMillis(KothUtils.parseTime(Setting.AUTO_START_DELAY.getString())).toSeconds();
         Bukkit.getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> {
            // Cannot start a game if one is already active
            if (this.activeZone != null) return;
            if (System.currentTimeMillis() - this.lastKothTime < delay * 20) return;

            // Start a new KOTH
            Zone zone = new ArrayList<>(this.cachedZones.values()).get(((int) (Math.random() * this.cachedZones.size())));
            this.start(zone.getId());

            this.lastKothTime = System.currentTimeMillis();
        }, 3 * 20, 3 * 20);
    }

    /**
     * Load the zones from the zones.yml file.
     */
    public void loadZones() {
        this.cachedZones.clear();

        // Load the zones from the config
        File file = FileUtils.createFile(this.rosePlugin, "zones.yml");
        CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
        CommentedConfigurationSection section = config.getConfigurationSection("zones");
        if (section == null) return;

        for (String key : section.getKeys(false)) {

            // Load the region from the config
            World world = Bukkit.getWorld(section.getString(key + ".region.world", "unknown-world"));
            if (world == null) {
                Bukkit.getLogger().severe("Failed to load zone, World not found.");
                continue;
            }

            Location pos1 = new Location(world,
                    section.getDouble(key + ".region.pos1.x"),
                    section.getDouble(key + ".region.pos1.y"),
                    section.getDouble(key + ".region.pos1.z")
            );

            Location pos2 = new Location(world,
                    section.getDouble(key + ".region.pos2.x"),
                    section.getDouble(key + ".region.pos2.y"),
                    section.getDouble(key + ".region.pos2.z")
            );

            Zone zone = new Zone(key, new Region(pos1, pos2));
            zone.setRewards(section.getStringList(key + ".rewards"));
            zone.setTimeToCapture(KothUtils.parseTime(section.getString(key + ".time-to-capture", "5m")));

            this.cachedZones.put(key, zone);
        }
    }

    @Override
    public void disable() {
        Bukkit.getScheduler().cancelTasks(this.rosePlugin);
    }

    /**
     * Start a koth match with the specified zone id.
     *
     * @param id The id of the zone to start the koth match.
     */
    public void start(String id) {
        // cancel any existing koth matches
        if (this.activeZone != null) this.cancel();

        // Create a duplicate instance of the zone from the cache
        this.activeZone = this.cachedZones.get(id);
        if (this.activeZone == null) return;

        // Start the KOTH
        this.activeZone.setStartTime(System.currentTimeMillis());
        this.kothListener = new KothListener(this.rosePlugin);
        this.kothTask = new CaptureTask(this.rosePlugin).runTaskTimerAsynchronously(this.rosePlugin, 0L, 1);
        this.lastKothTime = System.currentTimeMillis();

        Bukkit.getPluginManager().registerEvents(this.kothListener, this.rosePlugin);
    }

    /**
     * Cancel the active koth match. This will unregister all listeners and cancel the capture task.
     *
     * @see KothListener
     * @see CaptureTask
     */
    public void cancel() {
        if (this.activeZone == null) return;
        if (this.kothListener != null) HandlerList.unregisterAll(this.kothListener);
        if (this.kothTask != null) this.kothTask.cancel();

        this.activeZone = null;
        this.kothTask = null;
        this.kothListener = null;
    }

    public Zone getActiveZone() {
        return this.activeZone;
    }

    public Map<String, Zone> getCachedZones() {
        return cachedZones;
    }

}
