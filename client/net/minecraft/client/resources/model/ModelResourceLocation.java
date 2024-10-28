package net.minecraft.client.resources.model;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public record ModelResourceLocation(ResourceLocation id, String variant) {
   public static final String INVENTORY_VARIANT = "inventory";

   public ModelResourceLocation(ResourceLocation var1, String var2) {
      super();
      var2 = lowercaseVariant(var2);
      this.id = var1;
      this.variant = var2;
   }

   public static ModelResourceLocation inventory(ResourceLocation var0) {
      return new ModelResourceLocation(var0, "inventory");
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

   public ResourceLocation id() {
      return this.id;
   }

   public String variant() {
      return this.variant;
   }
}
