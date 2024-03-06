package xyz.oribuin.eternalkoth.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentsDefinition;
import dev.rosewood.rosegarden.command.framework.BaseRoseCommand;
import dev.rosewood.rosegarden.command.framework.CommandInfo;
import xyz.oribuin.eternalkoth.command.impl.HelpCommand;
import xyz.oribuin.eternalkoth.command.impl.ReloadCommand;
import xyz.oribuin.eternalkoth.command.impl.StartCommand;

public class KothCommand extends BaseRoseCommand {

    public KothCommand(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    protected CommandInfo createCommandInfo() {
        return CommandInfo.builder("koth")
                .descriptionKey("command-koth-description")
                .permission("eternalkoth.use")
                .build();
    }

    @Override
    protected ArgumentsDefinition createArgumentsDefinition() {
        return ArgumentsDefinition.builder()
                .requiredSub("command", new HelpCommand(this.rosePlugin, this),
                        new ReloadCommand(this.rosePlugin),
                        new StartCommand(this.rosePlugin)
                );
    }
}