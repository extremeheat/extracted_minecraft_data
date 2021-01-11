package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
   private static final Logger field_151629_c = LogManager.getLogger();
   private GenLayer field_151628_d;

   public GenLayerHills(long var1, GenLayer var3, GenLayer var4) {
      super(var1);
      this.field_75909_a = var3;
      this.field_151628_d = var4;
   }

   public int[] func_75904_a(int var1, int var2, int var3, int var4) {
      int[] var5 = this.field_75909_a.func_75904_a(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var6 = this.field_151628_d.func_75904_a(var1 - 1, var2 - 1, var3 + 2, var4 + 2);
      int[] var7 = IntCache.func_76445_a(var3 * var4);

      for(int var8 = 0; var8 < var4; ++var8) {
         for(int var9 = 0; var9 < var3; ++var9) {
            this.func_75903_a((long)(var9 + var1), (long)(var8 + var2));
            int var10 = var5[var9 + 1 + (var8 + 1) * (var3 + 2)];
            int var11 = var6[var9 + 1 + (var8 + 1) * (var3 + 2)];
            boolean var12 = (var11 - 2) % 29 == 0;
            if (var10 > 255) {
               field_151629_c.debug("old! " + var10);
            }

            if (var10 != 0 && var11 >= 2 && (var11 - 2) % 29 == 1 && var10 < 128) {
               if (BiomeGenBase.func_150568_d(var10 + 128) != null) {
                  var7[var9 + var8 * var3] = var10 + 128;
               } else {
                  var7[var9 + var8 * var3] = var10;
               }
            } else if (this.func_75902_a(3) != 0 && !var12) {
               var7[var9 + var8 * var3] = var10;
            } else {
               int var13 = var10;
               int var14;
               if (var10 == BiomeGenBase.field_76769_d.field_76756_M) {
                  var13 = BiomeGenBase.field_76786_s.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76767_f.field_76756_M) {
                  var13 = BiomeGenBase.field_76785_t.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150583_P.field_76756_M) {
                  var13 = BiomeGenBase.field_150582_Q.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150585_R.field_76756_M) {
                  var13 = BiomeGenBase.field_76772_c.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76768_g.field_76756_M) {
                  var13 = BiomeGenBase.field_76784_u.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150578_U.field_76756_M) {
                  var13 = BiomeGenBase.field_150581_V.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150584_S.field_76756_M) {
                  var13 = BiomeGenBase.field_150579_T.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76772_c.field_76756_M) {
                  if (this.func_75902_a(3) == 0) {
                     var13 = BiomeGenBase.field_76785_t.field_76756_M;
                  } else {
                     var13 = BiomeGenBase.field_76767_f.field_76756_M;
                  }
               } else if (var10 == BiomeGenBase.field_76774_n.field_76756_M) {
                  var13 = BiomeGenBase.field_76775_o.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76782_w.field_76756_M) {
                  var13 = BiomeGenBase.field_76792_x.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76771_b.field_76756_M) {
                  var13 = BiomeGenBase.field_150575_M.field_76756_M;
               } else if (var10 == BiomeGenBase.field_76770_e.field_76756_M) {
                  var13 = BiomeGenBase.field_150580_W.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150588_X.field_76756_M) {
                  var13 = BiomeGenBase.field_150587_Y.field_76756_M;
               } else if (func_151616_a(var10, BiomeGenBase.field_150607_aa.field_76756_M)) {
                  var13 = BiomeGenBase.field_150589_Z.field_76756_M;
               } else if (var10 == BiomeGenBase.field_150575_M.field_76756_M && this.func_75902_a(3) == 0) {
                  var14 = this.func_75902_a(2);
                  if (var14 == 0) {
                     var13 = BiomeGenBase.field_76772_c.field_76756_M;
                  } else {
                     var13 = BiomeGenBase.field_76767_f.field_76756_M;
                  }
               }

               if (var12 && var13 != var10) {
                  if (BiomeGenBase.func_150568_d(var13 + 128) != null) {
                     var13 += 128;
                  } else {
                     var13 = var10;
                  }
               }

               if (var13 == var10) {
                  var7[var9 + var8 * var3] = var10;
               } else {
                  var14 = var5[var9 + 1 + (var8 + 1 - 1) * (var3 + 2)];
                  int var15 = var5[var9 + 1 + 1 + (var8 + 1) * (var3 + 2)];
                  int var16 = var5[var9 + 1 - 1 + (var8 + 1) * (var3 + 2)];
                  int var17 = var5[var9 + 1 + (var8 + 1 + 1) * (var3 + 2)];
                  int var18 = 0;
                  if (func_151616_a(var14, var10)) {
                     ++var18;
                  }

                  if (func_151616_a(var15, var10)) {
                     ++var18;
                  }

                  if (func_151616_a(var16, var10)) {
                     ++var18;
                  }

                  if (func_151616_a(var17, var10)) {
                     ++var18;
                  }

                  if (var18 >= 3) {
                     var7[var9 + var8 * var3] = var13;
                  } else {
                     var7[var9 + var8 * var3] = var10;
                  }
               }
            }
         }
      }

      return var7;
   }
}
