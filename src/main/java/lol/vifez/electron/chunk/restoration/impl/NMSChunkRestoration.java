package lol.vifez.electron.chunk.restoration.impl;

import lol.vifez.electron.Practice;
import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.chunk.ChunkRestorationManager;
import lol.vifez.electron.chunk.data.NekoChunk;
import lol.vifez.electron.chunk.data.NekoChunkData;
import lol.vifez.electron.chunk.restoration.IChunkRestoration;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.Cuboid;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NMSChunkRestoration implements IChunkRestoration {

    private final Map<String, NekoChunkData> snapshots = new ConcurrentHashMap<>();

    @Override
    public void copy(Arena arena) {
        if (arena.getPositionOne() == null || arena.getPositionTwo() == null) return;
        
        Cuboid cuboid = new Cuboid(arena.getPositionOne(), arena.getPositionTwo());
        NekoChunkData data = new NekoChunkData();

        for (org.bukkit.Chunk bukkitChunk : cuboid.getChunks()) {
            Chunk nmsChunk = ((CraftChunk) bukkitChunk).getHandle();
            ChunkSection[] sections = ChunkRestorationManager.getINekoChunkReset().cloneSections(nmsChunk.getSections());
            data.getChunks().put(new NekoChunk(nmsChunk.locX, nmsChunk.locZ), sections);
        }

        snapshots.put(arena.getName(), data);
        Bukkit.getConsoleSender().sendMessage(CC.translate("&a[ChunkReset] Captured snapshot for arena: " + arena.getName() + " (" + data.getChunks().size() + " chunks)"));
    }

    @Override
    public void reset(Arena arena) {
        NekoChunkData data = snapshots.get(arena.getName());
        if (data == null) return;

        for (Map.Entry<NekoChunk, ChunkSection[]> entry : data.getChunks().entrySet()) {
            NekoChunk nekoChunk = entry.getKey();
            ChunkSection[] sections = entry.getValue();

            World world = arena.getPositionOne().getWorld();
            org.bukkit.Chunk bukkitChunk = world.getChunkAt(nekoChunk.getX(), nekoChunk.getZ());
            Chunk nmsChunk = ((CraftChunk) bukkitChunk).getHandle();

            ChunkRestorationManager.getINekoChunkReset().setSections(nmsChunk, ChunkRestorationManager.getINekoChunkReset().cloneSections(sections));
        }
        Bukkit.getConsoleSender().sendMessage(CC.translate("&a[ChunkReset] Restored arena: " + arena.getName()));
    }

    @Override
    public void copy(Cuboid cuboid) {
        // Not needed for basic arena reset, but implemented for completeness
        NekoChunkData data = new NekoChunkData();
        for (org.bukkit.Chunk bukkitChunk : cuboid.getChunks()) {
            Chunk nmsChunk = ((CraftChunk) bukkitChunk).getHandle();
            ChunkSection[] sections = ChunkRestorationManager.getINekoChunkReset().cloneSections(nmsChunk.getSections());
            data.getChunks().put(new NekoChunk(nmsChunk.locX, nmsChunk.locZ), sections);
        }
        // We would need a way to store these, but for now we focus on arenas
    }

    @Override
    public void reset(Cuboid cuboid) {
        // Not needed for basic arena reset
    }

    @Override
    public void reset(Arena arena, Set<org.bukkit.Chunk> chunks) {
        reset(arena);
    }
}
