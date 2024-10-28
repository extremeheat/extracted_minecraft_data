package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;

public class CatVariantTags {
   public static final TagKey<CatVariant> DEFAULT_SPAWNS = create("default_spawns");
   public static final TagKey<CatVariant> FULL_MOON_SPAWNS = create("full_moon_spawns");

   private CatVariantTags() {
      super();
   }

   private static TagKey<CatVariant> create(String var0) {
      return TagKey.create(Registries.CAT_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }
}
