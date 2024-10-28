package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class BastionPieces {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("bastion/starts");

   public BastionPieces() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var2 = var1.getOrThrow(ProcessorLists.BASTION_GENERIC_DEGRADATION);
      HolderGetter var3 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var4 = var3.getOrThrow(Pools.EMPTY);
      var0.register(START, new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/air_base", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/hoglin_stable/air_base", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/treasure/big_air_full", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_base", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      BastionHousingUnitsPools.bootstrap(var0);
      BastionHoglinStablePools.bootstrap(var0);
      BastionTreasureRoomPools.bootstrap(var0);
      BastionBridgePools.bootstrap(var0);
      BastionSharedPools.bootstrap(var0);
   }
}
