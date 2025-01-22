package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

public record MaterialMapper(ResourceLocation sheet, String prefix) {
   public MaterialMapper(ResourceLocation var1, String var2) {
      super();
      this.sheet = var1;
      this.prefix = var2;
   }

   public Material apply(ResourceLocation var1) {
      return new Material(this.sheet, var1.withPrefix(this.prefix + "/"));
   }

   public Material defaultNamespaceApply(String var1) {
      return this.apply(ResourceLocation.withDefaultNamespace(var1));
   }
}
