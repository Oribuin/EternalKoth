package xyz.oribuin.eternalkoth.koth;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.EternalKothPlugin;
import xyz.oribuin.eternalkoth.action.ActionType;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.team.TeamRegistry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Zone {

    private final String id;
    private Region region;
    private List<String> rewards;
    private Duration timeToCapture; // The time it takes to fully capture the zone
    private Duration maxDuration; // The maximum time the zone will be active before its shutdown

    // Mutable Data, Not saved to the config
    private long startTime;
    private long startOfCapture; // The time since the zone started being captured
    private long pauseTime; // The time the zone was paused
    private UUID captain;
    private String captainName;

    /**
     * The individual zone for the koth region
     *
     * @param id     The id of the zone
     * @param region The region of the zone
     */
    public Zone(String id, Region region) {
        this.id = id;
        this.region = region;
        this.rewards = new ArrayList<>();
        this.startTime = 0L;
        this.timeToCapture = Duration.ofMinutes(1);
        this.maxDuration = Duration.ofMinutes(5);
    }

    /**
     * Capture the zone and give the rewards to the player
     * This function will only fire if the zone has been captured
     */
    public void capture() {
        if (!this.isCaptured()) return;

        Player captain = Bukkit.getPlayer(this.captain);
        if (captain == null) return;

        // Cancel the current koth task
        EternalKothPlugin.get()
                .getManager(KothManager.class)
                .cancel();

        // Give rewards synchronously to the players, spigot doesn't like asynchronous command execution
        Bukkit.getScheduler().runTask(EternalKothPlugin.get(), () -> ActionType.run(
                captain,
                this.rewards,
                StringPlaceholders.of("zone", this.id)
        ));
    }


    /**
     * Check if the zone has been started
     *
     * @return True if the zone has been started
     */
    public boolean isCaptured() {
        if (this.startTime <= 0L || this.startOfCapture <= 0) return false;
        if (this.captain == null) return false;
        if (this.isPaused()) return false;

        return this.startOfCapture + this.timeToCapture.toMillis() < System.currentTimeMillis();
    }

    /**
     * Get the capture progress of the zone as a percentage
     *
     * @return The Capture Percentage
     */
    public double getProgressPercent() {
        if (this.startTime == 0L) return 0.0;
        if (this.captain == null) return 0.0;

        if (this.isPaused()) {
            return (this.pauseTime - this.startOfCapture) / (double) this.timeToCapture.toMillis();
        }

        long timePassed = System.currentTimeMillis() - this.startOfCapture;
        return (double) timePassed / (double) this.timeToCapture.toMillis();
    }

    /**
     * Get the remaining time to capture the zone
     *
     * @return The remaining time to capture the zone
     */
    public long getCaptureElapsed() {
        if (this.startTime == 0L) return 0L;
        if (this.startOfCapture == 0L) return 0L;

        if (this.isPaused()) {
            return Math.max(0, this.pauseTime - this.startOfCapture);
        }

        // time since the zone started capturing
        return System.currentTimeMillis() - this.startOfCapture;
    }

    /**
     * Get the remaining time the zone will be active
     *
     * @return The remaining time the zone will be active
     */
    public long getRemainingTime() {
        return this.maxDuration.toMillis() - (System.currentTimeMillis() - this.startTime);
    }

    /**
     * Mark a player as part of the capturing team and if there's only one player, make them the captain
     * This function will fire once the player has already entered the zone, inside size would be before size + 1
     *
     * @param player The player who has en
     */
    public void enter(Player player) {
        List<Player> inside = this.getPlayersInside();

        // All players are on the same team so it doesnt matter
        if (TeamRegistry.isOnSameTeam(inside) && this.captain != null) {
            System.out.println(player.getName() + " is on the same team as the captain");
            return;
        }

        // This is the first player inside the region, they are now the captain
        if (inside.size() <= 1 && this.captain == null) {
            System.out.println(player.getName() + " is now the captain of the zone");
            this.captain = player.getUniqueId();
            this.captainName = player.getName();
            this.startOfCapture = System.currentTimeMillis();
            this.pauseTime = 0L;
            return;
        }

        // There are multiple players inside the region, and they're not on the same team
        // so we are pausing the capture and resetting the timer
        // wait for these players to be holding the zone
        if (inside.size() > 1) {
            System.out.println("Pausing Capture");
            this.pauseTime = System.currentTimeMillis();
        }
    }

    /**
     * Remove a player from the zone and set the appropriate states if needed
     * This function is fired /after/ the player has left the zone, inside size would be after the player has left
     *
     * @param player The player who has left the zone
     */
    public void leave(Player player) {
        List<Player> inside = this.getPlayersInside();

        // The captain has left the zone or the zone is empty
        if ((this.captain != null && this.captain.equals(player.getUniqueId())) || inside.isEmpty()) {
            System.out.println(this.captainName + ", The captain has left the zone");
            this.captain = null;
            this.captainName = null;
            this.startOfCapture = 0;
            this.pauseTime = 0L;
            return;
        }

        // The captain has left the zone but theres someone else
        if (this.captain == null && inside.size() > 1) {
            Player newCaptain = inside.get(0);
            System.out.println("Changing the captain of the zone to " + inside.get(0).getName());

            this.captain = newCaptain.getUniqueId();
            this.captainName = newCaptain.getName();
            this.pauseTime = 0L;
            this.startOfCapture = System.currentTimeMillis();
        }

        // Unpause the zone
        this.startOfCapture += System.currentTimeMillis() - this.pauseTime;
        this.pauseTime = 0L;
        System.out.println("unpausing the zone");
    }

    /**
     * Get all the players inside the zone
     *
     * @return A list of players inside the zone
     */
    public List<Player> getPlayersInside() {
        if (this.region == null || this.region.getPos1() == null || this.region.getPos2() == null) {
            return new ArrayList<>();
        }

        World world = this.region.getPos1().getWorld();
        if (world == null) {
            return new ArrayList<>();
        }

        int centerX = (this.region.getPos1().getBlockX() + this.region.getPos2().getBlockX()) / 2;
        int centerZ = (this.region.getPos1().getBlockZ() + this.region.getPos2().getBlockZ()) / 2;
        int centerY = (this.region.getPos1().getBlockY() + this.region.getPos2().getBlockY()) / 2;

        Location center = new Location(world, centerX, centerY, centerZ);
        int radius = (int) Math.ceil(this.region.getPos1().distance(this.region.getPos2()) / 2);

        return world.getNearbyEntities(center, radius, radius, radius, entity -> entity.getType() == EntityType.PLAYER)
                .stream()
                .map(entity -> (Player) entity)
                .toList();
    }

    /**
     * Create an empty instance of the zone
     *
     * @param zone The zone to copy
     * @return A new instance of the zone
     */
    public static Zone from(Zone zone) {
        if (zone == null) return null;

        Zone newZone = new Zone(zone.id, zone.region);
        newZone.setRewards(zone.rewards);
        newZone.setTimeToCapture(zone.timeToCapture);
        newZone.setMaxDuration(zone.maxDuration);
        return newZone;
    }

    @Override
    public String toString() {
        return "Zone{" +
               "id='" + id + '\'' +
               ", startTime=" + startTime +
               ", startOfCapture=" + startOfCapture +
               ", pauseTime=" + pauseTime +
               ", captain=" + captain +
               ", captainName='" + captainName + '\'' +
               '}';
    }

    public String getId() {
        return id;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

    public Duration getTimeToCapture() {
        return timeToCapture;
    }

    public void setTimeToCapture(Duration timeToCapture) {
        this.timeToCapture = timeToCapture;
    }

    public Duration getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(Duration maxDuration) {
        this.maxDuration = maxDuration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartOfCapture() {
        return startOfCapture;
    }

    public void setStartOfCapture(long startOfCapture) {
        this.startOfCapture = startOfCapture;
    }

    public boolean isPaused() {
        return this.pauseTime > 0;
    }

    public long getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }

    public UUID getCaptain() {
        return captain;
    }

    public void setCaptain(UUID captain) {
        this.captain = captain;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }
}
