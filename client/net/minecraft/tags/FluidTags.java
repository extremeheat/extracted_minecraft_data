package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public final class FluidTags {
   public static final TagKey<Fluid> WATER = create("water");
   public static final TagKey<Fluid> LAVA = create("lava");

   private FluidTags() {
      super();
   }

   private static TagKey<Fluid> create(String var0) {
      return TagKey.create(Registries.FLUID, ResourceLocation.withDefaultNamespace(var0));
   }
}
