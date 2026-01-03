package lol.vifez.electron.divisions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public enum Divisions {

    SILVER_I("&7&lSilver I", 0, Material.IRON_INGOT),
    SILVER_II("&7&lSilver II", 300, Material.IRON_INGOT),
    SILVER_III("&7&lSilver III", 500, Material.IRON_INGOT),
    SILVER_IV("&7&lSilver IV", 700, Material.IRON_INGOT),
    SILVER_V("&7&lSilver V", 900, Material.IRON_INGOT),

    IRON_I("&f&lIron I", 1000, Material.IRON_BLOCK),
    IRON_II("&f&lIron II", 1050, Material.IRON_BLOCK),
    IRON_III("&f&lIron III", 1100, Material.IRON_BLOCK),
    IRON_IV("&f&lIron IV", 1150, Material.IRON_BLOCK),
    IRON_V("&f&lIron V", 1200, Material.IRON_BLOCK),

    GOLD_I("&6&lGold I", 1225, Material.GOLD_INGOT),
    GOLD_II("&6&lGold II", 1250, Material.GOLD_INGOT),
    GOLD_III("&6&lGold III", 1275, Material.GOLD_INGOT),
    GOLD_IV("&6&lGold IV", 1300, Material.GOLD_INGOT),
    GOLD_V("&6&lGold V", 1350, Material.GOLD_INGOT),

    DIAMOND_I("&b&lDiamond I", 1375, Material.DIAMOND),
    DIAMOND_II("&b&lDiamond II", 1400, Material.DIAMOND),
    DIAMOND_III("&b&lDiamond III", 1425, Material.DIAMOND),
    DIAMOND_IV("&b&lDiamond IV", 1450, Material.DIAMOND),
    DIAMOND_V("&b&lDiamond V", 1475, Material.DIAMOND),

    EMERALD_I("&a&lEmerald I", 1500, Material.EMERALD),
    EMERALD_II("&a&lEmerald II", 1525, Material.EMERALD),
    EMERALD_III("&a&lEmerald III", 1550, Material.EMERALD),
    EMERALD_IV("&a&lEmerald IV", 1575, Material.EMERALD),
    EMERALD_V("&a&lEmerald V", 1600, Material.EMERALD),

    MASTER_I("&2&lMaster I", 1625, Material.NETHER_STAR),
    MASTER_II("&2&lMaster II", 1650, Material.NETHER_STAR),
    MASTER_III("&2&lMaster III", 1675, Material.NETHER_STAR),
    MASTER_IV("&2&lMaster IV", 1700, Material.NETHER_STAR),
    MASTER_V("&2&lMaster V", 1750, Material.NETHER_STAR),

    GRANDMASTER("&5&lGrand Master", 1800, Material.BEACON),
    GRANDMASTER_I("&5&lGrand Master I", 2000, Material.BEACON),

    LEGEND("&A&lLegend", 2300, Material.DRAGON_EGG),
    SUPER_LEGEND("&A&lSuper Legend", 3000, Material.DRAGON_EGG);

    private final String prettyName;
    private final int minimumElo;
    private final Material material;
}