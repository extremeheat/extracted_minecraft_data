package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {
   private final WorldChunkManager field_76844_a;
   private long field_76842_b;
   private LongHashMap field_76843_c = new LongHashMap();
   private List field_76841_d = new ArrayList();

   public BiomeCache(WorldChunkManager var1) {
      super();
      this.field_76844_a = var1;
   }

   public BiomeCache$Block func_76840_a(int var1, int var2) {
      var1 >>= 4;
      var2 >>= 4;
      long var3 = (long)var1 & 4294967295L | ((long)var2 & 4294967295L) << 32;
      BiomeCache$Block var5 = (BiomeCache$Block)this.field_76843_c.func_76164_a(var3);
      if (var5 == null) {
         var5 = new BiomeCache$Block(this, var1, var2);
         this.field_76843_c.func_76163_a(var3, var5);
         this.field_76841_d.add(var5);
      }

      var5.field_76886_f = MinecraftServer.func_130071_aq();
      return var5;
   }

   public BiomeGenBase func_76837_b(int var1, int var2) {
      return this.func_76840_a(var1, var2).func_76885_a(var1, var2);
   }

   public void func_76838_a() {
      long var1 = MinecraftServer.func_130071_aq();
      long var3 = var1 - this.field_76842_b;
      if (var3 > 7500L || var3 < 0L) {
         this.field_76842_b = var1;

         for(int var5 = 0; var5 < this.field_76841_d.size(); ++var5) {
            BiomeCache$Block var6 = (BiomeCache$Block)this.field_76841_d.get(var5);
            long var7 = var1 - var6.field_76886_f;
            if (var7 > 30000L || var7 < 0L) {
               this.field_76841_d.remove(var5--);
               long var9 = (long)var6.field_76888_d & 4294967295L | ((long)var6.field_76889_e & 4294967295L) << 32;
               this.field_76843_c.func_76159_d(var9);
            }
         }
      }
   }

   public BiomeGenBase[] func_76839_e(int var1, int var2) {
      return this.func_76840_a(var1, var2).field_76891_c;
   }
}
