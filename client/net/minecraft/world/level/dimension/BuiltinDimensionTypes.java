package net.minecraft.world.level.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BuiltinDimensionTypes {
   public static final ResourceKey<DimensionType> OVERWORLD = register("overworld");
   public static final ResourceKey<DimensionType> NETHER = register("the_nether");
   public static final ResourceKey<DimensionType> END = register("the_end");
   public static final ResourceKey<DimensionType> OVERWORLD_CAVES = register("overworld_caves");

   public BuiltinDimensionTypes() {
      super();
   }

   private static ResourceKey<DimensionType> register(String var0) {
      return ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.withDefaultNamespace(var0));
   }
}
