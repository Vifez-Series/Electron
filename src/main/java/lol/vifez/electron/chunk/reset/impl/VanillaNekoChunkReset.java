package lol.vifez.electron.chunk.reset.impl;

import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.NibbleArray;
import lol.vifez.electron.chunk.reset.INekoChunkReset;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class VanillaNekoChunkReset implements INekoChunkReset {
    @SneakyThrows
    public void setSections(Chunk nmsChunk, ChunkSection[] sections) {
        setField("sections", nmsChunk, sections);

        // NMS way to refresh chunk for players
        net.minecraft.server.v1_8_R3.WorldServer worldServer = (net.minecraft.server.v1_8_R3.WorldServer) nmsChunk.getWorld();
        worldServer.getPlayerChunkMap().flagDirty(new net.minecraft.server.v1_8_R3.BlockPosition(nmsChunk.locX << 4, 0, nmsChunk.locZ << 4));
    }

    public ChunkSection[] cloneSections(ChunkSection[] sections) {
        ChunkSection[] newSections = new ChunkSection[sections.length];

        for (int i = 0; i < sections.length; ++i) {
            if (sections[i] != null) {
                newSections[i] = cloneSection(sections[i]);
            }
        }

        return newSections;
    }

    @SneakyThrows
    public ChunkSection cloneSection(ChunkSection chunkSection) {
        ChunkSection section = new ChunkSection(chunkSection.getYPosition(), chunkSection.getSkyLightArray() != null);

        setField("nonEmptyBlockCount", section, getFromField("nonEmptyBlockCount", chunkSection));
        setField("tickingBlockCount", section, getFromField("tickingBlockCount", chunkSection));
        setField("blockIds", section, chunkSection.getIdArray().clone());
        if (chunkSection.getEmittedLightArray() != null) {
            section.a(cloneNibbleArray(chunkSection.getEmittedLightArray()));
        }

        if (chunkSection.getSkyLightArray() != null) {
            section.b(cloneNibbleArray(chunkSection.getSkyLightArray()));
        }

        return section;
    }

    public NibbleArray cloneNibbleArray(NibbleArray nibbleArray) {
        return new NibbleArray(nibbleArray.a().clone());
    }

    @SneakyThrows
    public void setField(String fieldName, Object clazz, Object value) {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(clazz, value);
    }

    public Object getFromField(String fieldName, Object clazz) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(clazz);
    }
}
