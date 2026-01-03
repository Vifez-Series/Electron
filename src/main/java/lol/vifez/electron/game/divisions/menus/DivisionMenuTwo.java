package lol.vifez.electron.game.divisions.menus;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.divisions.Divisions;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class DivisionMenuTwo extends Menu {

    private final Practice instance = Practice.getInstance();

    @Override
    public String getTitle(Player player) {
        return "&7Divisions &8- &7Page 2";
    }

    @Override
    public int getSize() {
        return 45;
    }

    private Material getMaterialForDivision(Divisions division) {
        String name = division.name();
        if (name.startsWith("SILVER")) return Material.IRON_INGOT;
        if (name.startsWith("IRON")) return Material.IRON_BLOCK;
        if (name.startsWith("GOLD")) return Material.GOLD_INGOT;
        if (name.startsWith("DIAMOND")) return Material.DIAMOND;
        if (name.startsWith("EMERALD")) return Material.EMERALD;
        if (name.startsWith("MASTER")) return Material.NETHER_STAR;
        if (name.startsWith("GRANDMASTER")) return Material.BEACON;
        if (name.startsWith("LEGEND") || name.startsWith("SUPER")) return Material.DRAGON_EGG;
        return Material.BARRIER;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int row = 1;
        int itemsPerRow = 5;
        int startSlot = (9 * row) + 2;
        int colOffset = 0;

        for (Divisions division : Divisions.values()) {
            if (division.ordinal() < Divisions.DIAMOND_I.ordinal()) continue;
            if (division.ordinal() > Divisions.MASTER_V.ordinal()) break;

            int slot = startSlot + colOffset;

            ItemStack item = new ItemBuilder(getMaterialForDivision(division))
                    .name(division.getPrettyName())
                    .lore("&7Minimum Elo: &b" + division.getMinimumElo())
                    .build();

            buttons.put(slot, new EasyButton(item, true, false, () -> {}));

            colOffset++;
            if (colOffset == itemsPerRow) {
                row++;
                startSlot = (9 * row) + 2;
                colOffset = 0;
            }
        }

        buttons.put(36, new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name("&cPrevious Page")
                        .lore("&7Click to view previous page")
                        .build(),
                true,
                false,
                () -> new DivisionsMenu(1).openMenu(player)
        ));

        return buttons;
    }
}