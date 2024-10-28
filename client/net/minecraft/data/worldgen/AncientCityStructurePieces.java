package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class AncientCityStructurePieces {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("ancient_city/city_center");

   public AncientCityStructurePieces() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var2 = var1.getOrThrow(ProcessorLists.ANCIENT_CITY_START_DEGRADATION);
      HolderGetter var3 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var4 = var3.getOrThrow(Pools.EMPTY);
      var0.register(START, new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_2", (Holder)var2), 1), Pair.of(StructurePoolElement.single("ancient_city/city_center/city_center_3", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      AncientCityStructurePools.bootstrap(var0);
   }
}
