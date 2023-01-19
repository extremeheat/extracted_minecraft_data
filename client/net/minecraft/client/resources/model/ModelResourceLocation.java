package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public class ModelResourceLocation extends ResourceLocation {
   @VisibleForTesting
   static final char VARIANT_SEPARATOR = '#';
   private final String variant;

   protected ModelResourceLocation(String[] var1) {
      super(var1);
      this.variant = var1[2].toLowerCase(Locale.ROOT);
   }

   public ModelResourceLocation(String var1, String var2, String var3) {
      this(new String[]{var1, var2, var3});
   }

   public ModelResourceLocation(String var1) {
      this(decompose(var1));
   }

   public ModelResourceLocation(ResourceLocation var1, String var2) {
      this(var1.toString(), var2);
   }

   public ModelResourceLocation(String var1, String var2) {
      this(decompose(var1 + "#" + var2));
   }

   protected static String[] decompose(String var0) {
      String[] var1 = new String[]{null, var0, ""};
      int var2 = var0.indexOf(35);
      String var3 = var0;
      if (var2 >= 0) {
         var1[2] = var0.substring(var2 + 1, var0.length());
         if (var2 > 1) {
            var3 = var0.substring(0, var2);
         }
      }

      System.arraycopy(ResourceLocation.decompose(var3, ':'), 0, var1, 0, 2);
      return var1;
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ModelResourceLocation var2 && super.equals(var1) ? this.variant.equals(var2.variant) : false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   @Override
   public String toString() {
      return super.toString() + "#" + this.variant;
   }
}
