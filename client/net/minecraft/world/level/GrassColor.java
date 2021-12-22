package net.minecraft.world.level;

public class GrassColor {
   private static int[] pixels = new int[65536];

   public GrassColor() {
      super();
   }

   public static void init(int[] var0) {
      pixels = var0;
   }

   public static int get(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0D - var0) * 255.0D);
      int var5 = (int)((1.0D - var2) * 255.0D);
      int var6 = var5 << 8 | var4;
      return var6 >= pixels.length ? -65281 : pixels[var6];
   }
}
