package net.minecraft.util;

public class FastColor {
   public static class ARGB32 {
      public static int red(int var0) {
         return var0 >> 16 & 255;
      }

      public static int green(int var0) {
         return var0 >> 8 & 255;
      }

      public static int blue(int var0) {
         return var0 & 255;
      }
   }
}
