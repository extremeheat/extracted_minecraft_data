package net.minecraft.world.level;

public interface ColorMapColorUtil {
   static int get(double var0, double var2, int[] var4, int var5) {
      var2 *= var0;
      int var6 = (int)((1.0 - var0) * 255.0);
      int var7 = (int)((1.0 - var2) * 255.0);
      int var8 = var7 << 8 | var6;
      return var8 >= var4.length ? var5 : var4[var8];
   }
}
