package lol.vifez.electron.util;

import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import lol.vifez.electron.Practice;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */
public class AutoRespawn implements Listener {

    public AutoRespawn() {
        Practice.getInstance().getServer()
                .getPluginManager()
                .registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final lol.vifez.electron.profile.Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        
        if (profile != null && profile.inMatch()) return;

        final Location deathLocation = player.getLocation().clone();

        Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> {
            if (player.isDead()) {
                ((CraftPlayer) player).getHandle().playerConnection.a(
                        new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN)
                );
            }

            Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> {
                if (player.isOnline() && !player.isDead()) {
                    player.teleport(deathLocation);
                }
            }, 2L);
        }, 1L);
    }
}