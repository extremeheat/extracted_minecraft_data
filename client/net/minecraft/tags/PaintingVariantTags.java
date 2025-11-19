package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

public class PaintingVariantTags {
   public static final TagKey<PaintingVariant> PLACEABLE = create("placeable");

   private PaintingVariantTags() {
      super();
   }

   private static TagKey<PaintingVariant> create(String var0) {
      return TagKey.<PaintingVariant>create(Registries.PAINTING_VARIANT, Identifier.withDefaultNamespace(var0));
   }
}
