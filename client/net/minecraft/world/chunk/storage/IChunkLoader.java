package net.minecraft.world.chunk.storage;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {
   Chunk func_75815_a(World var1, int var2, int var3);

   void func_75816_a(World var1, Chunk var2);

   void func_75819_b(World var1, Chunk var2);

   void func_75817_a();

   void func_75818_b();
}
