package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.biome.BiomeGenMesa;

public class GenLayerShore extends GenLayer {
   public GenLayerShore(long var1, GenLayer var3) {
      super(var1);
      this.field_75909_a = var3;
   }

   public int[] func_75904_a(int var1, int var2, int var3, int var4) {
      int[] var5 = this.field_75909_a.func_75904_a(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var6 = IntCache.func_76445_a(var3 * var4);

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.func_75903_a((long)(var8 + var1), (long)(var7 + var2));
            int var9 = var5[var8 + 1 + (var7 + 1) * (var3 + 2)];
            BiomeGenBase var10 = BiomeGenBase.func_150568_d(var9);
            int var11;
            int var12;
            int var13;
            int var14;
            if (var9 == BiomeGenBase.field_76789_p.field_76756_M) {
               var11 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               var12 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               var13 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               var14 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (var11 != BiomeGenBase.field_76771_b.field_76756_M && var12 != BiomeGenBase.field_76771_b.field_76756_M && var13 != BiomeGenBase.field_76771_b.field_76756_M && var14 != BiomeGenBase.field_76771_b.field_76756_M) {
                  var6[var8 + var7 * var3] = var9;
               } else {
                  var6[var8 + var7 * var3] = BiomeGenBase.field_76788_q.field_76756_M;
               }
            } else if (var10 != null && var10.func_150562_l() == BiomeGenJungle.class) {
               var11 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
               var12 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
               var13 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
               var14 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
               if (this.func_151631_c(var11) && this.func_151631_c(var12) && this.func_151631_c(var13) && this.func_151631_c(var14)) {
                  if (!func_151618_b(var11) && !func_151618_b(var12) && !func_151618_b(var13) && !func_151618_b(var14)) {
                     var6[var8 + var7 * var3] = var9;
                  } else {
                     var6[var8 + var7 * var3] = BiomeGenBase.field_76787_r.field_76756_M;
                  }
               } else {
                  var6[var8 + var7 * var3] = BiomeGenBase.field_150574_L.field_76756_M;
               }
            } else if (var9 != BiomeGenBase.field_76770_e.field_76756_M && var9 != BiomeGenBase.field_150580_W.field_76756_M && var9 != BiomeGenBase.field_76783_v.field_76756_M) {
               if (var10 != null && var10.func_150559_j()) {
                  this.func_151632_a(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_150577_O.field_76756_M);
               } else if (var9 != BiomeGenBase.field_150589_Z.field_76756_M && var9 != BiomeGenBase.field_150607_aa.field_76756_M) {
                  if (var9 != BiomeGenBase.field_76771_b.field_76756_M && var9 != BiomeGenBase.field_150575_M.field_76756_M && var9 != BiomeGenBase.field_76781_i.field_76756_M && var9 != BiomeGenBase.field_76780_h.field_76756_M) {
                     var11 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
                     var12 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
                     var13 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
                     var14 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
                     if (!func_151618_b(var11) && !func_151618_b(var12) && !func_151618_b(var13) && !func_151618_b(var14)) {
                        var6[var8 + var7 * var3] = var9;
                     } else {
                        var6[var8 + var7 * var3] = BiomeGenBase.field_76787_r.field_76756_M;
                     }
                  } else {
                     var6[var8 + var7 * var3] = var9;
                  }
               } else {
                  var11 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
                  var12 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
                  var13 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
                  var14 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
                  if (!func_151618_b(var11) && !func_151618_b(var12) && !func_151618_b(var13) && !func_151618_b(var14)) {
                     if (this.func_151633_d(var11) && this.func_151633_d(var12) && this.func_151633_d(var13) && this.func_151633_d(var14)) {
                        var6[var8 + var7 * var3] = var9;
                     } else {
                        var6[var8 + var7 * var3] = BiomeGenBase.field_76769_d.field_76756_M;
                     }
                  } else {
                     var6[var8 + var7 * var3] = var9;
                  }
               }
            } else {
               this.func_151632_a(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_150576_N.field_76756_M);
            }
         }
      }

      return var6;
   }

   private void func_151632_a(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7) {
      if (func_151618_b(var6)) {
         var2[var3 + var4 * var5] = var6;
      } else {
         int var8 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
         int var9 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
         int var10 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
         int var11 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
         if (!func_151618_b(var8) && !func_151618_b(var9) && !func_151618_b(var10) && !func_151618_b(var11)) {
            var2[var3 + var4 * var5] = var6;
         } else {
            var2[var3 + var4 * var5] = var7;
         }

      }
   }

   private boolean func_151631_c(int var1) {
      if (BiomeGenBase.func_150568_d(var1) != null && BiomeGenBase.func_150568_d(var1).func_150562_l() == BiomeGenJungle.class) {
         return true;
      } else {
         return var1 == BiomeGenBase.field_150574_L.field_76756_M || var1 == BiomeGenBase.field_76782_w.field_76756_M || var1 == BiomeGenBase.field_76792_x.field_76756_M || var1 == BiomeGenBase.field_76767_f.field_76756_M || var1 == BiomeGenBase.field_76768_g.field_76756_M || func_151618_b(var1);
      }
   }

   private boolean func_151633_d(int var1) {
      return BiomeGenBase.func_150568_d(var1) instanceof BiomeGenMesa;
   }
}
