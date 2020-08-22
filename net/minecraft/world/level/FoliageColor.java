package net.minecraft.world.level;

public class FoliageColor {
   private static int[] pixels = new int[65536];

   public static void init(int[] var0) {
      pixels = var0;
   }

   public static int get(double var0, double var2) {
      var2 *= var0;
      int var4 = (int)((1.0D - var0) * 255.0D);
      int var5 = (int)((1.0D - var2) * 255.0D);
      return pixels[var5 << 8 | var4];
   }

   public static int getEvergreenColor() {
      return 6396257;
   }

   public static int getBirchColor() {
      return 8431445;
   }

   public static int getDefaultColor() {
      return 4764952;
   }
}
