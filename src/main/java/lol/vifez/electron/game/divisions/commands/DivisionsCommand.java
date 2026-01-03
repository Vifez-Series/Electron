package lol.vifez.electron.game.divisions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.game.divisions.menus.DivisionsMenu;
import org.bukkit.entity.Player;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@CommandAlias("divisions")
public class DivisionsCommand extends BaseCommand {

    @Default
    public void onDivisions(Player player) {
        new DivisionsMenu(1).openMenu(player);
    }
}