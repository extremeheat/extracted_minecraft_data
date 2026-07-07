package com.mojang.renderpearl.backend.vulkan.glsl;

class SpvcUtil {
   private SpvcUtil() {
      super();
   }

   public static String imageDimensionToString(final int dimension) {
      String var10000;
      switch (dimension) {
         case 0 -> var10000 = "1D";
         case 1 -> var10000 = "2D";
         case 2 -> var10000 = "3D";
         case 3 -> var10000 = "Cube";
         case 4 -> var10000 = "Rect";
         case 5 -> var10000 = "Buffer";
         case 6 -> var10000 = "SubpassData";
         default -> var10000 = "0x" + Integer.toHexString(dimension);
      }

      return var10000;
   }
}
