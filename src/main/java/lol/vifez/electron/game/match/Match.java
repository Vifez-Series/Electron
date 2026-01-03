package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@Data
public class Match {

    private final UUID matchId = UUID.randomUUID();

    private final Practice instance;
    private final Profile playerOne, playerTwo;
    private final Kit kit;
    private final Arena arena;
    private final boolean ranked;

    private Profile winner = null;
    private MatchState matchState = MatchState.STARTING;

    private int countdownTime = 5;
    @Getter @Setter private int currentCountdown = -1;
    @Getter @Setter private boolean countdownRunning;
    @Getter @Setter private BukkitTask countdownTask;

    private final Instant startTime = Instant.now();

    private final Map<UUID, Integer> hitsMap = new HashMap<>();
    private boolean bedBrokenOne, bedBrokenTwo;

    public Match(Practice instance, Profile playerOne, Profile playerTwo, Kit kit, Arena arena, boolean ranked) {
        this.instance = instance;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.kit = kit;
        this.arena = arena;
        this.ranked = ranked;

        hitsMap.put(playerOne.getUuid(), 0);
        hitsMap.put(playerTwo.getUuid(), 0);
    }

    public String getDuration() {
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public Profile getOpponent(Profile profile) {
        return profile.getUuid().equals(playerOne.getUuid()) ? playerTwo : playerOne;
    }

    public void denyMovement(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void allowMovement(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);
        player.setSprinting(true);
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void teleportAndSetup(Profile profile, boolean firstSpawn) {
        Player player = profile.getPlayer();
        player.teleport(firstSpawn ? arena.getSpawnA() : arena.getSpawnB());
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        denyMovement(player);

        ItemStack[] contents = profile.getKitLoadout().getOrDefault(kit.getName(), kit.getContents());
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(kit.getArmorContents());
        player.updateInventory();

        if (profile.getQueue() != null) {
            profile.getQueue().remove(player);
        }
    }
}