package xyz.oribuin.eternalkoth.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xyz.oribuin.eternalkoth.command.argument.ZoneArgumentHandler;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

public class ViewCommand extends BaseRoseCommand {

    public ViewCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        Player player = (Player) context.getSender();

        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Zone zone = context.get("zone");

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this.rosePlugin, () -> zone.getRegion().show(player, Particle.CRIT), 0L, 5L);
        Bukkit.getScheduler().runTaskLater(this.rosePlugin, task::cancel, 3 * 20L);

        locale.sendMessage(context.getSender(), "command-view-success", StringPlaceholders.of("zone", zone.getId()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("start")
                .descriptionKey("command-start-description")
                .permission("eternalkoth.start")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("zone", new ZoneArgumentHandler())
                .build();
    }

}
