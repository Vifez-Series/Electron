package lol.vifez.electron.kit;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Getter
public class KitManager {

    private final Map<String, Kit> kits = new ConcurrentHashMap<>();

    public KitManager() {
        loadKits();
    }

    private void loadKits() {
        ConfigurationSection section = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .getConfigurationSection("kits");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection kitSection = section.getConfigurationSection(key);
            if (kitSection == null) continue;

            Kit kit = Kit.fromConfig(key, kitSection);
            kits.put(key, kit);
        }
    }

    public Kit getKit(String name) {
        if (name == null) return null;

        for (Kit kit : kits.values()) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public void save(Kit kit) {
        kits.put(kit.getName(), kit);

        ConfigurationSection section = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .createSection("kits." + kit.getName());
        kit.toConfig(section);
        Practice.getInstance().getKitsFile().save();
    }

    public void saveAll() {
        ConfigurationSection root = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .getConfigurationSection("kits");

        if (root == null) {
            root = Practice.getInstance()
                    .getKitsFile()
                    .getConfiguration()
                    .createSection("kits");
        }

        for (Kit kit : kits.values()) {
            ConfigurationSection section = root.getConfigurationSection(kit.getName());
            if (section == null) {
                section = root.createSection(kit.getName());
            }
            kit.toConfig(section);
        }

        Practice.getInstance().getKitsFile().save();
    }

    public void delete(Kit kit) {
        kits.remove(kit.getName());

        Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .set("kits." + kit.getName(), null);

        Practice.getInstance().getKitsFile().save();
    }

    public void close() {
        ConfigurationSection root = Practice.getInstance()
                .getKitsFile()
                .getConfiguration()
                .createSection("kits");

        for (Kit kit : kits.values()) {
            ConfigurationSection section = root.createSection(kit.getName());
            kit.toConfig(section);
        }

        Practice.getInstance().getKitsFile().save();
    }
}