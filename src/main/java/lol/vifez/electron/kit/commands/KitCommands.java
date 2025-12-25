package lol.vifez.electron.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.util.CC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@CommandAlias("kit")
@CommandPermission("electron.admin")
public class KitCommands extends BaseCommand {

    private final Practice instance = Practice.getInstance();

    @Default
    public void onKitCommand(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lKit Commands"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&f • &b/kit create &f<kit> &7- &fCreate a new kit"));
        sender.sendMessage(CC.translate("&f • &b/kit delete &f<kit> &7- &fDelete an existing kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setType &f<kit> <type> &7- &fSet the type of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setInventory &f<kit> &7- &fSet the inventory of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit getInventory &f<kit> &7- &fGet the inventory of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setIcon &f<kit> &7- &fSet the icon for a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setRanked &f<kit> &7- &fToggle whether a kit is ranked"));
        sender.sendMessage(CC.translate("&f • &b/kit setBedFight &f<kit> &7- &fToggle whether a kit is BedFight"));
        sender.sendMessage(CC.translate("&f • &b/kit setDescription &f<kit> <description> &7- &fSet the description of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit list &7- &fList all kits"));
        sender.sendMessage(CC.translate("&f • &b/kit save &7- &fSave all kits"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("create")
    public void create(CommandSender sender, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit != null) {
            sender.sendMessage(CC.translate("&cKit already exists!"));
            return;
        }

        kit = new Kit(kitName);
        kit.setIcon(new ItemStack(Material.BOOK));
        instance.getKitManager().save(kit);

        sender.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fKit created"));
    }

    @Subcommand("delete")
    public void delete(CommandSender sender, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            sender.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        instance.getKitManager().delete(kit);
        sender.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fKit deleted"));
    }

    @Subcommand("save")
    @Description("Save all kits to kits.yml")
    public void save(CommandSender sender) {
        instance.getKitManager().saveAll();
        sender.sendMessage(CC.translate("&fSaved all &bkits"));
    }

    @Subcommand("setInventory")
    public void setInventory(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        kit.setArmorContents(player.getInventory().getArmorContents());
        kit.setContents(player.getInventory().getContents());
        instance.getKitManager().save(kit);

        player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fLayout saved"));
    }

    @Subcommand("getInventory")
    public void getInventory(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        player.getInventory().clear();
        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmorContents());

        player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fLayout loaded"));
    }

    @Subcommand("setIcon")
    public void setIcon(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        ItemStack item = player.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou must hold an item in your hand!"));
            return;
        }

        kit.setIcon(item.clone());
        instance.getKitManager().save(kit);

        player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fIcon updated"));
    }

    @Subcommand("setType")
    public void setType(Player player, @Name("kit") @Single String kitName, @Name("type") @Single String type) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        try {
            KitType kitType = KitType.valueOf(type.toUpperCase());
            kit.setKitType(kitType);
            instance.getKitManager().save(kit);

            player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fType set to &b" + kitType.name()));
        } catch (IllegalArgumentException e) {
            player.sendMessage(CC.translate("&cInvalid kit type! &7(Use: REGULAR, BUILD, BOXING, WATER_KILL)"));
        }
    }

    @Subcommand("setRanked")
    public void setRanked(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        kit.setRanked(!kit.isRanked());
        instance.getKitManager().save(kit);

        player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fRanked mode " +
                (kit.isRanked() ? "&aenabled" : "&cdisabled")));
    }

    @Subcommand("setBedFight")
    public void setBedFight(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            player.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        kit.setBedFight(!kit.isBedFight());
        instance.getKitManager().save(kit);

        player.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fBedFight mode " +
                (kit.isBedFight() ? "&aenabled" : "&cdisabled")));
    }

    @Subcommand("setDescription")
    public void setDescription(CommandSender sender, @Name("kit") @Single String kitName, String[] descriptionArgs) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            sender.sendMessage(CC.translate("&cKit not found!"));
            return;
        }

        String descriptionLine = String.join(" ", descriptionArgs);
        List<String> lore = new ArrayList<>();
        lore.add(descriptionLine);
        kit.setDescription(lore);

        instance.getKitManager().save(kit);
        sender.sendMessage(CC.translate("&7[&b" + kit.getName() + "&7] &fDescription updated"));
        sender.sendMessage(CC.translate("&fDescription: &7" + descriptionLine));
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        sender.sendMessage(CC.translate("&bKit list"));
        for (Kit kit : instance.getKitManager().getKits().values()) {
            sender.sendMessage(CC.translate("&f • &b" + kit.getName()));
        }
    }
}