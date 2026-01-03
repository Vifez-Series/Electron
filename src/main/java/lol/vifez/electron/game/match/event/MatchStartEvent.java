package lol.vifez.electron.game.match.event;

import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/
@Getter
@RequiredArgsConstructor
public class MatchStartEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Profile profileOne, profileTwo;
    private final Match match;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
