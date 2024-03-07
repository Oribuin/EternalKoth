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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KothManager extends Manager {

    private final Map<String, Zone> cachedZones = new HashMap<>(); // These are not the active zones but data for the configs.
    private File file;
    private CommentedFileConfiguration config;

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

        long delay = KothUtils.parseDuration(Setting.AUTO_START_DELAY.getString()).toSeconds();
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
        this.file = FileUtils.createFile(this.rosePlugin, "zones.yml");
        this.config = CommentedFileConfiguration.loadConfiguration(this.file);
        CommentedConfigurationSection section = this.config.getConfigurationSection("zones");
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
            zone.setTimeToCapture(KothUtils.parseDuration(section.getString(key + ".time-to-capture", "5m")));
            zone.setMaxDuration(KothUtils.parseDuration(section.getString(key + ".max-duration", "30m")));

            this.cachedZones.put(key, zone);
        }
    }

    @Override
    public void disable() {
        Bukkit.getScheduler().cancelTasks(this.rosePlugin);
    }

    /**
     * Save the zone to the zones.yml file.
     *
     * @param zone The zone to save.
     */
    public void save(Zone zone) {
        this.cachedZones.put(zone.getId(), zone);

        String path = "zones." + zone.getId().toLowerCase() + ".";

        Region region = zone.getRegion();
        if (region == null || region.getPos1() == null || region.getPos2() == null) return;

        // Save the unique zone data
        this.config.set(path + "rewards", zone.getRewards());
        this.config.set(path + "time-to-capture", KothUtils.convertMillis(zone.getTimeToCapture().toMillis()));
        this.config.set(path + "max-duration", KothUtils.convertMillis(zone.getMaxDuration().toMillis()));

        // Save the region
        this.config.set(path + "region.world", Objects.requireNonNull(region.getPos1().getWorld()).getName());
        this.config.set(path + "region.pos1.x", region.getPos1().getX());
        this.config.set(path + "region.pos1.y", region.getPos1().getY());
        this.config.set(path + "region.pos1.z", region.getPos1().getZ());

        this.config.set(path + "region.pos2.x", region.getPos2().getX());
        this.config.set(path + "region.pos2.y", region.getPos2().getY());
        this.config.set(path + "region.pos2.z", region.getPos2().getZ());

        // Save the whole config
        this.config.save(this.file);
    }

    public void delete(Zone zone) {
        this.cachedZones.remove(zone.getId());
        this.config.set("zones." + zone.getId().toLowerCase(), null);
        this.config.save(this.file);
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
        Zone clone = this.cachedZones.get(id);
        if (clone == null) return;

        clone.setStartTime(System.currentTimeMillis());

        // Start the KOTH
        this.kothListener = new KothListener(this.rosePlugin);
        this.kothTask = new CaptureTask(this.rosePlugin).runTaskTimerAsynchronously(this.rosePlugin, 0L, 1);
        this.lastKothTime = System.currentTimeMillis();
        this.activeZone = clone;

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

    public void setActiveZone(Zone activeZone) {
        this.activeZone = activeZone;
    }

    public Map<String, Zone> getCachedZones() {
        return cachedZones;
    }

}
