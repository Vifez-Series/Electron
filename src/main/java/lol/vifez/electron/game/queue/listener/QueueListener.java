package lol.vifez.electron.game.queue.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class QueueListener implements Listener {

    private final Practice plugin;
    public QueueListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Queue queue = plugin.getQueueManager().getQueue(player.getUniqueId());

        if (queue != null) {
            queue.remove(player);
        }
    }
}