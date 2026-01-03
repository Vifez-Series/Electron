package lol.vifez.electron.divisions.menus;

import lol.vifez.electron.divisions.Divisions;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class DivisionsMenu extends Menu {

    private static final int ITEMS_PER_PAGE = 15;
    private final int page;

    public DivisionsMenu(int page) {
        this.page = page;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Divisions &7[&b" + page +"&7]";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Divisions> divisions = Arrays.asList(Divisions.values());

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, divisions.size());

        int row = 1;
        int col = 0;
        int startSlot = 11;

        for (int i = startIndex; i < endIndex; i++) {
            Divisions division = divisions.get(i);

            int slot = startSlot + col + (row - 1) * 9;
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(division.getMaterial())
                            .name(division.getPrettyName())
                            .lore("&7Minimum Elo: &b" + division.getMinimumElo())
                            .build(),
                    true, false, () -> {}
            ));

            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        if (page > 1) {
            buttons.put(36, nav("&cPrevious Page", page - 1, player));
        }

        if (hasNextPage(divisions.size())) {
            buttons.put(44, nav("&aNext Page", page + 1, player));
        }

        return buttons;
    }

    private Button nav(String name, int targetPage, Player player) {
        return new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name(name)
                        .lore("&7Click to view page " + targetPage)
                        .build(),
                true, false,
                () -> new DivisionsMenu(targetPage).openMenu(player)
        );
    }

    private boolean hasNextPage(int total) {
        return page * ITEMS_PER_PAGE < total;
    }
}