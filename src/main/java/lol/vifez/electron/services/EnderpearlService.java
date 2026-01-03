package lol.vifez.electron.services;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class EnderpearlService implements Listener {

    private static final long COOLDOWN = 16_000L;
    private static final long UPDATE_INTERVAL = 2L;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public EnderpearlService() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onPearlThrow(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL) return;

        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (now - last < COOLDOWN) {
            double seconds = (COOLDOWN - (now - last)) / 1000.0;
            player.sendMessage(CC.translate("&cYou must wait &e" + String.format("%.1f", seconds) + "s &cto throw another pearl."));
            event.setCancelled(true);
            return;
        }

        cooldowns.put(player.getUniqueId(), now);
        startBar(player);
    }

    private void startBar(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
                long elapsed = System.currentTimeMillis() - last;

                if (elapsed >= COOLDOWN) {
                    player.setLevel(0);
                    player.setExp(0f);
                    cooldowns.remove(player.getUniqueId());
                    player.sendMessage(CC.translate("&aYou can now throw an enderpearl!"));
                    cancel();
                    return;
                }

                double remaining = (COOLDOWN - elapsed) / 1000.0;
                float xp = (float) Math.max(0.0, Math.min(1.0, remaining / (COOLDOWN / 1000.0)));
                player.setLevel((int) Math.ceil(remaining));
                player.setExp(xp);
            }
        }.runTaskTimer(Practice.getInstance(), 0L, UPDATE_INTERVAL);
    }
}