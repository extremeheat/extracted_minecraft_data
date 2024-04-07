package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class ModelResourceLocation extends ResourceLocation {
   @VisibleForTesting
   static final char VARIANT_SEPARATOR = '#';
   private final String variant;

   private ModelResourceLocation(String var1, String var2, String var3, @Nullable ResourceLocation.Dummy var4) {
      super(var1, var2, var4);
      this.variant = var3;
   }

   public ModelResourceLocation(String var1, String var2, String var3) {
      super(var1, var2);
      this.variant = lowercaseVariant(var3);
   }

   public ModelResourceLocation(ResourceLocation var1, String var2) {
      this(var1.getNamespace(), var1.getPath(), lowercaseVariant(var2), null);
   }

   public static ModelResourceLocation vanilla(String var0, String var1) {
      return new ModelResourceLocation("minecraft", var0, var1);
   }

   private static String lowercaseVariant(String var0) {
      return var0.toLowerCase(Locale.ROOT);
   }

   public String getVariant() {
      return this.variant;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ModelResourceLocation && super.equals(var1)) {
         ModelResourceLocation var2 = (ModelResourceLocation)var1;
         return this.variant.equals(var2.variant);
      } else {
         return false;
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
