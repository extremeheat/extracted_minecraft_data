package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerRiverMix extends GenLayer {
   private GenLayer field_75910_b;
   private GenLayer field_75911_c;

   public GenLayerRiverMix(long var1, GenLayer var3, GenLayer var4) {
      super(var1);
      this.field_75910_b = var3;
      this.field_75911_c = var4;
   }

   public void func_75905_a(long var1) {
      this.field_75910_b.func_75905_a(var1);
      this.field_75911_c.func_75905_a(var1);
      super.func_75905_a(var1);
   }

   public int[] func_75904_a(int var1, int var2, int var3, int var4) {
      int[] var5 = this.field_75910_b.func_75904_a(var1, var2, var3, var4);
      int[] var6 = this.field_75911_c.func_75904_a(var1, var2, var3, var4);
      int[] var7 = IntCache.func_76445_a(var3 * var4);

      for(int var8 = 0; var8 < var3 * var4; ++var8) {
         if (var5[var8] != BiomeGenBase.field_76771_b.field_76756_M && var5[var8] != BiomeGenBase.field_150575_M.field_76756_M) {
            if (var6[var8] == BiomeGenBase.field_76781_i.field_76756_M) {
               if (var5[var8] == BiomeGenBase.field_76774_n.field_76756_M) {
                  var7[var8] = BiomeGenBase.field_76777_m.field_76756_M;
               } else if (var5[var8] != BiomeGenBase.field_76789_p.field_76756_M && var5[var8] != BiomeGenBase.field_76788_q.field_76756_M) {
                  var7[var8] = var6[var8] & 255;
               } else {
                  var7[var8] = BiomeGenBase.field_76788_q.field_76756_M;
               }
            } else {
               var7[var8] = var5[var8];
            }
         } else {
            var7[var8] = var5[var8];
         }
      }

      return var7;
   }
}
