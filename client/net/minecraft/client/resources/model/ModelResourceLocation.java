package net.minecraft.client.resources.model;

import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

public record ModelResourceLocation(ResourceLocation id, String variant) {
   public static final String INVENTORY_VARIANT = "inventory";

   public ModelResourceLocation(ResourceLocation id, String variant) {
      super();
      variant = lowercaseVariant(variant);
      this.id = id;
      this.variant = variant;
   }

   public static ModelResourceLocation vanilla(String var0, String var1) {
      return new ModelResourceLocation(ResourceLocation.withDefaultNamespace(var0), var1);
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
      return this.id + "#" + this.variant;
   }
}
