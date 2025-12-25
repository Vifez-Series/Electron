package lol.vifez.electron.chunk.restoration;

import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.util.Cuboid;

public interface IChunkRestoration {

    void copy(Arena arena);

    void reset(Arena arena);

    void copy(Cuboid cuboid);

    void reset(Cuboid cuboid);

    void reset(Arena arena, java.util.Set<org.bukkit.Chunk> chunks);
}
