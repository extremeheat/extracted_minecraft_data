package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
   private static final Logger field_147436_a = LogManager.getLogger();
   private Chunk field_73238_a;
   private LongHashMap field_73236_b = new LongHashMap();
   private List field_73237_c = new ArrayList();
   private World field_73235_d;

   public ChunkProviderClient(World var1) {
      super();
      this.field_73238_a = new EmptyChunk(var1, 0, 0);
      this.field_73235_d = var1;
   }

   @Override
   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   public void func_73234_b(int var1, int var2) {
      Chunk var3 = this.func_73154_d(var1, var2);
      if (!var3.func_76621_g()) {
         var3.func_76623_d();
      }

      this.field_73236_b.func_76159_d(ChunkCoordIntPair.func_77272_a(var1, var2));
      this.field_73237_c.remove(var3);
   }

   @Override
   public Chunk func_73158_c(int var1, int var2) {
      Chunk var3 = new Chunk(this.field_73235_d, var1, var2);
      this.field_73236_b.func_76163_a(ChunkCoordIntPair.func_77272_a(var1, var2), var3);
      this.field_73237_c.add(var3);
      var3.field_76636_d = true;
      return var3;
   }

   @Override
   public Chunk func_73154_d(int var1, int var2) {
      Chunk var3 = (Chunk)this.field_73236_b.func_76164_a(ChunkCoordIntPair.func_77272_a(var1, var2));
      return var3 == null ? this.field_73238_a : var3;
   }

   @Override
   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   @Override
   public void func_104112_b() {
   }

   @Override
   public boolean func_73156_b() {
      long var1 = System.currentTimeMillis();

      for(Chunk var4 : this.field_73237_c) {
         var4.func_150804_b(System.currentTimeMillis() - var1 > 5L);
      }

      if (System.currentTimeMillis() - var1 > 100L) {
         field_147436_a.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - var1});
      }

      return false;
   }

   @Override
   public boolean func_73157_c() {
      return false;
   }

   @Override
   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
   }

   @Override
   public String func_73148_d() {
      return "MultiplayerChunkCache: " + this.field_73236_b.func_76162_a() + ", " + this.field_73237_c.size();
   }

   @Override
   public List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5) {
      return null;
   }

   @Override
   public int func_73152_e() {
      return this.field_73237_c.size();
   }

   @Override
   public void func_82695_e(int var1, int var2) {
   }
}
