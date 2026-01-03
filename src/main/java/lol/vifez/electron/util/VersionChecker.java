package lol.vifez.electron.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lol.vifez.electron.Practice;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class VersionChecker {

    private static final String URL_JSON =
            "https://raw.githubusercontent.com/Vifez-Series/Electron/master/version.json";

    private static String latest, download;
    private static List<String> changelog = new ArrayList<>();

    private VersionChecker() {}

    public static void check() {
        if (!enabled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
            try {
                JsonObject json = new JsonParser()
                        .parse(new InputStreamReader(new URL(URL_JSON).openStream()))
                        .getAsJsonObject();

                latest = json.get("version").getAsString();
                download = json.get("download").getAsString();

                changelog.clear();
                JsonArray log = json.getAsJsonArray("changelog");
                if (log != null) log.forEach(e -> changelog.add(e.getAsString()));

                if (outdated()) logConsole();
            } catch (Exception ignored) {
                Practice.getInstance().getLogger()
                        .warning("Update check failed.");
            }
        });
    }

    public static void notify(Player p) {
        if (!enabled() || !p.hasPermission("electron.admin") || !outdated()) return;

        p.sendMessage(CC.translate("&7&m-----------------------------------------"));
        p.sendMessage(CC.translate("&b&lElectron Practice"));
        p.sendMessage(CC.translate("&cYou're running an old version!"));
        p.sendMessage(" ");
        p.sendMessage(CC.translate("&fCurrent: &c" + current()));
        p.sendMessage(CC.translate("&fLatest: &b" + latest));
        p.sendMessage(" ");

        TextComponent link = new TextComponent(CC.translate("&7Download: &bClick here"));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, download));
        link.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(CC.translate("&fDownload latest")).create()
        ));
        p.spigot().sendMessage(link);

        if (!changelog.isEmpty()) {
            p.sendMessage(" ");
            p.sendMessage(CC.translate("&bChangelog:"));
            changelog.forEach(l -> p.sendMessage(CC.translate("&7- " + l)));
        }

        p.sendMessage(CC.translate("&7&m-----------------------------------------"));
    }

    private static boolean outdated() {
        return latest != null && compare(current(), latest);
    }

    private static String current() {
        return Practice.getInstance().getDescription().getVersion();
    }

    private static boolean enabled() {
        return Practice.getInstance()
                .getConfig()
                .getBoolean("SETTINGS.UPDATE-CHECKER.ENABLED", true);
    }

    private static boolean compare(String a, String b) {
        String[] x = a.split("\\.");
        String[] y = b.split("\\.");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int ai = i < x.length ? parse(x[i]) : 0;
            int bi = i < y.length ? parse(y[i]) : 0;
            if (ai != bi) return ai < bi;
        }
        return false;
    }

    private static int parse(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception ignored) { return 0; }
    }

    private static void logConsole() {
        Bukkit.getConsoleSender().sendMessage(CC.translate(
                "&bElectron &7| &cOutdated &7(" + current() + " → " + latest + ")\n" +
                "&7Download: &f" + download
        ));
    }

    public enum UpdateStatus {
        LATEST,
        UPDATE,
        UNKNOWN
    }

    public static String getStatusTag() {
        String status;
        switch (getStatus()) {
            case UPDATE:
                status = "&c[UPDATE]";
                break;
            case LATEST:
                status = "&b[LATEST]";
                break;
            default:
                status = "&7[UNKNOWN]";
                break;
        }
        return status;
    }

    public static UpdateStatus getStatus() {
        if (latest == null) return UpdateStatus.UNKNOWN;
        return outdated() ? UpdateStatus.UPDATE : UpdateStatus.LATEST;
    }
}