package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class Pools {
   public static final ResourceKey<StructureTemplatePool> EMPTY;
   private static final StructureTemplatePool BUILTIN_EMPTY;

   public static StructureTemplatePool register(StructureTemplatePool var0) {
      return (StructureTemplatePool)BuiltinRegistries.register(BuiltinRegistries.TEMPLATE_POOL, (ResourceLocation)var0.getName(), var0);
   }

   public static StructureTemplatePool bootstrap() {
      BastionPieces.bootstrap();
      PillagerOutpostPools.bootstrap();
      VillagePools.bootstrap();
      return BUILTIN_EMPTY;
   }

   static {
      EMPTY = ResourceKey.create(Registry.TEMPLATE_POOL_REGISTRY, new ResourceLocation("empty"));
      BUILTIN_EMPTY = register(new StructureTemplatePool(EMPTY.location(), EMPTY.location(), ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
      bootstrap();
   }
}
