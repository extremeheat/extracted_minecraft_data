package net.minecraft.client.multiplayer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
   private static final Logger field_147436_a = LogManager.getLogger();
   private final Chunk field_73238_a;
   private final Long2ObjectMap<Chunk> field_73236_b = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<Chunk>(8192) {
      protected void rehash(int var1) {
         if (var1 > this.key.length) {
            super.rehash(var1);
         }

      }
   });
   private final World field_73235_d;

   public ChunkProviderClient(World var1) {
      super();
      this.field_73238_a = new EmptyChunk(var1, 0, 0);
      this.field_73235_d = var1;
   }

   public void func_73234_b(int var1, int var2) {
      Chunk var3 = (Chunk)this.field_73236_b.remove(ChunkPos.func_77272_a(var1, var2));
      if (var3 != null) {
         var3.func_76623_d();
      }

   }

   @Nullable
   public Chunk func_186025_d(int var1, int var2, boolean var3, boolean var4) {
      Chunk var5 = (Chunk)this.field_73236_b.get(ChunkPos.func_77272_a(var1, var2));
      return var4 && var5 == null ? this.field_73238_a : var5;
   }

   public Chunk func_212474_a(int var1, int var2, PacketBuffer var3, int var4, boolean var5) {
      synchronized(this.field_73236_b) {
         long var7 = ChunkPos.func_77272_a(var1, var2);
         Chunk var9 = (Chunk)this.field_73236_b.computeIfAbsent(var7, (var3x) -> {
            return new Chunk(this.field_73235_d, var1, var2, new Biome[256]);
         });
         var9.func_186033_a(var3, var4, var5);
         var9.func_177417_c(true);
         return var9;
      }
   }

   public boolean func_73156_b(BooleanSupplier var1) {
      long var2 = Util.func_211177_b();
      ObjectIterator var4 = this.field_73236_b.values().iterator();

      while(var4.hasNext()) {
         Chunk var5 = (Chunk)var4.next();
         var5.func_150804_b(Util.func_211177_b() - var2 > 5L);
      }

      if (Util.func_211177_b() - var2 > 100L) {
         field_147436_a.info("Warning: Clientside chunk ticking took {} ms", Util.func_211177_b() - var2);
      }

      return false;
   }

   public String func_73148_d() {
      return "MultiplayerChunkCache: " + this.field_73236_b.size() + ", " + this.field_73236_b.size();
   }

   public IChunkGenerator<?> func_201711_g() {
      return null;
   }
}
