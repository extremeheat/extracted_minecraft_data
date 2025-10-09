package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;

public class CubicSampler {
   private static final int GAUSSIAN_SAMPLE_RADIUS = 2;
   private static final int GAUSSIAN_SAMPLE_BREADTH = 6;
   private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};
   @Nullable
   private BiomeManager biomeManager;
   private Vec3 position;
   private final Reference2DoubleArrayMap<Holder<Biome>> weights;

   public CubicSampler() {
      super();
      this.position = Vec3.ZERO;
      this.weights = new Reference2DoubleArrayMap();
   }

   private void computeWeights() {
      if (this.weights.isEmpty() && this.biomeManager != null) {
         Vec3 var1 = this.position.subtract(2.0, 2.0, 2.0).scale(0.25);
         int var2 = Mth.floor(var1.x());
         int var3 = Mth.floor(var1.y());
         int var4 = Mth.floor(var1.z());
         double var5 = var1.x() - (double)var2;
         double var7 = var1.y() - (double)var3;
         double var9 = var1.z() - (double)var4;

         for(int var11 = 0; var11 < 6; ++var11) {
            double var12 = Mth.lerp(var9, GAUSSIAN_SAMPLE_KERNEL[var11 + 1], GAUSSIAN_SAMPLE_KERNEL[var11]);
            int var14 = var4 - 2 + var11;

            for(int var15 = 0; var15 < 6; ++var15) {
               double var16 = Mth.lerp(var5, GAUSSIAN_SAMPLE_KERNEL[var15 + 1], GAUSSIAN_SAMPLE_KERNEL[var15]);
               int var18 = var2 - 2 + var15;

               for(int var19 = 0; var19 < 6; ++var19) {
                  double var20 = Mth.lerp(var7, GAUSSIAN_SAMPLE_KERNEL[var19 + 1], GAUSSIAN_SAMPLE_KERNEL[var19]);
                  int var22 = var3 - 2 + var19;
                  double var23 = var16 * var20 * var12;
                  Holder var25 = this.biomeManager.getNoiseBiomeAtQuart(var18, var22, var14);
                  this.weights.mergeDouble(var25, var23, Double::sum);
               }
            }
         }

      }
   }

   public Vec3 sampleVec3(Function<Holder<Biome>, Vec3> var1) {
      this.computeWeights();
      if (this.weights.isEmpty()) {
         return Vec3.ZERO;
      } else if (this.weights.size() == 1) {
         Holder var10 = (Holder)this.weights.keySet().iterator().next();
         return (Vec3)var1.apply(var10);
      } else {
         double var2 = 0.0;
         Vec3 var4 = Vec3.ZERO;

         Holder var7;
         double var8;
         for(ObjectIterator var5 = this.weights.reference2DoubleEntrySet().iterator(); var5.hasNext(); var4 = var4.add(((Vec3)var1.apply(var7)).scale(var8))) {
            Reference2DoubleMap.Entry var6 = (Reference2DoubleMap.Entry)var5.next();
            var7 = (Holder)var6.getKey();
            var8 = var6.getDoubleValue();
            var2 += var8;
         }

         return var4.scale(1.0 / var2);
      }
   }

   public void update(BiomeManager var1, Vec3 var2) {
      this.biomeManager = var1;
      this.position = var2;
      this.weights.clear();
   }
}
