package lol.vifez.electron.game.queue;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Getter
public class Queue {

    private final Practice instance;
    private final Kit kit;
    private final boolean ranked;

    private final Map<UUID, Long> playerJoinTimes;

    private int rankedIndex = 1;

    public Queue(Practice instance, Kit kit, boolean ranked) {
        this.instance = instance;
        this.kit = kit;
        this.ranked = ranked;
        this.playerJoinTimes = new ConcurrentHashMap<>();
    }

    public String getFormattedQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) return "00:00";

        long elapsed = System.currentTimeMillis() - joinTime;
        long seconds = elapsed / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void move() {
        List<UUID> queuedPlayers = new ArrayList<>(playerJoinTimes.keySet());
        if (queuedPlayers.size() < 2) return;

        queuedPlayers.sort(Comparator.comparingLong(playerJoinTimes::get));

        Player first = Bukkit.getPlayer(queuedPlayers.get(0));
        Player second = Bukkit.getPlayer(queuedPlayers.get(1));

        if (first == null || second == null) return;

        Profile profileOne = instance.getProfileManager().getProfile(first.getUniqueId());
        Profile profileTwo = instance.getProfileManager().getProfile(second.getUniqueId());

        Arena arena = instance.getArenaManager()
                .getAllAvailableArenas(kit)
                .stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (arena == null) {
            CC.sendMessage(first, "&cNo available arenas for " + kit.getName());
            CC.sendMessage(second, "&cNo available arenas for " + kit.getName());
            first.getInventory().setContents(Hotbar.getSpawnItems());
            second.getInventory().setContents(Hotbar.getSpawnItems());
            return;
        }

        if (ranked) {
            int difference = Math.abs(profileOne.getElo(kit) - profileTwo.getElo(kit));
            if (difference > 250) return;
        }

        playerJoinTimes.remove(first.getUniqueId());
        playerJoinTimes.remove(second.getUniqueId());

        instance.getQueueManager().getPlayersQueue().remove(first.getUniqueId());
        instance.getQueueManager().getPlayersQueue().remove(second.getUniqueId());

        Bukkit.getScheduler().runTask(instance, () -> {
            Match match = new Match(instance, profileOne, profileTwo, kit, arena, ranked);
            instance.getMatchManager().start(match);
        });
    }

    public void add(Player player) {
        instance.getQueueManager().getPlayersQueue().put(player.getUniqueId(), this);
        instance.getProfileManager().getProfile(player.getUniqueId())
                .setCurrentQueue(ranked ? "ranked_" + this.getKit().getName() : this.getKit().getName());

        playerJoinTimes.put(player.getUniqueId(), System.currentTimeMillis());

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Hotbar.getQueueItems());
    }

    public void remove(Player player) {
        instance.getQueueManager().getPlayersQueue().remove(player.getUniqueId());
        instance.getProfileManager().getProfile(player.getUniqueId()).setCurrentQueue("");

        playerJoinTimes.remove(player.getUniqueId());

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Hotbar.getSpawnItems());
    }

    public int getQueueSize() {
        return playerJoinTimes.size();
    }

    public String getQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) return "0s";

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - joinTime;

        long seconds = elapsedTime / 1000;

        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m";
        } else {
            return (seconds / 3600) + "h";
        }
    }
}