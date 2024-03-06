package xyz.oribuin.eternalkoth.koth;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.EternalKothPlugin;
import xyz.oribuin.eternalkoth.action.ActionType;
import xyz.oribuin.eternalkoth.team.TeamRegistry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Zone {

    // these are config defined
    private final String id; 
    private Region region; 
    private List<String> rewards;
    private long timeToCapture; // The time it takes to fully capture the zone

    // these all need to be mutable :(
    private long startTime;
    private long startOfCapture; // The time since the zone started being captured
    private UUID captain;
    private boolean paused;

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
        this.timeToCapture = Duration.ofMinutes(1).toMillis();
    }

    /**
     * Capture the zone and give the rewards to the player
     * This function will only fire if the zone has been captured
     */
    public void capture() {
        if (!this.isCaptured()) return;
        Player captain = Bukkit.getPlayer(this.captain);

        if (captain == null) return;

        this.paused = true;

        // Give rewards synchronously to the players, spigot doesnt like asynchronous command execution
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
        if (this.startTime == 0L) return false;
        if (this.captain == null) return false;
        if (this.paused) return false;

        return System.currentTimeMillis() - this.startOfCapture >= this.timeToCapture;
    }

    /**
     * Get the capture progress of the zone as a percentage
     *
     * @return The Capture Percentage
     */
    public double getProgress() {
        if (this.startTime == 0L) return 0.0;
        if (this.captain == null) return 0.0;
        if (this.paused) return 0.0;

        long timePassed = System.currentTimeMillis() - this.startOfCapture;
        return (double) timePassed / (double) this.timeToCapture;
    }

    /**
     * Get the remaining time to capture the zone
     *
     * @return The remaining time to capture the zone
     */
    public long getRemainingTime() {
        if (this.startTime == 0L) return 0L;
        if (this.captain == null) return 0L;
        if (this.paused) return 0L;

        long timePassed = System.currentTimeMillis() - this.startOfCapture;
        return this.timeToCapture - timePassed;
    }

    /**
     * Mark a player as part of the capturing team and if theres only one player, make them the captain
     * This function will fire once the player has already entered the zone, inside size would be before size + 1
     *
     * @param player The player who has en
     */
    public void enter(Player player) {
        List<Player> inside = this.getPlayersInside();

        // All players are on the same team so it doesnt matter
        if (TeamRegistry.isOnSameTeam(inside) && this.captain != null) {
            System.out.println("All players are on the same team with a captain defined, we are not doing anything");
            return;
        }

        // This is the first player inside the region, they are now the captain
        if (inside.size() <= 1) {
            this.captain = player.getUniqueId();
            this.paused = false;
            this.startOfCapture = System.currentTimeMillis();
            System.out.println(player.getName() + "is now the captain of the zone");
            return;
        }

        // There are multiple players inside the region and they're not on the same team
        // so we are pausing the capture and resetting the timer
        // wait for these players to be holding the zone
        this.paused = true;
        this.startOfCapture = 0;
        this.captain = null;
        System.out.println("The zone has been paused");
    }

    /**
     * Remove a player from the zone and set the appropriate states if needed
     * This function is fired /after/ the player has left the zone, inside size would be after the player has left
     *
     * @param player The player who has left the zone
     */
    public void leave(Player player) {
        List<Player> inside = this.getPlayersInside();

        // The captain has left the zone, we're going to reset the zone now
        if (this.captain != null && this.captain.equals(player.getUniqueId())) {
            this.captain = null;
            this.startOfCapture = 0;

            System.out.println("The captain has left the zone, we're going to reset the captain & timer");
        }

        // Player has left the zone, we're going to reset the zone now
        if (inside.isEmpty()) {
            this.captain = null;
            this.paused = false;
            this.startOfCapture = 0;

            System.out.println("No one is inside the zone, we're going to reset everything");
            return;
        }

        // The player who left was the last player inside the zone
        if (this.captain == null) {
            this.captain = inside.get(0).getUniqueId();
            this.paused = false;
            this.startOfCapture = System.currentTimeMillis();

            System.out.println("The last player inside the zone has left, we're going to reset the captain & timer");
        }
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

        return world.getNearbyPlayers(center, radius).stream().toList();
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

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeToCapture() {
        return timeToCapture;
    }

    public void setTimeToCapture(long timeToCapture) {
        this.timeToCapture = timeToCapture;
    }

    public long getLastCaptureTime() {
        return startOfCapture;
    }

    public void setLastCaptureTime(long startOfCapture) {
        this.startOfCapture = startOfCapture;
    }

    public UUID getCaptain() {
        return captain;
    }

    public void setCaptain(UUID captain) {
        this.captain = captain;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
