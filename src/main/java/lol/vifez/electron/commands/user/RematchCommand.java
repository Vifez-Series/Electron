package lol.vifez.electron.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("rematch")
@CommandPermission("electron.user")
public class RematchCommand extends BaseCommand {

    @Default
    public void onRematch(Player player) {
        Practice plugin = Practice.getInstance();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {
            CC.sendMessage(player, "&cProfile could not be found.");
            return;
        }

        if (profile.getMatch() != null) {
            CC.sendMessage(player, "&cYour in a match dude.");
            return;
        }

        if (profile.getRematchOpponent() == null || profile.getRematchKit() == null) {
            CC.sendMessage(player, "&cNo entries.");
            return;
        }

        Player opponentPlayer = profile.getRematchOpponent();
        if (opponentPlayer == null || !opponentPlayer.isOnline()) {
            CC.sendMessage(player, "&cPlayer not online.");
            return;
        }

        Profile opponent = plugin.getProfileManager().getProfile(opponentPlayer.getUniqueId());
        if (opponent == null || opponent.getMatch() != null) {
            CC.sendMessage(player, "&cUser busy.");
            return;
        }

        Kit kit = profile.getRematchKit();

        profile.sendDuelRequest(opponentPlayer, kit);

        TextComponent message = new TextComponent(CC.translate("&b" + player.getName() + " &fhas requested a rematch! "));

        opponentPlayer.spigot().sendMessage(message);
    }
}