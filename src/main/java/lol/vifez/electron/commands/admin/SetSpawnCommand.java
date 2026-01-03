package lol.vifez.electron.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.SerializationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("setspawn")
@CommandPermission("electron.admin")
public class SetSpawnCommand extends BaseCommand {

    @Default
    public void onSetSpawn(Player player) {
        Practice plugin = Practice.getInstance();

        Location loc = player.getLocation();
        String serialized = SerializationUtil.serializeLocation(loc);

        plugin.getConfig().set("SETTINGS.SPAWN-LOCATION", serialized);
        plugin.saveConfig();
        plugin.setSpawnLocation(loc);

        player.sendMessage(CC.translate("&b&lElectron &7┃ &fSpawn location updated."));
    }
}