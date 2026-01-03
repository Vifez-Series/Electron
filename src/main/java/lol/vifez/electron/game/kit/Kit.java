package lol.vifez.electron.game.kit;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.SerializationUtil;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Data
public class Kit {

    private static final ItemStack[] EMPTY_ITEMS = new ItemStack[0];
    private static final ItemStack DEFAULT_ICON = new ItemStack(Material.BOOK);

    private final String name;
    private List<String> description = new ArrayList<>();
    private ItemStack[] contents = EMPTY_ITEMS;
    private ItemStack[] armorContents = EMPTY_ITEMS;
    private ItemStack icon = DEFAULT_ICON;

    private ChatColor color = ChatColor.AQUA;
    private KitType kitType = KitType.REGULAR;

    private int weight;
    private boolean ranked;

    public ItemStack getDisplayItem() {
        return new ItemBuilder(icon.clone())
                .name(color + name)
                .lore(description)
                .build();
    }

    public void toConfig(ConfigurationSection section) {
        section.set("description", description);
        section.set("contents", SerializationUtil.serializeItemStackArray(contents));
        section.set("armorContents", SerializationUtil.serializeItemStackArray(armorContents));
        section.set("icon", SerializationUtil.serializeItemStack(icon));
        section.set("color", color.name());
        section.set("kitType", kitType.name());
        section.set("weight", weight);
        section.set("ranked", ranked);
    }

    public static Kit fromConfig(String name, ConfigurationSection section) {
        Kit kit = new Kit(name);
        kit.setDescription(section.getStringList("description"));
        kit.setContents(deserializeItemArray(section, "contents"));
        kit.setArmorContents(deserializeItemArray(section, "armorContents"));
        kit.setIcon(deserializeIcon(section));
        kit.setColor(parseEnum(section, "color", ChatColor.class, ChatColor.AQUA));
        kit.setKitType(parseEnum(section, "kitType", KitType.class, KitType.REGULAR));
        kit.setWeight(section.getInt("weight", 0));
        kit.setRanked(section.getBoolean("ranked", false));

        return kit;
    }

    private static ItemStack[] deserializeItemArray(ConfigurationSection section, String key) {
        String data = section.getString(key);
        if (data == null || data.isEmpty()) {
            return EMPTY_ITEMS;
        }

        try {
            return SerializationUtil.deserializeItemStackArray(data);
        } catch (Exception e) {
            logWarning("Failed to load " + key, e);
            return EMPTY_ITEMS;
        }
    }

    private static ItemStack deserializeIcon(ConfigurationSection section) {
        String data = section.getString("icon");
        if (data == null || data.isEmpty()) {
            return DEFAULT_ICON.clone();
        }

        try {
            return SerializationUtil.deserializeItemStack(data);
        } catch (Exception ignored) {
            try {
                return new ItemStack(Material.valueOf(data));
            } catch (Exception e) {
                logWarning("Failed to load icon", e);
                return DEFAULT_ICON.clone();
            }
        }
    }

    private static <T extends Enum<T>> T parseEnum(
            ConfigurationSection section,
            String key,
            Class<T> enumClass,
            T fallback
    ) {
        String value = section.getString(key);
        if (value == null) {
            return fallback;
        }

        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static void logWarning(String message, Exception e) {
        Practice.getInstance()
                .getLogger()
                .warning(message + ": " + e.getMessage());
    }
}