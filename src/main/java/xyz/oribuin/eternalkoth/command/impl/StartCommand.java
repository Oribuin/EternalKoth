package xyz.oribuin.eternalkoth.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import xyz.oribuin.eternalkoth.command.argument.ZoneArgumentHandler;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

public class StartCommand extends BaseRoseCommand {

    public StartCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        KothManager manager = this.rosePlugin.getManager(KothManager.class);

        Zone zone = context.get("zone");
        manager.start(zone.getId());
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
