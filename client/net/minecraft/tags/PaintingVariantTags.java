package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class PaintingVariantTags {
   public static final TagKey<PaintingVariant> PLACEABLE = create("placeable");

   private PaintingVariantTags() {
      super();
   }

   private static TagKey<PaintingVariant> create(String var0) {
      return TagKey.create(Registry.PAINTING_VARIANT_REGISTRY, new ResourceLocation(var0));
   }
}
