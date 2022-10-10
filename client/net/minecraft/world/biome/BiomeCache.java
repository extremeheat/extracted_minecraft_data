package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeCache {
   private final BiomeProvider field_76844_a;
   private final LoadingCache<ChunkPos, BiomeCache.Entry> field_76843_c;

   public BiomeCache(BiomeProvider var1) {
      super();
      this.field_76843_c = CacheBuilder.newBuilder().expireAfterAccess(30000L, TimeUnit.MILLISECONDS).build(new CacheLoader<ChunkPos, BiomeCache.Entry>() {
         public BiomeCache.Entry load(ChunkPos var1) throws Exception {
            return BiomeCache.this.new Entry(var1.field_77276_a, var1.field_77275_b);
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((ChunkPos)var1);
         }
      });
      this.field_76844_a = var1;
   }

   public BiomeCache.Entry func_76840_a(int var1, int var2) {
      var1 >>= 4;
      var2 >>= 4;
      return (BiomeCache.Entry)this.field_76843_c.getUnchecked(new ChunkPos(var1, var2));
   }

   public Biome func_180284_a(int var1, int var2, Biome var3) {
      Biome var4 = this.func_76840_a(var1, var2).func_76885_a(var1, var2);
      return var4 == null ? var3 : var4;
   }

   public void func_76838_a() {
   }

   public Biome[] func_76839_e(int var1, int var2) {
      return this.func_76840_a(var1, var2).field_76891_c;
   }

   public class Entry {
      private final Biome[] field_76891_c;

      public Entry(int var2, int var3) {
         super();
         this.field_76891_c = BiomeCache.this.field_76844_a.func_201537_a(var2 << 4, var3 << 4, 16, 16, false);
      }

      public Biome func_76885_a(int var1, int var2) {
         return this.field_76891_c[var1 & 15 | (var2 & 15) << 4];
      }
   }
}
