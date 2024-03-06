package xyz.oribuin.eternalkoth.team.impl;

import org.bukkit.entity.Player;
import xyz.oribuin.eternalkoth.team.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BasicTeam implements Team {

    private final Set<UUID> players = new HashSet<>();

    /**
     * Check if all players are on the same team
     *
     * @param players The players to check
     * @return If all players are on the same team
     */
    @Override
    public boolean isOnSameTeam(List<Player> players) {
        return this.players.containsAll(players.stream().map(Player::getUniqueId).toList());
    }

    /**
     * Add a player to the team
     *
     * @param player The player to add
     */
    @Override
    public void addPlayer(Player player) {
        this.players.add(player.getUniqueId());
    }

    /**
     * Remove a player from the team
     *
     * @param player The player to remove
     */
    @Override
    public void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

}
