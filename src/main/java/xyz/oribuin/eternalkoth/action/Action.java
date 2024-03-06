package xyz.oribuin.eternalkoth.action;

import dev.rosewood.rosegarden.hook.PlaceholderAPIHook;
import org.bukkit.entity.Player;

public abstract class Action {

    /**
     * Run a specific functionality on the server
     *
     * @param player The player it is ran for
     * @param input  The input of the action
     */
    public abstract void run(Player player, String input);

    /**
     * Parse the input through placeholder api.
     *
     * @param player The player to parse the input for.
     * @return The parsed input.
     */
    public final String parse(Player player, String result) {
        return PlaceholderAPIHook.applyPlaceholders(player, result);
    }

}
