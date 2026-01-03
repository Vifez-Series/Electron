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
        for (Match match : matchManager.getActiveMatches()) {
            if (match.getMatchState() != MatchState.STARTING) continue;
            if (match.isCountdownRunning()) continue;

            match.setCountdownRunning(true);

            final Profile p1 = match.getPlayerOne();
            final Profile p2 = match.getPlayerTwo();

            sendOpponentFound(match, p1, p2);
            sendOpponentFound(match, p2, p1);

            BukkitRunnable countdownTask = new BukkitRunnable() {
                int countdown = match.getCountdownTime();

                @Override
                public void run() {
                    if (match.getMatchState() != MatchState.STARTING) {
                        match.setCountdownRunning(false);
                        match.setCountdownTask(null);
                        cancel();
                        return;
                    }

                    match.setCurrentCountdown(countdown);

                    if (countdown > 0) {
                        tickCountdown(p1, countdown);
                        tickCountdown(p2, countdown);
                        countdown--;
                        return;
                    }

                    match.allowMovement(p1.getPlayer());
                    match.allowMovement(p2.getPlayer());

                    match.setMatchState(MatchState.STARTED);

                    Bukkit.getPluginManager().callEvent(new MatchStartEvent(match.getPlayerOne(), match.getPlayerTwo(), match));

                    match.setCountdownRunning(false);
                    match.setCountdownTask(null);
                    cancel();
                }
            };

            match.setCountdownTask(countdownTask.runTaskTimer(Practice.getInstance(), 0L, 20L));
        }
    }

    private void sendOpponentFound(Match match, Profile self, Profile opponent) {
        self.getPlayer().sendMessage(" ");
        self.getPlayer().sendMessage(CC.colorize("&b&lOPPONENT FOUND"));
        self.getPlayer().sendMessage(CC.colorize("&fKit: &b" + match.getKit().getName()));
        self.getPlayer().sendMessage(CC.colorize("&fOpponent: &c" + opponent.getPlayer().getName()));
        self.getPlayer().sendMessage(" ");
        self.getPlayer().playSound(self.getPlayer().getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
    }

    private void tickCountdown(Profile profile, int countdown) {
        profile.getPlayer().sendMessage(CC.colorize("&7Match Starting In &b" + countdown + "s"));
        profile.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.NOTE_PIANO, 0.5f, 0.5f);
    }
}