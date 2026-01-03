package lol.vifez.electron.game.arena.menu;

import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.arena.ArenaManager;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class ArenasMenu extends Menu {

    private final ArenaManager arenaManager;

    @Override
    public String getTitle(Player player) {
        return "&8Manage Arenas";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Arena> arenas = new ArrayList<>(arenaManager.getArenas());

        int slot = 10;
        for (Arena arena : arenas) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(arena.getIcon() == null ? Material.PAPER : arena.getIcon())
                            .name("&b&l" + arena.getName())
                            .lore(Arrays.asList(
                                    " ",
                                    "&fClick to manage &b" + arena.getName() + " &farena.",
                                    " "
                            ))
                            .build(),
                    true,
                    false, () ->
                    new ArenaEditorMenu(arena, arenaManager)
                    .openMenu(player)
            ));

            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };        for (int i : borderSlots) {
            buttons.put(i, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true,false,()->{}
            ));
        }

        return buttons;
    }
}
