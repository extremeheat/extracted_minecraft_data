package net.minecraft.client.model;

public class ModelUtils {
   public ModelUtils() {
      super();
   }

   public static float rotlerpRad(float var0, float var1, float var2) {
      float var3;
      for(var3 = var1 - var0; var3 < -3.1415927F; var3 += 6.2831855F) {
      }

      while(var3 >= 3.1415927F) {
         var3 -= 6.2831855F;
      }

      return var0 + var2 * var3;
   }
}
