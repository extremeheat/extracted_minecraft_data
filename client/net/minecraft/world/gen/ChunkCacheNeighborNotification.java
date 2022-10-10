package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkCacheNeighborNotification extends Long2ObjectOpenHashMap<Chunk> {
   private static final Logger field_202835_a = LogManager.getLogger();

   public ChunkCacheNeighborNotification(int var1) {
      super(var1);
   }

   public Chunk put(long var1, Chunk var3) {
      Chunk var4 = (Chunk)super.put(var1, var3);
      ChunkPos var5 = new ChunkPos(var1);

      for(int var6 = var5.field_77276_a - 1; var6 <= var5.field_77276_a + 1; ++var6) {
         for(int var7 = var5.field_77275_b - 1; var7 <= var5.field_77275_b + 1; ++var7) {
            if (var6 != var5.field_77276_a || var7 != var5.field_77275_b) {
               long var8 = ChunkPos.func_77272_a(var6, var7);
               Chunk var10 = (Chunk)this.get(var8);
               if (var10 != null) {
                  var3.func_201605_F();
                  var10.func_201605_F();
               }
            }
         }
      }

      return var4;
   }

   public Chunk put(Long var1, Chunk var2) {
      return this.put(var1, var2);
   }

   public Chunk remove(long var1) {
      Chunk var3 = (Chunk)super.remove(var1);
      ChunkPos var4 = new ChunkPos(var1);

      for(int var5 = var4.field_77276_a - 1; var5 <= var4.field_77276_a + 1; ++var5) {
         for(int var6 = var4.field_77275_b - 1; var6 <= var4.field_77275_b + 1; ++var6) {
            if (var5 != var4.field_77276_a || var6 != var4.field_77275_b) {
               Chunk var7 = (Chunk)this.get(ChunkPos.func_77272_a(var5, var6));
               if (var7 != null) {
                  var7.func_201611_G();
               }
            }
         }
      }

      return var3;
   }

   public Chunk remove(Object var1) {
      return this.remove((Long)var1);
   }

   public void putAll(Map<? extends Long, ? extends Chunk> var1) {
      throw new RuntimeException("Not yet implemented");
   }

   public boolean remove(Object var1, Object var2) {
      throw new RuntimeException("Not yet implemented");
   }

   // $FF: synthetic method
   public Object remove(long var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public Object put(long var1, Object var3) {
      return this.put(var1, (Chunk)var3);
   }

   // $FF: synthetic method
   public Object remove(Object var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public Object put(Long var1, Object var2) {
      return this.put(var1, (Chunk)var2);
   }

   // $FF: synthetic method
   public Object put(Object var1, Object var2) {
      return this.put((Long)var1, (Chunk)var2);
   }
}
