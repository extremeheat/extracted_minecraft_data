package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class PillagerOutpostPools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("pillager_outpost/base_plates");

   public PillagerOutpostPools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var2 = var1.getOrThrow(ProcessorLists.OUTPOST_ROT);
      HolderGetter var3 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var4 = var3.getOrThrow(Pools.EMPTY);
      var0.register(START, new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/base_plate"), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "pillager_outpost/towers", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.list(ImmutableList.of(StructurePoolElement.legacy("pillager_outpost/watchtower"), StructurePoolElement.legacy("pillager_outpost/watchtower_overgrown", var2))), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "pillager_outpost/feature_plates", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_plate"), 1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
      Pools.register(var0, "pillager_outpost/features", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage_with_allays"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_logs"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent1"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent2"), 1), Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_targets"), 1), Pair.of(StructurePoolElement.empty(), 6)), StructureTemplatePool.Projection.RIGID));
   }
}
