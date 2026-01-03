package lol.vifez.electron.game.duel.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.game.duel.DuelRequest;
import lol.vifez.electron.game.duel.menu.DuelKitMenu;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 * Project: Essence
 */

@CommandAlias("duel|d")
public class DuelCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players @kits")
    @Syntax("<player> [kit]")
    public void onDuel(Player sender, @Single @Name("player") String targetName, @Optional @Name("kit") String kitName) {
        if (targetName == null || targetName.isEmpty()) {
            CC.sendMessage(sender, "&cUsage: /duel <player> [kit]");
            return;
        }

        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null || !target.isOnline()) {
            CC.sendMessage(sender, "&cThat player is not online.");
            return;
        }

        if (target.getUniqueId().equals(sender.getUniqueId())) {
            CC.sendMessage(sender, "&cYou cannot duel yourself");
            return;
        }

        Profile senderProfile = Practice.getInstance().getProfileManager().getProfile(sender.getUniqueId());

        if (kitName != null) {
            Kit kit = Practice.getInstance().getKitManager().getKit(kitName);

            if (kit == null) {
                CC.sendMessage(sender, "&cKit '&f" + kitName + "&c' not found.");
                return;
            }

            senderProfile.sendDuelRequest(target, kit);
        } else {
            new DuelKitMenu(Practice.getInstance(), target).openMenu(sender);
        }
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void onAccept(Player player, @Single @Name("player") String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null || !target.isOnline()) {
            CC.sendMessage(player, "&cThat player is not online.");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        DuelRequest request = profile.getDuelRequest();

        if (request == null) {
            CC.sendMessage(player, "&cYou do not have any pending duel requests");
            return;
        }

        if (!request.getSender().getName().equalsIgnoreCase(target.getName())) {
            CC.sendMessage(player, "&cNo pending duel request from &e" + target.getName() + "&CC.");
            return;
        }

        if (request.isExpired()) {
            CC.sendMessage(player, "&cThat duel request has expired.");
            return;
        }

        request.accept();
    }
}