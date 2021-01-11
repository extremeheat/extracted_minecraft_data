package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
   private static final Logger field_147436_a = LogManager.getLogger();
   private Chunk field_73238_a;
   private LongHashMap<Chunk> field_73236_b = new LongHashMap();
   private List<Chunk> field_73237_c = Lists.newArrayList();
   private World field_73235_d;

   public ChunkProviderClient(World var1) {
      super();
      this.field_73238_a = new EmptyChunk(var1, 0, 0);
      this.field_73235_d = var1;
   }

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

   public Chunk func_73158_c(int var1, int var2) {
      Chunk var3 = new Chunk(this.field_73235_d, var1, var2);
      this.field_73236_b.func_76163_a(ChunkCoordIntPair.func_77272_a(var1, var2), var3);
      this.field_73237_c.add(var3);
      var3.func_177417_c(true);
      return var3;
   }

   public Chunk func_73154_d(int var1, int var2) {
      Chunk var3 = (Chunk)this.field_73236_b.func_76164_a(ChunkCoordIntPair.func_77272_a(var1, var2));
      return var3 == null ? this.field_73238_a : var3;
   }

   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   public void func_104112_b() {
   }

   public boolean func_73156_b() {
      long var1 = System.currentTimeMillis();
      Iterator var3 = this.field_73237_c.iterator();

      while(var3.hasNext()) {
         Chunk var4 = (Chunk)var3.next();
         var4.func_150804_b(System.currentTimeMillis() - var1 > 5L);
      }

      if (System.currentTimeMillis() - var1 > 100L) {
         field_147436_a.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - var1});
      }

      return false;
   }

   public boolean func_73157_c() {
      return false;
   }

   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   public String func_73148_d() {
      return "MultiplayerChunkCache: " + this.field_73236_b.func_76162_a() + ", " + this.field_73237_c.size();
   }

   public List<BiomeGenBase.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      return null;
   }

   public BlockPos func_180513_a(World var1, String var2, BlockPos var3) {
      return null;
   }

   public int func_73152_e() {
      return this.field_73237_c.size();
   }

   public void func_180514_a(Chunk var1, int var2, int var3) {
   }

   public Chunk func_177459_a(BlockPos var1) {
      return this.func_73154_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }
}
