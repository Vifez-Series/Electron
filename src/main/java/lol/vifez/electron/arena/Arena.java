package lol.vifez.electron.arena;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import lombok.Data;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Data
public class Arena {

    private final String name;
    private String type;
    private Location spawnA;
    private Location spawnB;
    private Material icon;

    private final List<String> kits;
    private boolean busy;

    private final List<Block> blocksBuilt;
    private final Map<Block, Material> blockBroken;

    private Location positionOne;
    private Location positionTwo;

    public Arena(String name) {
        this.name = name;
        this.type = "default";
        this.icon = Material.PAPER;

        this.kits = new ArrayList<>();
        this.blocksBuilt = new ArrayList<>();
        this.blockBroken = new HashMap<>();

        this.busy = false;
    }

    public Arena(String name, String type, String spawnA, String spawnB, String icon, String positionOne, String positionTwo) {
        this.name = name;
        this.type = type;
        this.icon = Material.getMaterial(icon);

        this.spawnA = parseLocation(spawnA);
        this.spawnB = parseLocation(spawnB);
        this.positionOne = parseLocation(positionOne);
        this.positionTwo = parseLocation(positionTwo);
        this.kits = new ArrayList<>();
        this.blocksBuilt = new ArrayList<>();
        this.blockBroken = new HashMap<>();
    }

    public void setKits(List<String> kits) {
        this.kits.clear();
        if (kits != null) {
            this.kits.addAll(kits);
        }
    }

    private Location parseLocation(String input) {
        if (input == null || input.isEmpty()) { return null;
        }

        String[] parts = input.split(",");
        if (parts.length < 6) {
            return null;
        }

        try {
            var world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public void fixArena() {
        blocksBuilt.stream().filter(Objects::nonNull).forEach(block -> block.setType(Material.AIR));
        blocksBuilt.clear();

        blockBroken.forEach((block, material) -> block.setType(material));
        blockBroken.clear();
    }

    public void kitsCleanup() {
        Practice instance = JavaPlugin.getPlugin(Practice.class);
        kits.removeIf(str -> instance.getKitManager().getKit(str) == null);
    }

    public void teleport(Player player) {
        if (spawnA != null) {
            player.teleport(spawnA);
            return;
        }

        if (spawnB != null) {
            player.teleport(spawnB);
            return;
        }
        player.sendMessage(CC.translate("&cNo spawn point set."));
    }
}