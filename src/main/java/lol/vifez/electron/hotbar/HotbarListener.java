package lol.vifez.electron.hotbar;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.menu.editor.KitSelectMenu;
import lol.vifez.electron.leaderboard.menu.LeaderboardMenu;
import lol.vifez.electron.navigator.menu.NavigatorMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.queue.Queue;
import lol.vifez.electron.queue.menu.QueuesMenu;
import lol.vifez.electron.queue.menu.RankedMenu;
import lol.vifez.electron.queue.menu.UnrankedMenu;
import lol.vifez.electron.settings.menu.OptionsMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

public final class HotbarListener implements Listener {

    private final Practice instance;
    private final Map<Hotbar, HotbarAction> actions = new EnumMap<>(Hotbar.class);

    public HotbarListener() {
        this.instance = Practice.getInstance();
        registerActions();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!isRightClick(event.getAction())) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInHand();
        if (item == null) return;

        for (Hotbar hotbar : Hotbar.values()) {
            ItemStack hotbarItem = hotbar.getItem();
            if (hotbarItem == null || !hotbarItem.isSimilar(item)) continue;

            HotbarAction action = actions.get(hotbar);
            if (action != null) {
                event.setCancelled(true);
                action.execute(player);
            }
            return;
        }
    }

    private void registerActions() {
        actions.put(Hotbar.UNRANKED, p ->
                new UnrankedMenu(instance).openMenu(p));

        actions.put(Hotbar.RANKED, p ->
                new RankedMenu(instance).openMenu(p));

        actions.put(Hotbar.LEADERBOARDS, p ->
                new LeaderboardMenu(instance).openMenu(p));

        actions.put(Hotbar.QUEUES, p ->
                new QueuesMenu(instance).openMenu(p));

        actions.put(Hotbar.KIT_EDITOR, p ->
                new KitSelectMenu(instance).openMenu(p));

        actions.put(Hotbar.NAVIGATOR, p -> {
            Profile profile = instance.getProfileManager().getProfile(p.getUniqueId());
            if (profile == null) {
                CC.sendMessage(p, "&cProfile not found!");
                return;
            }
            new NavigatorMenu(instance).openMenu(p);
        });

        actions.put(Hotbar.SETTINGS, p -> {
            Profile profile = instance.getProfileManager().getProfile(p.getUniqueId());
            if (profile == null) {
                CC.sendMessage(p, "&cProfile not found!");
                return;
            }
            new OptionsMenu().openMenu(p);
        });

        actions.put(Hotbar.LEAVE_QUEUE, p -> {
            Queue queue = instance.getQueueManager().getQueue(p.getUniqueId());
            if (queue != null) queue.remove(p);

            p.getInventory().setContents(Hotbar.getSpawnItems());
            p.getInventory().setArmorContents(null);
            CC.sendMessage(p, "&cYou left the queue!");
        });
    }

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    @FunctionalInterface
    private interface HotbarAction {
        void execute(Player player);
    }
}