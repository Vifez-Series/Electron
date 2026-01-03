package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class ToggleScoreboardButton {

    public static Button createToggleScoreboardButton(Profile profile, Practice instance) {
        boolean scoreboardEnabled = profile.isScoreboardEnabled();

        ItemStack scoreboardItem = new ItemBuilder(Material.PAINTING)
                .name("&eScoreboard")
                .lore(
                        "&fShow or hide the in-game scoreboard.",
                        "",
                        "&fStatus: " + (scoreboardEnabled ? "&a&lENABLED" : "&c&lDISABLED"),
                        "",
                        "&eClick to toggle"
                )
                .build();

        return new EasyButton(scoreboardItem, true, false, () -> {
            profile.setScoreboardEnabled(!scoreboardEnabled);
            Practice.getInstance().getProfileManager().save(profile);

            new SettingsMenu(instance, profile).openMenu(profile.getPlayer());
        });
    }
}