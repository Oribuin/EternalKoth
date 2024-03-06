package xyz.oribuin.eternalkoth.team;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.team.impl.MMOTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TeamRegistry {

    private static final Map<String, Team> TEAMS = new HashMap<>();

    static {
        TEAMS.put("mcmmo", new MMOTeam());
    }

    /**
     * Check if all players are on the same team
     *
     * @param players The players to check
     * @return If all players are on the same team
     */
    public static boolean isOnSameTeam(List<Player> players) {
        return TEAMS.values().stream().anyMatch(team -> team.isOnSameTeam(players));
    }

    /**
     * Get a team by its id
     *
     * @param id The id of the team
     * @return The team
     */
    public static Team getTeam(String id) {
        return TEAMS.get(id);
    }

    /**
     * Register a new team
     *
     * @param id   The id of the team
     * @param team The team
     */
    public static void registerTeam(String id, Team team) {
        TEAMS.put(id, team);
    }

}
