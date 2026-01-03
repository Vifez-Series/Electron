package lol.vifez.electron.leaderboard.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */
public class YourStatsMenu extends Menu {

    private final Practice instance;

    public YourStatsMenu(Practice instance) {
        this.instance = instance;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Your Stats";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) return buttons;

        int globalElo = EloUtil.getGlobalElo(profile);

        List<String> globalLore = new ArrayList<>();
        globalLore.add("&fYour overall average");
        globalLore.add("&facross all ranked kits.");
        globalLore.add(" ");
        globalLore.add("&fGlobal Elo: &b" + globalElo);
        globalLore.add("&fTotal Wins: &a" + profile.getWins());
        globalLore.add("&fTotal Losses: &c" + profile.getLosses());
        globalLore.add("&fCurrent Win Streak: &e" + profile.getWinStreak());

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.NETHER_STAR)
                        .name("&b&lGlobal Stats")
                        .lore(globalLore)
                        .build(),
                true, true, () -> {}
        ));

        int[] kitSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int index = 0;

        for (Kit kit : kits) {
            if (!kit.isRanked()) continue;
            if (index >= kitSlots.length) break;

            int elo = profile.getElo(kit);

            int kitWins = profile.getKitWins().getOrDefault(kit.getName(), 0);

            int kitLosses = Math.max(0, profile.getLosses() / Math.max(1, kits.length));

            int total = kitWins + kitLosses;
            double winrate = total > 0 ? (kitWins * 100.0 / total) : 0.0;

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("&f• ELO: &b" + elo);
            lore.add(" ");
            lore.add("&f• Won: &a" + kitWins);
            lore.add("&f• Lost: &c" + kitLosses);
            lore.add("&f• Winrate: &e" + String.format("%.1f", winrate) + "%");

            buttons.put(kitSlots[index], new EasyButton(
                    new ItemBuilder(kit.getDisplayItem())
                            .name(kit.getColor() + kit.getName())
                            .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                            .lore(lore)
                            .build(),
                    true, true, () -> {}
            ));

            index++;
        }

        buttons.put(40, new EasyButton(
                new ItemBuilder(Material.BOOK)
                        .name("&a&lLeaderboards")
                        .lore(Arrays.asList(
                                "&fView the global",
                                "&fLeaderboards.",
                                " ",
                                "&aClick to view!"
                        ))
                        .build(),
                true, true, () -> new LeaderboardMenu(instance).openMenu(player)
        ));

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true, false, () -> {}
            ));
        }

        return buttons;
    }
}