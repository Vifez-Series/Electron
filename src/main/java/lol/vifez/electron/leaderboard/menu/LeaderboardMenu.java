package lol.vifez.electron.leaderboard.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.leaderboard.Leaderboard;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class LeaderboardMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player player) {
        return "&7Leaderboard";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Profile> topPlayers = instance.getProfileManager().getProfiles().values().stream()
                .sorted(Comparator.comparingInt(EloUtil::getGlobalElo).reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<String> lore = new ArrayList<>();
        lore.add("&fShowing top 10 results");
        lore.add(" ");
        for (int i = 0; i < topPlayers.size(); i++) {
            Profile profile = topPlayers.get(i);
            int globalElo = EloUtil.getGlobalElo(profile);
            int rank = i + 1;
            String color = Leaderboard.getRankColor(rank);
            lore.add(color + "✩" + rank + " &f" + profile.getName() + " &7[&b" + globalElo + "&7]");
        }

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.NETHER_STAR)
                        .name("&b&lGlobal Elo")
                        .lore(lore)
                        .build(),
                true, true, () -> {}
        ));

        buttons.put(40, new EasyButton(
                new ItemBuilder(Material.BOOK)
                        .name("&a&lYour Stats")
                        .lore(Arrays.asList(
                                "&fDisplaying top 10 players",
                                "&ffor each kit and globally.",
                                " ",
                                "&aClick to view!"
                        ))
                        .build(),
                true, true, () -> new YourStatsMenu(instance).openMenu(player)
        ));

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int startIndex = 10;

        for (int i = 0; i < kits.length; i++) {
            int slot = startIndex + i;
            buttons.put(slot, new LeaderboardButton(instance, kits[i]));
        }

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

class LeaderboardButton extends EasyButton {
    public LeaderboardButton(Practice instance, Kit kit) {
        super(buildItem(instance, kit), true, true, () -> {});
    }

    private static ItemStack buildItem(Practice instance, Kit kit) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Showing top 10 results");
        lore.add(" ");
        lore.addAll(Arrays.asList(instance.getLeaderboards().getLeaderboardLayout(kit)));

        return new ItemBuilder(kit.getDisplayItem())
                .name(kit.getColor() + kit.getName())
                .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                .lore(lore)
                .build();
    }
}