package xyz.oribuin.eternalkoth.command.argument;

import dev.rosewood.rosegarden.command.framework.Argument;
import dev.rosewood.rosegarden.command.framework.ArgumentHandler;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.InputIterator;
import xyz.oribuin.eternalkoth.EternalKothPlugin;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.KothManager;

import java.util.List;

public class ZoneArgumentHandler extends ArgumentHandler<Zone> {

    public ZoneArgumentHandler() {
        super(Zone.class);
    }

    @Override
    public Zone handle(CommandContext context, Argument argument, InputIterator inputIterator) throws HandledArgumentException {
        String input = inputIterator.next();
        Zone zone = EternalKothPlugin.get().getManager(KothManager.class)
                .getCachedZones()
                .get(input);

        if (zone == null) throw new HandledArgumentException("argument-handler-zone");

        return zone;
    }

    @Override
    public List<String> suggest(CommandContext context, Argument argument, String[] args) {
        List<String> zones = EternalKothPlugin.get().getManager(KothManager.class)
                .getCachedZones()
                .keySet()
                .stream()
                .toList();

        return zones.isEmpty() ? List.of("<no zones>") : zones;
    }

}
