package net.minecraft.world.level;

public class DryFoliageColor {
   public static final int FOLIAGE_DRY_DEFAULT = -10732494;
   private static int[] pixels = new int[65536];

   public DryFoliageColor() {
      super();
   }

   public static void init(int[] var0) {
      pixels = var0;
   }

   public static int get(double var0, double var2) {
      return ColorMapColorUtil.get(var0, var2, pixels, -10732494);
   }
}
