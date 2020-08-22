package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class CanyonWorldCarver extends WorldCarver {
   private final float[] rs = new float[1024];

   public CanyonWorldCarver(Function var1) {
      super(var1, 256);
   }

   public boolean isStartChunk(Random var1, int var2, int var3, ProbabilityFeatureConfiguration var4) {
      return var1.nextFloat() <= var4.probability;
   }

   public boolean carve(ChunkAccess var1, Function var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, ProbabilityFeatureConfiguration var10) {
      int var11 = (this.getRange() * 2 - 1) * 16;
      double var12 = (double)(var5 * 16 + var3.nextInt(16));
      double var14 = (double)(var3.nextInt(var3.nextInt(40) + 8) + 20);
      double var16 = (double)(var6 * 16 + var3.nextInt(16));
      float var18 = var3.nextFloat() * 6.2831855F;
      float var19 = (var3.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double var20 = 3.0D;
      float var22 = (var3.nextFloat() * 2.0F + var3.nextFloat()) * 2.0F;
      int var23 = var11 - var3.nextInt(var11 / 4);
      boolean var24 = false;
      this.genCanyon(var1, var2, var3.nextLong(), var4, var7, var8, var12, var14, var16, var22, var18, var19, 0, var23, 3.0D, var9);
      return true;
   }

   private void genCanyon(ChunkAccess var1, Function var2, long var3, int var5, int var6, int var7, double var8, double var10, double var12, float var14, float var15, float var16, int var17, int var18, double var19, BitSet var21) {
      Random var22 = new Random(var3);
      float var23 = 1.0F;

      for(int var24 = 0; var24 < 256; ++var24) {
         if (var24 == 0 || var22.nextInt(3) == 0) {
            var23 = 1.0F + var22.nextFloat() * var22.nextFloat();
         }

         this.rs[var24] = var23 * var23;
      }

      float var33 = 0.0F;
      float var25 = 0.0F;

      for(int var26 = var17; var26 < var18; ++var26) {
         double var27 = 1.5D + (double)(Mth.sin((float)var26 * 3.1415927F / (float)var18) * var14);
         double var29 = var27 * var19;
         var27 *= (double)var22.nextFloat() * 0.25D + 0.75D;
         var29 *= (double)var22.nextFloat() * 0.25D + 0.75D;
         float var31 = Mth.cos(var16);
         float var32 = Mth.sin(var16);
         var8 += (double)(Mth.cos(var15) * var31);
         var10 += (double)var32;
         var12 += (double)(Mth.sin(var15) * var31);
         var16 *= 0.7F;
         var16 += var25 * 0.05F;
         var15 += var33 * 0.05F;
         var25 *= 0.8F;
         var33 *= 0.5F;
         var25 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 2.0F;
         var33 += (var22.nextFloat() - var22.nextFloat()) * var22.nextFloat() * 4.0F;
         if (var22.nextInt(4) != 0) {
            if (!this.canReach(var6, var7, var8, var12, var26, var18, var14)) {
               return;
            }

            this.carveSphere(var1, var2, var3, var5, var6, var7, var8, var10, var12, var27, var29, var21);
         }
      }

   }

   protected boolean skip(double var1, double var3, double var5, int var7) {
      return (var1 * var1 + var5 * var5) * (double)this.rs[var7 - 1] + var3 * var3 / 6.0D >= 1.0D;
   }
}
