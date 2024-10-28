package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class BastionHousingUnitsPools {
   public BastionHousingUnitsPools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var2 = var1.getOrThrow(ProcessorLists.HOUSING);
      HolderGetter var3 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var4 = var3.getOrThrow(Pools.EMPTY);
      Pools.register(var0, "bastion/units/center_pieces", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_2", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/pathways", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/pathways/pathway_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/pathways/pathway_wall_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/walls/wall_bases", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/walls/wall_base", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/walls/connected_wall", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/stages/stage_0", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_2", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_3", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/stages/stage_1", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_2", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_3", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/stages/rot/stage_1", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/rot/stage_1_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/stages/stage_2", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_2_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_2_1", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/stages/stage_3", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_2", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_3", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/fillers/stage_0", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/fillers/stage_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/edges", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/edges/edge_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/wall_units", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/wall_units/unit_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/edge_wall_units", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/wall_units/edge_0_large", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/ramparts", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_0", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_1", (Holder)var2), 1), Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_2", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/large_ramparts", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(var0, "bastion/units/rampart_plates", new StructureTemplatePool(var4, ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/rampart_plates/plate_0", (Holder)var2), 1)), StructureTemplatePool.Projection.RIGID));
   }
}
