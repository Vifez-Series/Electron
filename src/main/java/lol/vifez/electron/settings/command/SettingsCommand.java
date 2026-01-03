package lol.vifez.electron.settings.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.OptionsMenu;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("settings|options")
public class SettingsCommand extends BaseCommand {

    private final Practice instance = Practice.getInstance();

    @Default
    public void openSettings(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return;
        }

        new OptionsMenu().openMenu(player);
    }
}