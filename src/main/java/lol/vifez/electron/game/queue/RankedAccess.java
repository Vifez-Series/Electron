package lol.vifez.electron.game.queue;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class RankedAccess {

    private RankedAccess() {}

    public static boolean canAccess(Player player, Profile profile, boolean sendMessage) {
        Practice instance = Practice.getInstance();

        if (!instance.getConfig().getBoolean("SETTINGS.RANKED.REQUIRE-WINS", true)) {
            return true;
        }

        int requiredWins = instance.getConfig().getInt(
                "SETTINGS.RANKED.UNLOCK-AMOUNT",
                10
        );

        if (profile.getWins() >= requiredWins) {
            return true;
        }

        if (sendMessage) {
            CC.sendMessage(player,
                    "&cYou must win &e" + requiredWins + " unranked matches &cto unlock ranked.");
        }

        return false;
    }
}