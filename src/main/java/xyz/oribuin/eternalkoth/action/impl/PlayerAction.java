package xyz.oribuin.eternalkoth.action.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.action.Action;

public class PlayerAction extends Action {

    @Override
    public void run(Player player, String input) {
        Bukkit.dispatchCommand(player, this.parse(player, input));
    }

}
