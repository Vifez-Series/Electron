package lol.vifez.electron.game.kit.menu.editor;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.DisplayButton;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class KitSelectMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player p0) {
        return "&8Kit Editor";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Kit> kits = new ArrayList<>(instance.getKitManager().getKits().values());
        int slot = 10;

        for (Kit kit : kits) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(kit.getDisplayItem())
                            .name(kit.getColor() + kit.getName())
                            .lore("&r", "&fClick to edit " + kit.getColor() + kit.getName() + "&f's kit layout")
                            .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                            .build(),
                    true, false, () -> {
                Profile profile = instance.getProfileManager().getProfile(p0.getUniqueId());

                if (profile.getKitLoadout().get(kit.getName()) == null) {
                    p0.getInventory().setContents(kit.getContents());
                } else {
                    p0.getInventory().setContents(profile.getKitLoadout().get(kit.getName()));
                }

                setClosedByMenu(true);
                p0.closeInventory();
                new KitEditMenu(kit).openMenu(p0);
            }
            ));
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }

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