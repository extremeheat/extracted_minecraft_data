package net.minecraft.world.chunk.storage;

import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.storage.SessionLockException;

public interface IChunkLoader {
   @Nullable
   Chunk func_199813_a(IWorld var1, int var2, int var3, Consumer<Chunk> var4) throws IOException;

   @Nullable
   ChunkPrimer func_202152_b(IWorld var1, int var2, int var3, Consumer<IChunk> var4) throws IOException;

   void func_75816_a(World var1, IChunk var2) throws IOException, SessionLockException;

   void func_75818_b();
}
