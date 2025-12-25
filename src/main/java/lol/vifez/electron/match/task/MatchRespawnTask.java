package lol.vifez.electron.match.task;

import lol.vifez.electron.match.Match;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.util.CC;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class MatchRespawnTask extends BukkitRunnable {

    private final Match match;
    private final Player player;
    private final org.bukkit.Location deathLocation;
    private int seconds;

    public MatchRespawnTask(Match match, Player player, int seconds) {
        this.match = match;
        this.player = player;
        this.seconds = seconds;
        this.deathLocation = player.getLocation().clone().add(0, 2, 0);
        
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(deathLocation);
    }

    @Override
    public void run() {
        if (match.getMatchState() == MatchState.ENDING || match.getMatchState() == MatchState.ENDED) {
            cancel();
            return;
        }

        if (!player.isOnline()) {
            cancel();
            return;
        }

        if (seconds > 0) {
            player.sendTitle(CC.translate("&c&lYOU DIED!"), CC.translate("&fRespawning in &e" + seconds + " &fseconds..."));
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
            
            // Keep them at the death location
            if (player.getLocation().distanceSquared(deathLocation) > 4) {
                player.teleport(deathLocation);
            }
            
            seconds--;
        } else {
            player.sendTitle(CC.translate("&a&lRESPAWNED!"), CC.translate("&7You are back in the fight!"));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
            player.setGameMode(GameMode.SURVIVAL);
            match.respawn(player);
            cancel();
        }
    }
}
