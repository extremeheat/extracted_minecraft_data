package net.minecraft.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class UpdateOneTwentyOnePools {
   public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

   public UpdateOneTwentyOnePools() {
      super();
   }

   public static ResourceKey<StructureTemplatePool> createKey(String var0) {
      return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(var0));
   }

   public static void register(BootstapContext<StructureTemplatePool> var0, String var1, StructureTemplatePool var2) {
      Pools.register(var0, var1, var2);
   }

   public static void bootstrap(BootstapContext<StructureTemplatePool> var0) {
      TrialChambersStructurePools.bootstrap(var0);
   }
}
