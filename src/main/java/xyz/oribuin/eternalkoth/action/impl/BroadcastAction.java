package xyz.oribuin.eternalkoth.action.impl;

import dev.rosewood.rosegarden.utils.HexUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.action.Action;

public class BroadcastAction extends Action {

    @Override
    public void run(Player player, String input) {
        String content = this.parse(player, HexUtils.colorify(input));
        Bukkit.getOnlinePlayers().forEach(online -> online.sendMessage(content));
    }

}
