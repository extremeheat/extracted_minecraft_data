package net.minecraft.world.attribute;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public interface LerpFunction<T> {
   static LerpFunction<Float> ofFloat() {
      return Mth::lerp;
   }

   static <T> LerpFunction<T> ofStep(float var0) {
      return (var1, var2, var3) -> var1 >= var0 ? var3 : var2;
   }

   static LerpFunction<Integer> ofColor() {
      return ARGB::srgbLerp;
   }

   T apply(float var1, T var2, T var3);
}
