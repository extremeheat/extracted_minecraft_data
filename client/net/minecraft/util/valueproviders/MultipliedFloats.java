package net.minecraft.util.valueproviders;

import java.util.Arrays;
import net.minecraft.util.RandomSource;

public class MultipliedFloats implements SampledFloat {
   private final SampledFloat[] values;

   public MultipliedFloats(SampledFloat... var1) {
      super();
      this.values = var1;
   }

   @Override
   public float sample(RandomSource var1) {
      float var2 = 1.0F;

      for(SampledFloat var6 : this.values) {
         var2 *= var6.sample(var1);
      }

      return var2;
   }

   @Override
   public String toString() {
      return "MultipliedFloats" + Arrays.toString((Object[])this.values);
   }
}
