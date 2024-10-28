package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class Pools {
   public static final ResourceKey<StructureTemplatePool> EMPTY = createKey("empty");

   public Pools() {
      super();
   }

   public static ResourceKey<StructureTemplatePool> createKey(String var0) {
      return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace(var0));
   }

   public static ResourceKey<StructureTemplatePool> parseKey(String var0) {
      return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.parse(var0));
   }

   public static void register(BootstrapContext<StructureTemplatePool> var0, String var1, StructureTemplatePool var2) {
      var0.register(createKey(var1), var2);
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var2 = var1.getOrThrow(EMPTY);
      var0.register(EMPTY, new StructureTemplatePool(var2, ImmutableList.of(), StructureTemplatePool.Projection.RIGID));
      BastionPieces.bootstrap(var0);
      PillagerOutpostPools.bootstrap(var0);
      VillagePools.bootstrap(var0);
      AncientCityStructurePieces.bootstrap(var0);
      TrailRuinsStructurePools.bootstrap(var0);
      TrialChambersStructurePools.bootstrap(var0);
   }
}
