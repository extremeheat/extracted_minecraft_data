package net.minecraft.client.renderer.model;

import java.util.Locale;
import net.minecraft.util.ResourceLocation;

public class ModelResourceLocation extends ResourceLocation {
   private final String field_177519_c;

   protected ModelResourceLocation(String[] var1) {
      super(var1);
      this.field_177519_c = var1[2].toLowerCase(Locale.ROOT);
   }

   public ModelResourceLocation(String var1) {
      this(func_177517_b(var1));
   }

   public ModelResourceLocation(ResourceLocation var1, String var2) {
      this(var1.toString(), var2);
   }

   public ModelResourceLocation(String var1, String var2) {
      this(func_177517_b(var1 + '#' + var2));
   }

   protected static String[] func_177517_b(String var0) {
      String[] var1 = new String[]{null, var0, ""};
      int var2 = var0.indexOf(35);
      String var3 = var0;
      if (var2 >= 0) {
         var1[2] = var0.substring(var2 + 1, var0.length());
         if (var2 > 1) {
            var3 = var0.substring(0, var2);
         }
      }

      System.arraycopy(ResourceLocation.func_195823_b(var3, ':'), 0, var1, 0, 2);
      return var1;
   }

   public String func_177518_c() {
      return this.field_177519_c;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ModelResourceLocation && super.equals(var1)) {
         ModelResourceLocation var2 = (ModelResourceLocation)var1;
         return this.field_177519_c.equals(var2.field_177519_c);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.field_177519_c.hashCode();
   }

   public String toString() {
      return super.toString() + '#' + this.field_177519_c;
   }
}
