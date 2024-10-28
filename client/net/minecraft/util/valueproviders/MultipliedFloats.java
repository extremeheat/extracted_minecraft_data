package net.minecraft.util.valueproviders;

import java.util.Arrays;
import net.minecraft.util.RandomSource;

public class MultipliedFloats implements SampledFloat {
   private final SampledFloat[] values;

   public MultipliedFloats(SampledFloat... var1) {
      super();
      this.values = var1;
   }

   public float sample(RandomSource var1) {
      float var2 = 1.0F;
      SampledFloat[] var3 = this.values;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         SampledFloat var6 = var3[var5];
         var2 *= var6.sample(var1);
      }

      return var2;
   }

   public String toString() {
      return "MultipliedFloats" + Arrays.toString(this.values);
   }
}
