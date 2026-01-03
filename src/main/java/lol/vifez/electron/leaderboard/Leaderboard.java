package lol.vifez.electron.leaderboard;

import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.ProfileManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class Leaderboard {

    private final ProfileManager profileManager;

    public Leaderboard(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public List<Profile> getLeaderboard(Kit kit) {
        Comparator<Profile> byElo = Comparator.comparingInt(p -> p.getElo(kit));
        return profileManager.getProfiles().values().stream()
                .sorted(byElo.reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public String[] getLeaderboardLayout(Kit kit) {
        List<Profile> top = getLeaderboard(kit);
        String[] lines = new String[10];

        for (int i = 0; i < 10; i++) {
            int rank = i + 1;
            if (i < top.size()) {
                Profile p = top.get(i);
                lines[i] = getRankColor(rank) + "✩" + rank + " &f" + p.getName() +
                        " &7[&b" + p.getElo(kit) + "&7]";
            } else {
                lines[i] = getRankColor(rank) + "✩" + rank + " &cN/A";
            }
        }
        return lines;
    }

    public static String getRankColor(int rank) {
        if (rank == 1) return "&6";
        if (rank <= 3) return "&e";
        return "&b";
    }
}