package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class BastionBridgePools {
   public BastionBridgePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var2 = var1.getOrThrow(ProcessorLists.ENTRANCE_REPLACEMENT);
      Holder.Reference var3 = var1.getOrThrow(ProcessorLists.BASTION_GENERIC_DEGRADATION);
      Holder.Reference var4 = var1.getOrThrow(ProcessorLists.BRIDGE);
      Holder.Reference var5 = var1.getOrThrow(ProcessorLists.RAMPART_DEGRADATION);
      HolderGetter var6 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var7 = var6.getOrThrow(Pools.EMPTY);
      Pools.register(var0, "bastion/bridge/starting_pieces", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_face", (Holder)var3), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/bridge_pieces", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/bridge_pieces/bridge", (Holder)var4), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/legs", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/legs/leg_0", (Holder)var3), 1), Pair.of(StructurePoolElement.single("bastion/bridge/legs/leg_1", (Holder)var3), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/walls", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/walls/wall_base_0", (Holder)var5), 1), Pair.of(StructurePoolElement.single("bastion/bridge/walls/wall_base_1", (Holder)var5), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/ramparts", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/ramparts/rampart_0", (Holder)var5), 1), Pair.of(StructurePoolElement.single("bastion/bridge/ramparts/rampart_1", (Holder)var5), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/rampart_plates", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/rampart_plates/plate_0", (Holder)var5), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/bridge/connectors", new StructureTemplatePool(var7, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/bridge/connectors/back_bridge_top", (Holder)var3), 1), Pair.of(StructurePoolElement.single("bastion/bridge/connectors/back_bridge_bottom", (Holder)var3), 1)), StructureTemplatePool.Projection.RIGID));
   }
}
