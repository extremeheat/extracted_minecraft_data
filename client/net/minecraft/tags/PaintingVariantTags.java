package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantTags {
   public static final TagKey<PaintingVariant> PLACEABLE = create("placeable");

   private PaintingVariantTags() {
      super();
   }

   private static TagKey<PaintingVariant> create(String var0) {
      return TagKey.create(Registries.PAINTING_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }
}
