package lol.vifez.electron.kit;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.enums.KitType;
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

    private final String name;
    private List<String> description = new ArrayList<>();

    private ItemStack[] contents = new ItemStack[]{}, armorContents = new ItemStack[]{};
    private ItemStack icon = new ItemStack(Material.BOOK);

    private ChatColor color = ChatColor.AQUA;
    private KitType kitType = KitType.REGULAR;

    private int weight = 0;
    private boolean ranked = false;
    private boolean bedFight = false;

    public ItemStack getDisplayItem() {
        ItemStack item = icon.clone();
        return new lol.vifez.electron.util.ItemBuilder(item)
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
        section.set("bedFight", bedFight);
    }

    public static Kit fromConfig(String name, ConfigurationSection section) {
        Kit kit = new Kit(name);
        kit.setDescription(section.getStringList("description"));

        try {
            String contentsData = section.getString("contents");
            if (contentsData != null && !contentsData.isEmpty()) {
                kit.setContents(SerializationUtil.deserializeItemStackArray(contentsData));
            } else {
                kit.setContents(new ItemStack[0]);
            }
        } catch (Exception e) {
            kit.setContents(new ItemStack[0]);
            Practice.getInstance().getLogger().warning("Failed to load contents for kit " + name + ": " + e.getMessage());
        }

        try {
            String armorData = section.getString("armorContents");
            if (armorData != null && !armorData.isEmpty()) {
                kit.setArmorContents(SerializationUtil.deserializeItemStackArray(armorData));
            } else {
                kit.setArmorContents(new ItemStack[0]);
            }
        } catch (Exception e) {
            kit.setArmorContents(new ItemStack[0]);
            Practice.getInstance().getLogger().warning("Failed to load armor for kit " + name + ": " + e.getMessage());
        }

        try {
            String iconData = section.getString("icon");
            if (iconData != null && !iconData.isEmpty()) {
                kit.setIcon(SerializationUtil.deserializeItemStack(iconData));
            } else if (section.isString("icon")) {
                try {
                    kit.setIcon(new ItemStack(Material.valueOf(section.getString("icon"))));
                } catch (IllegalArgumentException ignored) {
                    kit.setIcon(new ItemStack(Material.BOOK));
                }
            }
        } catch (Exception e) {
            kit.setIcon(new ItemStack(Material.BOOK));
            Practice.getInstance().getLogger().warning("Failed to load icon for kit " + name + ": " + e.getMessage());
        }

        if (section.isString("color")) {
            try { kit.setColor(ChatColor.valueOf(section.getString("color"))); } catch (IllegalArgumentException ignored) {}
        }
        if (section.isString("kitType")) {
            try { kit.setKitType(KitType.valueOf(section.getString("kitType"))); } catch (IllegalArgumentException ignored) {}
        }

        kit.setWeight(section.getInt("weight", 0));
        kit.setRanked(section.getBoolean("ranked", false));
        kit.setBedFight(section.getBoolean("bedFight", false));

        return kit;
    }
}