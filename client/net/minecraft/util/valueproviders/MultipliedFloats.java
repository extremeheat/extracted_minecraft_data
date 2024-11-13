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

      for(SampledFloat var6 : this.values) {
         var2 *= var6.sample(var1);
      }

      return var2;
   }

   public String toString() {
      return "MultipliedFloats" + Arrays.toString(this.values);
   }
}
