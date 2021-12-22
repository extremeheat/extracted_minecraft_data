package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class BastionHousingUnitsPools {
   public BastionHousingUnitsPools() {
      super();
   }

   public static void bootstrap() {
   }

   static {
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/center_pieces"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_1", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/center_pieces/center_2", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/pathways"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/pathways/pathway_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/pathways/pathway_wall_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/walls/wall_bases"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/walls/wall_base", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/walls/connected_wall", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/stages/stage_0"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_1", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_2", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_0_3", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/stages/stage_1"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_1", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_2", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_1_3", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/stages/rot/stage_1"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/rot/stage_1_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/stages/stage_2"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_2_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_2_1", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/stages/stage_3"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_1", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_2", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/stages/stage_3_3", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/fillers/stage_0"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/fillers/stage_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/edges"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/edges/edge_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/wall_units"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/wall_units/unit_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/edge_wall_units"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/wall_units/edge_0_large", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/ramparts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_0", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_1", ProcessorLists.HOUSING), 1), Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_2", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/large_ramparts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/ramparts/ramparts_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
      Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/units/rampart_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(StructurePoolElement.single("bastion/units/rampart_plates/plate_0", ProcessorLists.HOUSING), 1)), StructureTemplatePool.Projection.RIGID));
   }
}
