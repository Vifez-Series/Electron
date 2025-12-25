package lol.vifez.electron.chunk;

import lol.vifez.electron.chunk.reset.INekoChunkReset;
import lol.vifez.electron.chunk.reset.impl.VanillaNekoChunkReset;
import lol.vifez.electron.chunk.restoration.IChunkRestoration;
import lol.vifez.electron.chunk.restoration.impl.NMSChunkRestoration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ChunkRestorationManager {

    @Getter @Setter(AccessLevel.PRIVATE) private static INekoChunkReset iNekoChunkReset;
    @Getter @Setter(AccessLevel.PRIVATE) private static IChunkRestoration iChunkRestoration;

    public ChunkRestorationManager() {
        if (iNekoChunkReset == null) {
            setINekoChunkReset(new VanillaNekoChunkReset());
        }

        if (iChunkRestoration == null) {
            setIChunkRestoration(new NMSChunkRestoration());
        }
    }
}
