package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer {
   private final GenLayerEdge.Mode field_151627_c;

   public GenLayerEdge(long var1, GenLayer var3, GenLayerEdge.Mode var4) {
      super(var1);
      this.field_75909_a = var3;
      this.field_151627_c = var4;
   }

   public int[] func_75904_a(int var1, int var2, int var3, int var4) {
      switch(this.field_151627_c) {
      case COOL_WARM:
      default:
         return this.func_151626_c(var1, var2, var3, var4);
      case HEAT_ICE:
         return this.func_151624_d(var1, var2, var3, var4);
      case SPECIAL:
         return this.func_151625_e(var1, var2, var3, var4);
      }
   }

   private int[] func_151626_c(int var1, int var2, int var3, int var4) {
      int var5 = var1 - 1;
      int var6 = var2 - 1;
      int var7 = 1 + var3 + 1;
      int var8 = 1 + var4 + 1;
      int[] var9 = this.field_75909_a.func_75904_a(var5, var6, var7, var8);
      int[] var10 = IntCache.func_76445_a(var3 * var4);

      for(int var11 = 0; var11 < var4; ++var11) {
         for(int var12 = 0; var12 < var3; ++var12) {
            this.func_75903_a((long)(var12 + var1), (long)(var11 + var2));
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            if (var13 == 1) {
               int var14 = var9[var12 + 1 + (var11 + 1 - 1) * var7];
               int var15 = var9[var12 + 1 + 1 + (var11 + 1) * var7];
               int var16 = var9[var12 + 1 - 1 + (var11 + 1) * var7];
               int var17 = var9[var12 + 1 + (var11 + 1 + 1) * var7];
               boolean var18 = var14 == 3 || var15 == 3 || var16 == 3 || var17 == 3;
               boolean var19 = var14 == 4 || var15 == 4 || var16 == 4 || var17 == 4;
               if (var18 || var19) {
                  var13 = 2;
               }
            }

            var10[var12 + var11 * var3] = var13;
         }
      }

      return var10;
   }

   private int[] func_151624_d(int var1, int var2, int var3, int var4) {
      int var5 = var1 - 1;
      int var6 = var2 - 1;
      int var7 = 1 + var3 + 1;
      int var8 = 1 + var4 + 1;
      int[] var9 = this.field_75909_a.func_75904_a(var5, var6, var7, var8);
      int[] var10 = IntCache.func_76445_a(var3 * var4);

      for(int var11 = 0; var11 < var4; ++var11) {
         for(int var12 = 0; var12 < var3; ++var12) {
            int var13 = var9[var12 + 1 + (var11 + 1) * var7];
            if (var13 == 4) {
               int var14 = var9[var12 + 1 + (var11 + 1 - 1) * var7];
               int var15 = var9[var12 + 1 + 1 + (var11 + 1) * var7];
               int var16 = var9[var12 + 1 - 1 + (var11 + 1) * var7];
               int var17 = var9[var12 + 1 + (var11 + 1 + 1) * var7];
               boolean var18 = var14 == 2 || var15 == 2 || var16 == 2 || var17 == 2;
               boolean var19 = var14 == 1 || var15 == 1 || var16 == 1 || var17 == 1;
               if (var19 || var18) {
                  var13 = 3;
               }
            }

            var10[var12 + var11 * var3] = var13;
         }
      }

      return var10;
   }

   private int[] func_151625_e(int var1, int var2, int var3, int var4) {
      int[] var5 = this.field_75909_a.func_75904_a(var1, var2, var3, var4);
      int[] var6 = IntCache.func_76445_a(var3 * var4);

      for(int var7 = 0; var7 < var4; ++var7) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.func_75903_a((long)(var8 + var1), (long)(var7 + var2));
            int var9 = var5[var8 + var7 * var3];
            if (var9 != 0 && this.func_75902_a(13) == 0) {
               var9 |= 1 + this.func_75902_a(15) << 8 & 3840;
            }

            var6[var8 + var7 * var3] = var9;
         }
      }

      return var6;
   }

   public static enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;

      private Mode() {
      }
   }
}
