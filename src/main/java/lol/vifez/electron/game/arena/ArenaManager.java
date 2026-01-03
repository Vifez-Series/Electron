package lol.vifez.electron.game.arena;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public class ArenaManager {

    private final Map<String, Arena> arenas = new ConcurrentHashMap<>();

    public ArenaManager() {
        ConfigurationSection section = Practice.getInstance().getArenasFile()
                .getConfiguration()
                .getConfigurationSection("arenas");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                String type = section.getString(key + ".type");
                String spawnA = section.getString(key + ".spawnA");
                String spawnB = section.getString(key + ".spawnB");
                String icon = section.getString(key + ".icon");
                String positionOne = section.getString(key + ".positionOne");
                String positionTwo = section.getString(key + ".positionTwo");

                Arena arena = new Arena(key, type, spawnA, spawnB, icon, positionOne, positionTwo);
                arena.setKits(section.getStringList(key + ".kits"));
                arenas.put(key, arena);
            });
        }
    }

    public Arena getArena(String name) {
        for (Arena arena : arenas.values()) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public Set<Arena> getAllAvailableArenas(Kit kit) {
        return CompletableFuture.supplyAsync(() -> arenas.values().stream()
                        .filter(arena -> arena.getKits().stream()
                                .anyMatch(k -> k.equalsIgnoreCase(kit.getName())) && !arena.isBusy())
                        .collect(Collectors.toSet()))
                .join();
    }

    public Arena getAvailableArena(Kit kit) {
        Set<Arena> available = getAllAvailableArenas(kit);
        if (available.isEmpty()) return null;

        int index = new Random().nextInt(available.size());
        return available.stream().skip(index).findFirst().orElse(null);
    }

    public void save(Arena arena) {
        arenas.put(arena.getName(), arena);
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public void delete(Arena arena) {
        arenas.remove(arena.getName());

        Practice.getInstance().getArenasFile().getConfiguration()
                .set("arenas." + arena.getName(), null);
        Practice.getInstance().getArenasFile().save();
    }

    public void close() {
        arenas.values().forEach(arena -> {
            String path = "arenas." + arena.getName();
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".type", arena.getType());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnA", formatLocation(arena.getSpawnA()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnB", formatLocation(arena.getSpawnB()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".icon", arena.getIcon().toString());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionOne", formatLocation(arena.getPositionOne()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionTwo", formatLocation(arena.getPositionTwo()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".kits", arena.getKits());
        });
        Practice.getInstance().getArenasFile().save();
    }

    private String formatLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }
}