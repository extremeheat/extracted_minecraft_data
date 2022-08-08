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

      for(int var3 = 0; var3 < this.values.length; ++var3) {
         var2 *= this.values[var3].sample(var1);
      }

      return var2;
   }

   public String toString() {
      return "MultipliedFloats" + Arrays.toString(this.values);
   }
}
