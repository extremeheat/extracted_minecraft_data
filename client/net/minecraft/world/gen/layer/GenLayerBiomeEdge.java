package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;

public class GenLayerBiomeEdge extends GenLayer {
   public GenLayerBiomeEdge(long var1, GenLayer var3) {
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
            if (!this.func_151636_a(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_76770_e.field_76756_M, BiomeGenBase.field_76783_v.field_76756_M) && !this.func_151635_b(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_150607_aa.field_76756_M, BiomeGenBase.field_150589_Z.field_76756_M) && !this.func_151635_b(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_150608_ab.field_76756_M, BiomeGenBase.field_150589_Z.field_76756_M) && !this.func_151635_b(var5, var6, var8, var7, var3, var9, BiomeGenBase.field_150578_U.field_76756_M, BiomeGenBase.field_76768_g.field_76756_M)) {
               int var10;
               int var11;
               int var12;
               int var13;
               if (var9 == BiomeGenBase.field_76769_d.field_76756_M) {
                  var10 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
                  var11 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
                  var12 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
                  var13 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
                  if (var10 != BiomeGenBase.field_76774_n.field_76756_M && var11 != BiomeGenBase.field_76774_n.field_76756_M && var12 != BiomeGenBase.field_76774_n.field_76756_M && var13 != BiomeGenBase.field_76774_n.field_76756_M) {
                     var6[var8 + var7 * var3] = var9;
                  } else {
                     var6[var8 + var7 * var3] = BiomeGenBase.field_150580_W.field_76756_M;
                  }
               } else if (var9 == BiomeGenBase.field_76780_h.field_76756_M) {
                  var10 = var5[var8 + 1 + (var7 + 1 - 1) * (var3 + 2)];
                  var11 = var5[var8 + 1 + 1 + (var7 + 1) * (var3 + 2)];
                  var12 = var5[var8 + 1 - 1 + (var7 + 1) * (var3 + 2)];
                  var13 = var5[var8 + 1 + (var7 + 1 + 1) * (var3 + 2)];
                  if (var10 != BiomeGenBase.field_76769_d.field_76756_M && var11 != BiomeGenBase.field_76769_d.field_76756_M && var12 != BiomeGenBase.field_76769_d.field_76756_M && var13 != BiomeGenBase.field_76769_d.field_76756_M && var10 != BiomeGenBase.field_150584_S.field_76756_M && var11 != BiomeGenBase.field_150584_S.field_76756_M && var12 != BiomeGenBase.field_150584_S.field_76756_M && var13 != BiomeGenBase.field_150584_S.field_76756_M && var10 != BiomeGenBase.field_76774_n.field_76756_M && var11 != BiomeGenBase.field_76774_n.field_76756_M && var12 != BiomeGenBase.field_76774_n.field_76756_M && var13 != BiomeGenBase.field_76774_n.field_76756_M) {
                     if (var10 != BiomeGenBase.field_76782_w.field_76756_M && var13 != BiomeGenBase.field_76782_w.field_76756_M && var11 != BiomeGenBase.field_76782_w.field_76756_M && var12 != BiomeGenBase.field_76782_w.field_76756_M) {
                        var6[var8 + var7 * var3] = var9;
                     } else {
                        var6[var8 + var7 * var3] = BiomeGenBase.field_150574_L.field_76756_M;
                     }
                  } else {
                     var6[var8 + var7 * var3] = BiomeGenBase.field_76772_c.field_76756_M;
                  }
               } else {
                  var6[var8 + var7 * var3] = var9;
               }
            }
         }
      }

      return var6;
   }

   private boolean func_151636_a(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (!func_151616_a(var6, var7)) {
         return false;
      } else {
         int var9 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
         int var10 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
         int var11 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
         int var12 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
         if (this.func_151634_b(var9, var7) && this.func_151634_b(var10, var7) && this.func_151634_b(var11, var7) && this.func_151634_b(var12, var7)) {
            var2[var3 + var4 * var5] = var6;
         } else {
            var2[var3 + var4 * var5] = var8;
         }

         return true;
      }
   }

   private boolean func_151635_b(int[] var1, int[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var6 != var7) {
         return false;
      } else {
         int var9 = var1[var3 + 1 + (var4 + 1 - 1) * (var5 + 2)];
         int var10 = var1[var3 + 1 + 1 + (var4 + 1) * (var5 + 2)];
         int var11 = var1[var3 + 1 - 1 + (var4 + 1) * (var5 + 2)];
         int var12 = var1[var3 + 1 + (var4 + 1 + 1) * (var5 + 2)];
         if (func_151616_a(var9, var7) && func_151616_a(var10, var7) && func_151616_a(var11, var7) && func_151616_a(var12, var7)) {
            var2[var3 + var4 * var5] = var6;
         } else {
            var2[var3 + var4 * var5] = var8;
         }

         return true;
      }
   }

   private boolean func_151634_b(int var1, int var2) {
      if (func_151616_a(var1, var2)) {
         return true;
      } else {
         BiomeGenBase var3 = BiomeGenBase.func_150568_d(var1);
         BiomeGenBase var4 = BiomeGenBase.func_150568_d(var2);
         if (var3 != null && var4 != null) {
            BiomeGenBase.TempCategory var5 = var3.func_150561_m();
            BiomeGenBase.TempCategory var6 = var4.func_150561_m();
            return var5 == var6 || var5 == BiomeGenBase.TempCategory.MEDIUM || var6 == BiomeGenBase.TempCategory.MEDIUM;
         } else {
            return false;
         }
      }
   }
}
