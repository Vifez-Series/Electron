package lol.vifez.electron.listeners;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(killed.getUniqueId());
        if (profile != null && profile.inMatch()) return;
        Player killer = killed.getKiller();
        event.setDeathMessage(null);

        if (killer != null) {
            String message = CC.translate("&a" + killed.getName() + " &fwas killed by &c" + killer.getName());
            Bukkit.broadcastMessage(message);
        }
    }
}