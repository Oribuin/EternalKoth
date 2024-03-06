package xyz.oribuin.eternalkoth.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.argument.ArgumentHandlers;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalkoth.command.argument.LocationArgumentHandler;
import xyz.oribuin.eternalkoth.koth.Region;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

public class CreateCommand extends BaseRoseCommand {

    public CreateCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        KothManager manager = this.rosePlugin.getManager(KothManager.class);
        String name = context.get("name").toString().toLowerCase().replace(".", "_");
        Location pos1 = context.get("pos1");
        Location pos2 = context.get("pos2");

        // Make sure the zone does not exist
        if (manager.getCachedZones().containsKey(name)) {
            locale.sendMessage(context.getSender(), "command-create-already-exists");
            return;
        }

        Zone zone = new Zone(name, new Region(pos1, pos2));
        manager.save(zone);
        locale.sendMessage(context.getSender(), "command-create-success", StringPlaceholders.of("zone", name));

        if (context.getSender() instanceof Player player) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> zone.getRegion().show(player, Particle.CRIT), 0L, 5L);
            Bukkit.getScheduler().runTaskLater(this.rosePlugin, task::cancel, 3 * 20L);
        }

    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("create")
                .descriptionKey("command-create-description")
                .permission("eternalkoth.create")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("name", ArgumentHandlers.STRING)
                .required("pos1", new LocationArgumentHandler())
                .required("pos2", new LocationArgumentHandler())
                .build();
    }

}
