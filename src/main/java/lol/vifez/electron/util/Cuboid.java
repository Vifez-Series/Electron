package lol.vifez.electron.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class Cuboid implements Iterable<Location> {

    private String worldName;
    private int x1, y1, z1;
    private int x2, y2, z2;

    public Cuboid(Location l1, Location l2) {
        this(l1.getWorld().getName(),
                l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(),
                l2.getBlockX(), l2.getBlockY(), l2.getBlockZ()
        );

    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(world.getName(), x1, y1, z1, x2, y2, z2);
    }

    public Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Location getLowerCorner() {
        return new Location(getWorld(), x1, y1, z1);
    }

    public Location getUpperCorner() {
        return new Location(getWorld(), x2, y2, z2);
    }

    public Location getCenter() {
        return new Location(
                getWorld(), getLowerX() + (getUpperX() - getLowerX()) / 2,
                getLowerY() + (getUpperY() - getLowerY()) / 2, getLowerZ() + (getUpperZ() - getLowerZ()) / 2
        );
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalStateException("world '" + worldName + "' is not loaded");
        }
        return world;
    }

    public int getSizeX() {
        return (x2 - x1) + 1;
    }

    public int getSizeY() {
        return (y2 - y1) + 1;
    }

    public int getSizeZ() {
        return (z2 - z1) + 1;
    }

    public int getLowerX() {
        return x1;
    }

    public int getLowerY() {
        return y1;
    }

    public int getLowerZ() {
        return z1;
    }

    public int getUpperX() {
        return x2;
    }

    public int getUpperY() {
        return y2;
    }

    public int getUpperZ() {
        return z2;
    }

    public boolean contains(int x, int y, int z) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public boolean contains(Location location) {
        if(location == null || this.worldName == null) return false;
        World world = location.getWorld();
        return world != null && this.worldName.equals(location.getWorld().getName()) && this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        World w = getWorld();
        int x1 = getLowerX() & ~0xf;
        int x2 = getUpperX() & ~0xf;
        int z1 = getLowerZ() & ~0xf;
        int z2 = getUpperZ() & ~0xf;
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                chunks.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }
        return chunks;
    }

    public Iterator<Location> iterator() {
        return new LocationCuboidIterator(getWorld(), x1, y1, z1, x2, y2, z2);
    }

    public class LocationCuboidIterator implements Iterator<Location> {
        private World w;
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;

        public LocationCuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            baseX = x1;
            baseY = y1;
            baseZ = z1;
            sizeX = Math.abs(x2 - x1) + 1;
            sizeY = Math.abs(y2 - y1) + 1;
            sizeZ = Math.abs(z2 - z1) + 1;
            x = y = z = 0;
        }

        public boolean hasNext() {
            return x < sizeX && y < sizeY && z < sizeZ;
        }

        public Location next() {
            Location b = new Location(w, baseX + x, baseY + y, baseZ + z);
            if (++x >= sizeX) {
                x = 0;
                if (++y >= sizeY) {
                    y = 0;
                    ++z;
                }
            }
            return b;
        }

        public void remove() {}
    }
}
