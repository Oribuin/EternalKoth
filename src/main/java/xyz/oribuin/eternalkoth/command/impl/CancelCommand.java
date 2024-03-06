package xyz.oribuin.eternalkoth.command.impl;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalkoth.koth.Zone;
import xyz.oribuin.eternalkoth.manager.KothManager;
import xyz.oribuin.eternalkoth.manager.LocaleManager;

public class CancelCommand extends BaseRoseCommand {

    public CancelCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        KothManager manager = this.rosePlugin.getManager(KothManager.class);

        // Make sure there is no active zone
        Zone activeZone = manager.getActiveZone();
        if (activeZone == null) {
            locale.sendMessage(context.getSender(), "command-cancel-not-active");
            return;
        }

        manager.cancel();
        locale.sendMessage(context.getSender(), "command-cancel-success", StringPlaceholders.of("zone", activeZone.getId()));
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("cancel")
                .descriptionKey("command-cancel-description")
                .permission("eternalkoth.cancel")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.empty();
    }

}
