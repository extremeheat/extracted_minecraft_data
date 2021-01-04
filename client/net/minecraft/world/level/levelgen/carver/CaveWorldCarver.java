package net.minecraft.world.level.levelgen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class CaveWorldCarver extends WorldCarver<ProbabilityFeatureConfiguration> {
   public CaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityFeatureConfiguration> var1, int var2) {
      super(var1, var2);
   }

   public boolean isStartChunk(Random var1, int var2, int var3, ProbabilityFeatureConfiguration var4) {
      return var1.nextFloat() <= var4.probability;
   }

   public boolean carve(ChunkAccess var1, Random var2, int var3, int var4, int var5, int var6, int var7, BitSet var8, ProbabilityFeatureConfiguration var9) {
      int var10 = (this.getRange() * 2 - 1) * 16;
      int var11 = var2.nextInt(var2.nextInt(var2.nextInt(this.getCaveBound()) + 1) + 1);

      for(int var12 = 0; var12 < var11; ++var12) {
         double var13 = (double)(var4 * 16 + var2.nextInt(16));
         double var15 = (double)this.getCaveY(var2);
         double var17 = (double)(var5 * 16 + var2.nextInt(16));
         int var19 = 1;
         float var22;
         if (var2.nextInt(4) == 0) {
            double var20 = 0.5D;
            var22 = 1.0F + var2.nextFloat() * 6.0F;
            this.genRoom(var1, var2.nextLong(), var3, var6, var7, var13, var15, var17, var22, 0.5D, var8);
            var19 += var2.nextInt(4);
         }

         for(int var26 = 0; var26 < var19; ++var26) {
            float var21 = var2.nextFloat() * 6.2831855F;
            var22 = (var2.nextFloat() - 0.5F) / 4.0F;
            float var23 = this.getThickness(var2);
            int var24 = var10 - var2.nextInt(var10 / 4);
            boolean var25 = false;
            this.genTunnel(var1, var2.nextLong(), var3, var6, var7, var13, var15, var17, var23, var21, var22, 0, var24, this.getYScale(), var8);
         }
      }

      return true;
   }

   protected int getCaveBound() {
      return 15;
   }

   protected float getThickness(Random var1) {
      float var2 = var1.nextFloat() * 2.0F + var1.nextFloat();
      if (var1.nextInt(10) == 0) {
         var2 *= var1.nextFloat() * var1.nextFloat() * 3.0F + 1.0F;
      }

      return var2;
   }

   protected double getYScale() {
      return 1.0D;
   }

   protected int getCaveY(Random var1) {
      return var1.nextInt(var1.nextInt(120) + 8);
   }

   protected void genRoom(ChunkAccess var1, long var2, int var4, int var5, int var6, double var7, double var9, double var11, float var13, double var14, BitSet var16) {
      double var17 = 1.5D + (double)(Mth.sin(1.5707964F) * var13);
      double var19 = var17 * var14;
      this.carveSphere(var1, var2, var4, var5, var6, var7 + 1.0D, var9, var11, var17, var19, var16);
   }

   protected void genTunnel(ChunkAccess var1, long var2, int var4, int var5, int var6, double var7, double var9, double var11, float var13, float var14, float var15, int var16, int var17, double var18, BitSet var20) {
      Random var21 = new Random(var2);
      int var22 = var21.nextInt(var17 / 2) + var17 / 4;
      boolean var23 = var21.nextInt(6) == 0;
      float var24 = 0.0F;
      float var25 = 0.0F;

      for(int var26 = var16; var26 < var17; ++var26) {
         double var27 = 1.5D + (double)(Mth.sin(3.1415927F * (float)var26 / (float)var17) * var13);
         double var29 = var27 * var18;
         float var31 = Mth.cos(var15);
         var7 += (double)(Mth.cos(var14) * var31);
         var9 += (double)Mth.sin(var15);
         var11 += (double)(Mth.sin(var14) * var31);
         var15 *= var23 ? 0.92F : 0.7F;
         var15 += var25 * 0.1F;
         var14 += var24 * 0.1F;
         var25 *= 0.9F;
         var24 *= 0.75F;
         var25 += (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 2.0F;
         var24 += (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 4.0F;
         if (var26 == var22 && var13 > 1.0F) {
            this.genTunnel(var1, var21.nextLong(), var4, var5, var6, var7, var9, var11, var21.nextFloat() * 0.5F + 0.5F, var14 - 1.5707964F, var15 / 3.0F, var26, var17, 1.0D, var20);
            this.genTunnel(var1, var21.nextLong(), var4, var5, var6, var7, var9, var11, var21.nextFloat() * 0.5F + 0.5F, var14 + 1.5707964F, var15 / 3.0F, var26, var17, 1.0D, var20);
            return;
         }

         if (var21.nextInt(4) != 0) {
            if (!this.canReach(var5, var6, var7, var11, var26, var17, var13)) {
               return;
            }

            this.carveSphere(var1, var2, var4, var5, var6, var7, var9, var11, var27, var29, var20);
         }
      }

   }

   protected boolean skip(double var1, double var3, double var5, int var7) {
      return var3 <= -0.7D || var1 * var1 + var3 * var3 + var5 * var5 >= 1.0D;
   }
}
