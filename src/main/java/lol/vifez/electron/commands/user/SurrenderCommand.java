package lol.vifez.electron.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.CommandPermission;
import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.match.MatchManager;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("surrender|forfeit")
@CommandPermission("electron.user")
public class SurrenderCommand extends BaseCommand {

    private final MatchManager matchManager = Practice.getInstance().getMatchManager();

    @Default
    public void onSurrender(Player player) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            player.sendMessage(CC.translate("&cProfile not found."));
            return;
        }

        Match match = matchManager.getMatch(player.getUniqueId());
        if (match == null || match.getMatchState() != MatchState.STARTED) {
            player.sendMessage(CC.translate("&cYou are not in a match."));
            return;
        }

        if (match.isRanked()) {
            player.sendMessage(CC.translate("&cYou cannot forfiet a ranked match."));
            return;
        }

        Profile opponent = match.getOpponent(profile);
        match.setWinner(opponent);

        player.sendMessage(" ");
        player.sendMessage(CC.translate("&c&lYou have surrendered!"));
        player.sendMessage(CC.translate("&7You lost the match."));
        player.sendMessage(" ");

        opponent.getPlayer().sendMessage(" ");
        opponent.getPlayer().sendMessage(CC.translate("&a&lYour opponent surrendered!"));
        opponent.getPlayer().sendMessage(CC.translate("&7You won the match."));
        opponent.getPlayer().sendMessage(" ");

        matchManager.end(match);
    }
}