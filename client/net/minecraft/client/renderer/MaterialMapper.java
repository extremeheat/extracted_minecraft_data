package net.minecraft.client.renderer;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;

public record MaterialMapper(Identifier sheet, String prefix) {
   public MaterialMapper {
      super();
   }

   public Material apply(final Identifier path) {
      return new Material(this.sheet, path.withPrefix(this.prefix + "/"));
   }

   public Material defaultNamespaceApply(final String path) {
      return this.apply(Identifier.withDefaultNamespace(path));
   }
}
