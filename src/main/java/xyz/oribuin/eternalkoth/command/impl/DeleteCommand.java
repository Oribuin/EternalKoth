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
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

public class DeleteCommand extends BaseRoseCommand {

    public DeleteCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        KothManager manager = this.rosePlugin.getManager(KothManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Zone zone = context.get("zone");

        manager.delete(zone);
        locale.sendMessage(context.getSender(), "command-delete-success", StringPlaceholders.of("zone", zone.getId()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("delete")
                .descriptionKey("command-delete-description")
                .permission("eternalkoth.delete")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .required("zone", new ZoneArgumentHandler())
                .build();
    }

}
