package lol.vifez.electron.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerMoveEvent;
import lol.vifez.electron.util.CC;
import org.bukkit.material.Bed;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.match.task.MatchRespawnTask;
import org.bukkit.Bukkit;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class MatchListener implements Listener {

    private final Practice plugin;

    public MatchListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) return;

        Match match = profile.getMatch();
        MatchState state = match.getMatchState();

        if (state == MatchState.STARTING || state == MatchState.ENDING || state == MatchState.ENDED) {
            event.setCancelled(true);
            return;
        }

        if (match.getKit().isBedFight() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) return;

        Match match = profile.getMatch();
        if (match.getMatchState() == MatchState.STARTING && match.getKit().isBedFight()) {
            if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                player.teleport(event.getFrom());
            }
        }

        if (match.getMatchState() == MatchState.STARTED && match.getKit().isBedFight()) {
            double spawnY = match.getArena().getSpawnA().getY();
            if (player.getLocation().getY() < spawnY - 25 && player.getHealth() > 0) {
                player.setHealth(0.0D);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) return;

        Match match = profile.getMatch();
        Profile killer = match.getOpponent(profile);

        event.getDrops().clear();
        event.setDeathMessage(null);

        // Instant respawn to avoid death screen
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && player.isDead()) {
                ((CraftPlayer) player).getHandle().playerConnection.a(
                        new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN)
                );
            }
        });

        if (match.getKit().isBedFight()) {
            String victimColor = match.getTeamChatColor(profile);
            String victimName = victimColor + player.getName();

            if (player.getKiller() != null) {
                Player killerPlayer = player.getKiller();
                Profile killerProfile = plugin.getProfileManager().getProfile(killerPlayer.getUniqueId());
                String killerColor = match.getTeamChatColor(killerProfile);
                String killerName = killerColor + killerPlayer.getName();

                match.getPlayerOne().getPlayer().sendMessage(CC.translate(victimName + " &7was killed by " + killerName + "&7."));
                match.getPlayerTwo().getPlayer().sendMessage(CC.translate(victimName + " &7was killed by " + killerName + "&7."));
            } else {
                match.getPlayerOne().getPlayer().sendMessage(CC.translate(victimName + " &7fell into the void."));
                match.getPlayerTwo().getPlayer().sendMessage(CC.translate(victimName + " &7fell into the void."));
            }

            if (!match.isBedBroken(profile)) {
                new MatchRespawnTask(match, player, 3).runTaskTimer(plugin, 1L, 20L);
                return;
            }
        }

        match.setWinner(killer);
        plugin.getMatchManager().end(match);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) return;

        Match match = profile.getMatch();
        Block block = event.getBlock();

        if (match.getKit().isBedFight()) {
            if (block.getType() == Material.BED_BLOCK) {
                double distToA = block.getLocation().distanceSquared(match.getArena().getSpawnA());
                double distToB = block.getLocation().distanceSquared(match.getArena().getSpawnB());

                Profile bedOwner = distToA < distToB ? match.getPlayerOne() : match.getPlayerTwo();

                if (bedOwner.equals(profile)) {
                    player.sendMessage(CC.translate("&cYou cannot break your own bed!"));
                    event.setCancelled(true);
                    return;
                }

                if (!match.isBedBroken(bedOwner)) {
                    match.setBedBroken(bedOwner, true);
                    
                    player.sendMessage(CC.translate("&aYou have broken " + bedOwner.getPlayer().getName() + "'s bed!"));
                    player.sendTitle(CC.translate("&a&lBED DESTROYED!"), CC.translate("&fThe enemy will no longer respawn!"));
                    player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);

                    bedOwner.getPlayer().sendMessage(CC.translate("&cYour bed has been broken!"));
                    bedOwner.getPlayer().sendTitle(CC.translate("&c&lBED DESTROYED!"), CC.translate("&fYou will no longer respawn!"));
                    bedOwner.getPlayer().playSound(bedOwner.getPlayer().getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
                    
                    // Remove both halves of the bed
                    Bed bed = (Bed) block.getState().getData();
                    BlockFace face = bed.getFacing();
                    if (bed.isHeadOfBed()) face = face.getOppositeFace();
                    Block otherHalf = block.getRelative(face);
                    if (otherHalf.getType() == Material.BED_BLOCK) {
                        otherHalf.setType(Material.AIR);
                    }
                    
                    block.setType(Material.AIR);
                    event.setCancelled(true); // Cancel to prevent default drops
                } else {
                    event.setCancelled(true);
                }
                return;
            }
            
            // Spawn protection logic
            double distToSpawn1 = block.getLocation().distanceSquared(match.getArena().getPositionOne());
            double distToSpawn2 = block.getLocation().distanceSquared(match.getArena().getPositionTwo());
            if (distToSpawn1 < 25 || distToSpawn2 < 25) { // 5 blocks radius
                player.sendMessage(CC.translate("&cYou cannot break blocks near spawn!"));
                event.setCancelled(true);
                return;
            }

            // Allow breaking wool/blocks placed during match and give item back
            if (block.getType() == Material.WOOL || block.getType() == Material.WOOD || block.getType() == Material.ENDER_STONE) {
                event.setCancelled(false);
                player.getInventory().addItem(new ItemStack(block.getType(), 1, block.getData()));
                player.updateInventory();
                block.setType(Material.AIR);
            } else {
                event.setCancelled(true);
            }
        } else {
            // Default behavior for non-bedfight matches
            if (!match.getArena().getBlocksBuilt().contains(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null || !profile.inMatch()) return;

        Match match = profile.getMatch();
        if (match.getMatchState() != MatchState.STARTED) {
            event.setCancelled(true);
            return;
        }

        if (match.getKit().isBedFight()) {
            double spawnY = match.getArena().getSpawnA().getY();
            if (event.getBlock().getLocation().getY() > spawnY + 10) {
                player.sendMessage(CC.translate("&cYou have reached the build height limit!"));
                event.setCancelled(true);
                return;
            }
            event.setCancelled(false);
        } else {
            // Default behavior
            match.getArena().getBlocksBuilt().add(event.getBlock());
        }
    }
}