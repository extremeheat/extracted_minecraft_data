package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface IChunkProvider {
   boolean func_73149_a(int var1, int var2);

   Chunk func_73154_d(int var1, int var2);

   Chunk func_73158_c(int var1, int var2);

   void func_73153_a(IChunkProvider var1, int var2, int var3);

   boolean func_73151_a(boolean var1, IProgressUpdate var2);

   boolean func_73156_b();

   boolean func_73157_c();

   String func_73148_d();

   List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4);

   ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5);

   int func_73152_e();

   void func_82695_e(int var1, int var2);

   void func_104112_b();
}
