package lol.vifez.electron.game.queue.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.Map;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class UnrankedMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit...";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int[] kitSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30};

        for (int i = 0; i < kits.length && i < kitSlots.length; i++) {
            buttons.put(kitSlots[i], new KitButton(instance, player, kits[i]));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true, false, () -> {}));
        }

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.FIREWORK)
                        .name("&b&lRandom Queue")
                        .lore("&7Select a random kit")
                        .build(),
                true,
                false,
                () -> {
                    if (kits.length == 0) {
                        CC.sendMessage(player, "&cNo kits available to queue.");
                        return;
                    }

                    Kit randomKit = kits[(int) (Math.random() * kits.length)];
                    Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

                    instance.getQueueManager().getQueue(randomKit, false).add(profile.getPlayer());

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&e&lRandom Queue &7[UR]");
                    CC.sendMessage(player, "&e• &7Kit: &e" + randomKit.getName());
                    CC.sendMessage(player, "&e• &7Searching for a &eplayer...");
                    CC.sendMessage(player, " ");
                    player.closeInventory();
                }
        ));

        return buttons;
    }
}

class KitButton extends EasyButton {

    public KitButton(Practice instance, Player player, Kit kit) {
        super(new ItemBuilder(kit.getDisplayItem())
                .name("&b&l" + kit.getName())
                .lore(
                        "&fPlaying&7: &b" + instance.getMatchManager().getTotalPlayersInMatches(),
                        "&fIn Queue&7: &b" + instance.getQueueManager().getPlayersQueue().size(),
                        "",
                        "&aClick to queue"
                )
                .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                .build(), true, false, () -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            instance.getQueueManager().getQueue(kit, false).add(profile.getPlayer());

            CC.sendMessage(player, " ");
            CC.sendMessage(player, "&b&lUnranked Queue");
            CC.sendMessage(player, "&b• &7Kit: &b" + kit.getName());
            CC.sendMessage(player, "&b• &7Searching for a &bplayer...");
            CC.sendMessage(player, " ");

            player.closeInventory();
        });
    }
}