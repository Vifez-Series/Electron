package lol.vifez.electron.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import lol.vifez.electron.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Match {

    private final Practice instance;
    private final Profile playerOne, playerTwo;
    private final Kit kit;
    private final Arena arena;
    private final boolean ranked, waterKill;

    private Profile winner = null;
    private MatchState matchState = MatchState.STARTING;

    private int countdownTime = 5;
    @Getter @Setter
    private int currentCountdown = -1;
    @Getter @Setter
    private boolean countdownRunning = false;

    private Instant startTime;
    private Map<UUID, Integer> hitsMap = new HashMap<>();
    private boolean bedBrokenOne = false, bedBrokenTwo = false;

    public Match(Practice instance, Profile playerOne, Profile playerTwo, Kit kit, Arena arena, boolean ranked) {
        this.instance = instance;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.kit = kit;
        this.arena = arena;
        this.startTime = Instant.now();
        this.ranked = ranked;
        this.waterKill = kit.getKitType() == KitType.WATER_KILL;

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

        // Clear immediately to prevent lobby items from being used
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();

        Bukkit.getScheduler().runTask(instance, () -> {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            ItemStack[] kitContents = profile.getKitLoadout().getOrDefault(kit.getName(), kit.getContents()).clone();
            ItemStack[] contents = new ItemStack[36];
            System.arraycopy(kitContents, 0, contents, 0, Math.min(kitContents.length, 36));
            
            if (kit.isBedFight()) {
                Color teamColor = getTeamColor(profile);
                short durability = getTeamDurability(profile);

                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item == null) continue;

                    if (item.getType() == Material.WOOL || item.getType() == Material.STAINED_CLAY || item.getType() == Material.STAINED_GLASS) {
                        contents[i] = new ItemBuilder(item).durability(durability).build();
                    }
                }

                ItemStack[] armor = kit.getArmorContents().clone();
                for (int i = 0; i < armor.length; i++) {
                    ItemStack piece = armor[i];
                    if (piece != null && piece.getType().name().contains("LEATHER_")) {
                        armor[i] = new ItemBuilder(piece).color(teamColor).build();
                    }
                }
                player.getInventory().setArmorContents(armor);
            } else {
                player.getInventory().setArmorContents(kit.getArmorContents());
            }

            player.getInventory().setContents(contents);
            player.updateInventory();
        });

        if (profile.getQueue() != null) profile.getQueue().remove(player);
    }

    public Color getTeamColor(Profile profile) {
        return profile.getUuid().equals(playerOne.getUuid()) ? Color.RED : Color.BLUE;
    }

    public short getTeamDurability(Profile profile) {
        return (short) (profile.getUuid().equals(playerOne.getUuid()) ? 14 : 11);
    }

    public String getTeamChatColor(Profile profile) {
        return profile.getUuid().equals(playerOne.getUuid()) ? "&c" : "&9";
    }

    public void respawn(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        teleportAndSetup(profile, profile.getUuid().equals(playerOne.getUuid()));
        allowMovement(player);
        player.sendMessage(CC.translate("&aYou have respawned!"));
    }

    public boolean isBedBroken(Profile profile) {
        return profile.getUuid().equals(playerOne.getUuid()) ? bedBrokenOne : bedBrokenTwo;
    }

    public void setBedBroken(Profile profile, boolean broken) {
        if (profile.getUuid().equals(playerOne.getUuid())) {
            bedBrokenOne = broken;
        } else {
            bedBrokenTwo = broken;
        }
    }
}