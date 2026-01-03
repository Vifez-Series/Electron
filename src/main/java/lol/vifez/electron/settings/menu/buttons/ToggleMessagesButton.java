package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class ToggleMessagesButton {

    public static Button createToggleMessagesButton(Profile profile, Practice instance) {
        boolean privateMessagingEnabled = profile.isPrivateMessagingEnabled();
        ItemStack messagesItem = new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&ePrivate Messages")
                .lore(
                        "&fAllow players to send you private messages.",
                        "",
                        "&fStatus: " + (privateMessagingEnabled ? "&a&lENABLED" : "&c&lDISABLED"),
                        "",
                        "&eClick to toggle"
                )
                .build();

        return new EasyButton(messagesItem, true, false, () -> {
            profile.setPrivateMessagingEnabled(!privateMessagingEnabled);
            Practice.getInstance().getProfileManager().save(profile);

            new SettingsMenu(instance, profile).openMenu(profile.getPlayer());
        });
    }
}