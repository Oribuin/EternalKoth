package xyz.oribuin.eternalkoth.action.impl;

import dev.rosewood.rosegarden.utils.HexUtils;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.action.Action;

public class MessageAction extends Action {

    @Override
    public void run(Player player, String input) {
        player.sendMessage(this.parse(player, HexUtils.colorify(input)));
    }

}
