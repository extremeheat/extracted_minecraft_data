package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;

public record MaterialMapper(Identifier sheet, String prefix) {
   public MaterialMapper(Identifier var1, String var2) {
      super();
      this.sheet = var1;
      this.prefix = var2;
   }

   public Material apply(Identifier var1) {
      return new Material(this.sheet, var1.withPrefix(this.prefix + "/"));
   }

   public Material defaultNamespaceApply(String var1) {
      return this.apply(Identifier.withDefaultNamespace(var1));
   }
}
