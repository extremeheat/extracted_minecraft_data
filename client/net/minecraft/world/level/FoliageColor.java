package net.minecraft.world.level;

public class FoliageColor {
   public static final int FOLIAGE_EVERGREEN = -10380959;
   public static final int FOLIAGE_BIRCH = -8345771;
   public static final int FOLIAGE_DEFAULT = -12012264;
   public static final int FOLIAGE_MANGROVE = -7158200;
   private static int[] pixels = new int[65536];

   public FoliageColor() {
      super();
   }

   public static void init(int[] var0) {
      pixels = var0;
   }

   public static int get(double var0, double var2) {
      return ColorMapColorUtil.get(var0, var2, pixels, -12012264);
   }
}
