package lol.vifez.electron.match.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.match.MatchManager;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.match.event.MatchStartEvent;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class MatchTask extends BukkitRunnable {

    private final MatchManager matchManager;

    public MatchTask(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Override
    public void run() {
        matchManager.getMatches().values().forEach(match -> {
            if (match.getMatchState() != MatchState.STARTING || match.isCountdownRunning()) return;
            match.setCountdownRunning(true);

            Profile[] players = {match.getPlayerOne(), match.getPlayerTwo()};
            Arrays.stream(players).forEach(profile -> {
                Profile opponent = match.getOpponent(profile);
                profile.getPlayer().sendMessage(" ");
                profile.getPlayer().sendMessage(CC.colorize("&b&lOPPONENT FOUND"));
                profile.getPlayer().sendMessage(CC.colorize("&fKit: &b" + match.getKit().getName()));
                profile.getPlayer().sendMessage(CC.colorize("&fOpponent: &c" + opponent.getPlayer().getName()));
                profile.getPlayer().sendMessage(" ");
                profile.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            });

            new BukkitRunnable() {
                int countdown = match.getCountdownTime();

                @Override
                public void run() {
                    match.setCurrentCountdown(countdown);
                    if (countdown > 0) {
                        Arrays.stream(players).forEach(p -> {
                            p.getPlayer().sendMessage(CC.colorize("&7Match Starting In &b" + countdown + "s"));
                            p.getPlayer().sendTitle(CC.translate("&b&l" + countdown), CC.translate("&7Starting match..."));
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.NOTE_PIANO, 0.5f, 0.5f);
                        });
                        countdown--;
                    } else {
                        Arrays.stream(players).forEach(p -> {
                            match.allowMovement(p.getPlayer());
                            p.getPlayer().sendTitle(CC.translate("&b&lGO!"), CC.translate("&7The match has started!"));
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
                        });
                        match.setMatchState(MatchState.STARTED);
                        Bukkit.getPluginManager().callEvent(new MatchStartEvent(match.getPlayerOne(), match.getPlayerTwo(), match));
                        match.setCountdownRunning(false);
                        cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), 0L, 20L);
        });
    }
}