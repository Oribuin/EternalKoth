package xyz.oribuin.eternalkoth.action.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.action.Action;

public class ConsoleAction extends Action {

    private static final CommandSender CONSOLE = Bukkit.getConsoleSender();

    @Override
    public void run(Player player, String input) {
        Bukkit.dispatchCommand(CONSOLE, this.parse(player, input));
    }

}
