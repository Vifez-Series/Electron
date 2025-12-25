package lol.vifez.electron.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.elo.EloUtil;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.match.event.MatchEndEvent;
import lol.vifez.electron.match.event.MatchStartEvent;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class MatchManager {

    private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

    public Match getMatch(UUID uuid) {
        return matches.get(uuid);
    }

    public Map<UUID, Match> getMatches() {
        return matches;
    }

    public void add(Match match) {
        matches.put(match.getPlayerOne().getUuid(), match);
        matches.put(match.getPlayerTwo().getUuid(), match);
    }

    public void remove(Match match) {
        match.getArena().setBusy(false);
        matches.remove(match.getPlayerOne().getUuid());
        matches.remove(match.getPlayerTwo().getUuid());
    }

    public int getTotalPlayersInMatches() {
        return matches.values().stream().mapToInt(m -> m.getMatchState() == MatchState.STARTED ? 2 : 0).sum();
    }

    public int getPlayersInKitMatches(Kit kit) {
        return matches.values().stream()
                .filter(match -> match.getKit().equals(kit))
                .filter(match -> match.getMatchState() == MatchState.STARTED)
                .mapToInt(match -> 2)
                .sum();
    }

    public void start(Match match) {
        match.setMatchState(MatchState.STARTING);
        match.getArena().setBusy(true);
        add(match);

        match.teleportAndSetup(match.getPlayerOne(), true);
        match.teleportAndSetup(match.getPlayerTwo(), false);

        Bukkit.getPluginManager().callEvent(new MatchStartEvent(match.getPlayerOne(), match.getPlayerTwo(), match));
    }

    public void end(Match match) {
        match.setMatchState(MatchState.ENDING);

        Profile winner = match.getWinner();
        Profile loser = winner == null ? null : match.getOpponent(winner);

        if (winner != null && match.isRanked()) updateEloForRankedMatch(winner, loser, match.getKit());

        Profile[] profiles = winner == null ? new Profile[]{match.getPlayerOne(), match.getPlayerTwo()} : new Profile[]{winner, loser};

        for (Profile profile : profiles) {
            Player player = profile.getPlayer();
            if (player == null) continue;

            if (winner == null) {
                player.sendMessage(CC.translate("&cMatch has ended in a draw!"));
                player.sendTitle(CC.translate("&c&lDRAW!"), CC.translate("&7No one won the match."));
            } else if (profile.equals(winner)) {
                player.sendMessage(CC.translate("&aYou have won the match!"));
                player.sendTitle(CC.translate("&a&lVICTORY!"), CC.translate("&7You defeated " + loser.getName() + "!"));
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            } else {
                player.sendMessage(CC.translate("&cYou have lost the match!"));
                player.sendTitle(CC.translate("&c&lDEFEAT!"), CC.translate("&7You were defeated by " + winner.getName() + "!"));
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
            }
        }

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
            for (Profile profile : profiles) {
                Player player = profile.getPlayer();
                if (player != null) {
                    resetPlayerAfterMatch(player);
                }
            }

            if (match.getKit().isBedFight() || match.getArena().getType().contains("build")) {
                Practice.getInstance().getChunkRestorationManager().getIChunkRestoration().reset(match.getArena());
            } else {
                match.getArena().fixArena();
            }

            remove(match);
        }, 60L); // 3 seconds delay

        Bukkit.getPluginManager().callEvent(new MatchEndEvent(match.getPlayerOne(), match.getPlayerTwo(), match));
    }

    private void resetPlayerAfterMatch(Player player) {
        player.getInventory().setContents(Hotbar.getSpawnItems());
        player.getInventory().setArmorContents(null);
        player.teleport(Practice.getInstance().getSpawnLocation());
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    private void updateEloForRankedMatch(Profile winner, Profile loser, Kit kit) {
        int winnerElo = winner.getElo(kit);
        int loserElo = loser.getElo(kit);

        int newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
        int newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

        winner.setElo(kit, newWinnerElo);
        loser.setElo(kit, newLoserElo);

        winner.checkDivision(kit);
        loser.checkDivision(kit);

        CC.sendMessage(winner.getPlayer(), "&aYou won! &7ELO: " + (newWinnerElo - winnerElo >= 0 ? "&a+" : "&c") + (newWinnerElo - winnerElo));
        CC.sendMessage(loser.getPlayer(), "&cYou lost! &7ELO: " + (newLoserElo - loserElo >= 0 ? "&a+" : "&c") + (newLoserElo - loserElo));
    }
}