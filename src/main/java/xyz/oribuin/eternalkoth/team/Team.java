package xyz.oribuin.eternalkoth.team;

import org.bukkit.entity.Player;

import java.util.List;

public interface Team {

    /**
     * Check if all players are on the same team
     *
     * @param players The players to check
     * @return If all players are on the same team
     */
    boolean isOnSameTeam(List<Player> players);

}
