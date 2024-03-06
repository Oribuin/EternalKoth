package xyz.oribuin.eternalkoth.koth;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private Location pos1; // First position of the region
    private Location pos2; // Second position of the region

    public Region(Location pos1, Location pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    /**
     * Check if location is in the region
     *
     * @param location The location to check
     * @return Whether the location is in the region
     */
    public boolean isInside(Location location) {
        // Check if the position is null
        if (pos1 == null || pos2 == null)
            return false;

        // Check if the location is inside the world of the region
        if (location.getWorld() != pos1.getWorld() || location.getWorld() != pos2.getWorld())
            return false;

        // Declare location x, y, z
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Check if the location is inside the region
        return x >= Math.min(pos1.getX(), pos2.getX()) && x <= Math.max(pos1.getX(), pos2.getX()) &&
               y >= Math.min(pos1.getY(), pos2.getY()) && y <= Math.max(pos1.getY(), pos2.getY()) &&
               z >= Math.min(pos1.getZ(), pos2.getZ()) && z <= Math.max(pos1.getZ(), pos2.getZ());
    }

    /**
     * Show the region to a player
     *
     * @param viewer   The player to show the region to
     * @param particle The particle to show
     */
    public void show(Player viewer, Particle particle) {
        if (pos1 == null || pos2 == null)
            return;

        List<Location> cube = this.getCube(pos1, pos2, 1.0);
        cube.forEach(loc -> viewer.spawnParticle(particle, loc, 1, 0, 0, 0, 0, 0));
    }

    /**
     * Get all the particle locations to spawn a hollow cube in between point A & Point B
     *
     * @param corner1 The first corner.
     * @param corner2 The second corner
     * @return The list of particle locations
     * @author Esophose
     * @ <a href="https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleOutline.java#L86">...</a>
     */
    private List<Location> getCube(Location corner1, Location corner2, double outerAdjustment) {
        List<Location> result = new ArrayList<>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX()) + outerAdjustment;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + outerAdjustment;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + outerAdjustment;

        for (double x = minX; x <= maxX; x += 0.5) {
            result.add(new Location(world, x, minY, minZ));
            result.add(new Location(world, x, maxY, minZ));
            result.add(new Location(world, x, minY, maxZ));
            result.add(new Location(world, x, maxY, maxZ));
        }

        for (double y = minY; y <= maxY; y += 0.5) {
            result.add(new Location(world, minX, y, minZ));
            result.add(new Location(world, maxX, y, minZ));
            result.add(new Location(world, minX, y, maxZ));
            result.add(new Location(world, maxX, y, maxZ));
        }

        for (double z = minZ; z <= maxZ; z += 0.5) {
            result.add(new Location(world, minX, minY, z));
            result.add(new Location(world, maxX, minY, z));
            result.add(new Location(world, minX, maxY, z));
            result.add(new Location(world, maxX, maxY, z));
        }

        return result;
    }

    public @Nullable Location getPos1() {
        return pos1;
    }

    public void setPos1(@Nullable Location pos1) {
        this.pos1 = pos1;
    }

    public @Nullable Location getPos2() {
        return pos2;
    }

    public void setPos2(@Nullable Location pos2) {
        this.pos2 = pos2;
    }

}
