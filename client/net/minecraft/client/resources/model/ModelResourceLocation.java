package net.minecraft.client.resources.model;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public record ModelResourceLocation(ResourceLocation id, String variant) {
   public ModelResourceLocation(ResourceLocation var1, String var2) {
      super();
      var2 = lowercaseVariant(var2);
      this.id = var1;
      this.variant = var2;
   }

   private static String lowercaseVariant(String var0) {
      return var0.toLowerCase(Locale.ROOT);
   }

   public String getVariant() {
      return this.variant;
   }

   public String toString() {
      String var10000 = String.valueOf(this.id);
      return var10000 + "#" + this.variant;
   }
}
