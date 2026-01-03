package lol.vifez.electron.game.duel.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
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

/**
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 * Project: Essence
 */

@RequiredArgsConstructor
public class DuelKitMenu extends Menu {

    private final Practice instance;
    private final Player target;

    @Override
    public String getTitle(Player player) {
        return "&7Select a kit &7(duel)";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int[] kitSlots = {10, 11, 12, 14, 15, 16, 19, 20, 21, 23, 24, 25};

        for (int i = 0; i < kits.length && i < kitSlots.length; i++) {
            buttons.put(kitSlots[i], new DuelKitButton(instance, player, target, kits[i]));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
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

        return buttons;
    }

    @Override
    public int getSize() {
        return 45;
    }
}

class DuelKitButton extends EasyButton {

    public DuelKitButton(Practice instance, Player player, Player target, Kit kit) {
        super(new ItemBuilder(kit.getDisplayItem())
                .name("&b&l" + kit.getName())
                .lore("",
                        "&7Click to send &b" + target.getName() + " &7a duel request")
                .flag(ItemFlag.HIDE_ATTRIBUTES)
                .build(), true, false, () -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());

            if (targetProfile == null) return;

            player.closeInventory();
            profile.sendDuelRequest(target, kit);
        });
    }
}