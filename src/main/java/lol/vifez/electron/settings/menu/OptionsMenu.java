package lol.vifez.electron.settings.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.leaderboard.menu.LeaderboardMenu;
import lol.vifez.electron.leaderboard.menu.YourStatsMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.DisplayButton;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class OptionsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&7Options";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Practice instance = Practice.getInstance();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) return buttons;

        buttons.put(10, new EasyButton(
                new ItemBuilder(Material.EMERALD)
                        .name("&b&lLeaderboards")
                        .lore(Arrays.asList(
                                "&fView the global leaderboards,",
                                " ",
                                "&bClick to open"
                        ))
                        .build(),
                true, true, () -> new LeaderboardMenu(instance).openMenu(player)
        ));

        buttons.put(12, new EasyButton(
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("&b&lBank")
                        .lore(Arrays.asList(
                                "&fView and manage your bank.",
                                "&fbalance, shop, and more.",
                                " ",
                                "&cCurrently unavailable."
                        ))
                        .build(),
                true, true, () -> {
        }
        ));

        buttons.put(14, new EasyButton(
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name("&b&lConfigurations")
                        .lore(Arrays.asList(
                                "&fAdjust your personal settings,",
                                "&ftoggle scoreboard, messages, and more.",
                                " ",
                                "&bClick to open"
                        ))
                        .build(),
                true, true, () -> new SettingsMenu(instance, profile).openMenu(player)
        ));

        buttons.put(16, new EasyButton(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name("&b&lStatistics")
                        .lore(Arrays.asList(
                                "&fView your personal statistics,",
                                " ",
                                "&bClick to open"
                        ))
                        .build(),
                true, true, () -> new YourStatsMenu(instance).openMenu(player)
        ));

        for (int i = 0; i < getSize(); i++) {
            buttons.putIfAbsent(i, new DisplayButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build()
            ));
        }

        return buttons;
    }
}