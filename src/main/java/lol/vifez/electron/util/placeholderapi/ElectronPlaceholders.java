package lol.vifez.electron.util.placeholderapi;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class ElectronPlaceholders extends PlaceholderExpansion {

    private final Practice instance;

    public ElectronPlaceholders(Practice instance) {
        this.instance = instance;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) return "";

        String param = params.toLowerCase();

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) return "";

        if (param.equals("elo_global")) {
            return String.valueOf(EloUtil.getGlobalElo(profile));
        }

        if (param.startsWith("elo_")) {
            String kitName = param.substring(4);
            Kit kit = instance.getKitManager().getKit(kitName);
            if (kit == null) return "N/A";
            return String.valueOf(profile.getElo(kit));
        }

        return "";
    }

    @Override
    public String getIdentifier() {
        return "practice";
    }

    @Override
    public String getAuthor() {
        return "vifez";
    }

    @Override
    public String getVersion() {
        return instance.getDescription().getVersion();
    }
}