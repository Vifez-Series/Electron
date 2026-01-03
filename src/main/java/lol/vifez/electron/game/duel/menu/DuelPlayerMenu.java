package lol.vifez.electron.game.duel.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 * Project: Essence
 */

public class DuelPlayerMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player p0) {
        return "";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.canSee(p0) && p0.canSee(p))
                .filter(p -> p != p0)
                .filter(p -> Practice.getInstance().getProfileManager().getProfile(p.getUniqueId()).isDuelRequestsEnabled())
                .collect(Collectors.toList())) {
            buttons.put(buttons.size(), new DuelPlayerButton(Practice.getInstance(), Practice.getInstance().getProfileManager().getProfile(p0.getUniqueId()), Practice.getInstance().getProfileManager().getProfile(player.getUniqueId())));
        }

        return buttons;
    }
}

class DuelPlayerButton extends Button {

    private final Practice instance;
    private final Profile profile;
    private final Profile target;

    DuelPlayerButton(Practice instance, Profile profile, Profile target) {
        this.instance = instance;
        this.profile = profile;
        this.target = target;
    }

    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.SKULL_ITEM)
                .name("&b" + target.getName())
                .skullOwner(target.getName())
                .lore("&r",
                        "&7Click to send a duel request to " + target.getName(),
                        "&r")
                .build();
    }

    @Override
    public void onClick(Player player, int slot, ClickType type) {
        Player target = Bukkit.getPlayer(this.target.getUuid());

        if (target == null || !target.isOnline() || this.target.inMatch()) return;

        new DuelKitMenu(instance, target).openMenu(player);
    }
}