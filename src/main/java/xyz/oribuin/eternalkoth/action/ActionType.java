package xyz.oribuin.eternalkoth.action;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.action.impl.BroadcastAction;
import xyz.oribuin.eternalkoth.action.impl.ConsoleAction;
import xyz.oribuin.eternalkoth.action.impl.MessageAction;
import xyz.oribuin.eternalkoth.action.impl.PlayerAction;

import java.util.List;

public enum ActionType {
    MESSAGE(new MessageAction()),
    CONSOLE(new ConsoleAction()),
    PLAYER(new PlayerAction()),
    BROADCAST(new BroadcastAction()),
    ;

    private final Action action;

    ActionType(Action action) {
        this.action = action;
    }


    /**
     * Run all the plugin actions through a series of comomands
     *
     * @param player       The player to run the actions for
     * @param commands     The commands to run
     * @param placeholders The placeholders to apply to the commands
     */
    public static void run(Player player, List<String> commands, StringPlaceholders placeholders) {
        for (String command : commands) {
            ActionType type = match(command);

            if (type == null) {
                throw new IllegalArgumentException("Invalid action type: " + command);
            }

            String content = PlaceholderAPIHook.applyPlaceholders(player, command.substring(command.indexOf("]") + 2)); // remove "[action] ", todo: make removing the whitespace optional
            type.get().run(player, placeholders.apply(content));
        }
    }

    /**
     * Run all the plugin actions through a series of comomands
     *
     * @param player   The player to run the actions for
     * @param commands The commands to run
     */
    public static void run(Player player, List<String> commands) {
        run(player, commands, StringPlaceholders.empty());
    }

    /**
     * Find the action type from the content
     *
     * @param content The content to find the action type from
     * @return The action type
     */
    private static ActionType match(String content) {
        // Match the [action] in the content
        String action = content.substring(1, content.indexOf("]"));

        // Loop through all the actions
        for (ActionType type : values()) {
            if (type.name().equalsIgnoreCase(action))
                return type;
        }

        return null;
    }

    public Action get() {
        return this.action;
    }

}
