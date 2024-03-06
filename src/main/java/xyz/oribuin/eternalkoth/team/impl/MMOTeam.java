package xyz.oribuin.eternalkoth.team.impl;

import com.gmail.nossr50.api.PartyAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.team.Team;

import java.util.List;

/**
 * Handler that checks for if all players are on the same mcmmo team
 */
public class MMOTeam implements Team {

    /**
     * Check if all players are on the same team
     *
     * @param players The players to check
     * @return If all players are on the same team
     */
    @Override
    public boolean isOnSameTeam(List<Player> players) {
        if (!Bukkit.getPluginManager().isPluginEnabled("mcMMO"))
            return false;

        if (players.stream().noneMatch(PartyAPI::inParty))
            return false;

        // Make sure all players are in the same party
        for (Player a : players) {
            for (Player b : players) {
                if (a.getUniqueId() == b.getUniqueId())
                    continue;

                if (!PartyAPI.inSameParty(a, b))
                    return false;
            }
        }

        return true;
    }

}
