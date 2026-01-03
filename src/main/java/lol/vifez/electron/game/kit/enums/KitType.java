package lol.vifez.electron.game.kit.enums;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public enum KitType {
    REGULAR,
    BED_FIGHT,
    BOXING;

    public KitType[] getAll() {
        return new KitType[] {REGULAR, BED_FIGHT, BOXING};
    }
}