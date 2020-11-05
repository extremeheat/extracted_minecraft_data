package net.minecraft.world.level.newbiome.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public final class LazyArea implements Area {
   private final PixelTransformer transformer;
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCache;

   public LazyArea(Long2IntLinkedOpenHashMap var1, int var2, PixelTransformer var3) {
      super();
      this.cache = var1;
      this.maxCache = var2;
      this.transformer = var3;
   }

   public int get(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      synchronized(this.cache) {
         int var6 = this.cache.get(var3);
         if (var6 != -2147483648) {
            return var6;
         } else {
            int var7 = this.transformer.apply(var1, var2);
            this.cache.put(var3, var7);
            if (this.cache.size() > this.maxCache) {
               for(int var8 = 0; var8 < this.maxCache / 16; ++var8) {
                  this.cache.removeFirstInt();
               }
            }

            return var7;
         }
      }
   }

   public int getMaxCache() {
      return this.maxCache;
   }
}
