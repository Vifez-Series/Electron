package lol.vifez.electron.chunk.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.ChunkSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class NekoChunkData {

	public Map<NekoChunk, ChunkSection[]> chunks = new ConcurrentHashMap<>();

	public ChunkSection[] getNyaChunk(int x, int z) {
		return chunks.get(new NekoChunk(x, z));
	}
}
