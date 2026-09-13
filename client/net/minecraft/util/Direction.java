package net.minecraft.util;

public class Direction {
   public static final int[] field_71583_a = new int[]{0, -1, 0, 1};
   public static final int[] field_71581_b = new int[]{1, 0, -1, 0};
   public static final String[] field_82373_c = new String[]{"SOUTH", "WEST", "NORTH", "EAST"};
   public static final int[] field_71582_c = new int[]{3, 4, 2, 5};
   public static final int[] field_71579_d = new int[]{-1, -1, 2, 0, 1, 3};
   public static final int[] field_71580_e = new int[]{2, 3, 0, 1};
   public static final int[] field_71577_f = new int[]{1, 2, 3, 0};
   public static final int[] field_71578_g = new int[]{3, 0, 1, 2};
   public static final int[][] field_71584_h = new int[][]{{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};

   public static int func_82372_a(double var0, double var2) {
      if (MathHelper.func_76135_e((float)var0) > MathHelper.func_76135_e((float)var2)) {
         return var0 > 0.0 ? 1 : 3;
      } else {
         return var2 > 0.0 ? 2 : 0;
      }
   }
}
