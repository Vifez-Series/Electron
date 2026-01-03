package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class WorldTimeButton {

    public static Button createWorldTimeButton(Profile profile, Practice instance) {
        String worldTime = profile.getWorldTime();

        ItemStack worldTimeItem = new ItemBuilder(Material.NETHER_STAR)
                .name("&eWorld Time")
                .lore(
                        "&fChange the sky time in arenas.",
                        "",
                        "&fCurrent: &a" + formatTime(worldTime),
                        "",
                        "&eClick to cycle"
                )
                .build();

        return new EasyButton(worldTimeItem, true, false, () -> {
            String newTime = getNextTime(profile.getWorldTime());
            profile.setWorldTime(newTime);
            Practice.getInstance().getProfileManager().save(profile);

            Player player = profile.getPlayer();
            if (player != null) {
                applyWorldTime(player, newTime);
                new SettingsMenu(instance, profile).openMenu(player);
            }
        });
    }

    private static String getNextTime(String current) {
        switch (current) {
            case "DAY":
                return "SUNSET";
            case "SUNSET":
                return "NIGHT";
            default:
                return "DAY";
        }
    }

    private static String formatTime(String time) {
        switch (time) {
            case "SUNSET":
                return "Sunset";
            case "NIGHT":
                return "Night";
            default:
                return "Day";
        }
    }

    private static void applyWorldTime(Player player, String time) {
        World world = player.getWorld();

        switch (time) {
            case "DAY":
                world.setTime(1000);
                break;
            case "SUNSET":
                world.setTime(12500);
                break;
            case "NIGHT":
                world.setTime(18000);
                break;
        }
    }
}