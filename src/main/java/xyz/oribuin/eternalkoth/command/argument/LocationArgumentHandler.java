package xyz.oribuin.eternalkoth.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class LocationArgumentHandler extends ArgumentHandler<Location> {

    public LocationArgumentHandler() {
        super(Location.class);
    }

    @Override
    public Location handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        try {
            return fromString(inputIterator.next());
        } catch (NumberFormatException ex) {
            throw new HandledArgumentException("argument-handler-location");
        }
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        if (!(context.getSender() instanceof Player player))
            return List.of("0,0,0,world");

        try {
            Block targetBlock = player.getTargetBlockExact(5);
            if (targetBlock == null || targetBlock.getType().isAir() || targetBlock.isLiquid()) {
                return List.of(asString(player.getLocation()));
            }

            return List.of(asString(targetBlock.getLocation()));
        } catch (NumberFormatException ex) {
            throw new HandledArgumentException("argument-handler-location");
        }
    }

    /**
     * Convert a string to a location
     *
     * @param loc The location to convert
     * @return The location as a string
     */
    private String asString(Location loc) {
        if (loc == null || loc.getWorld() == null) return "0,0,0,world";

        return String.format("%s,%s,%s,%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }

    /**
     * Convert a location to a string
     *
     * @param loc The location to convert
     * @return The location as a string
     */
    private Location fromString(String loc) {
        String[] split = loc.split(",");

        World world = null;
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);

        if (split.length == 4) {
            world = Bukkit.getWorld(split[3]);
        }

        return new Location(world, x, y, z);
    }

}
