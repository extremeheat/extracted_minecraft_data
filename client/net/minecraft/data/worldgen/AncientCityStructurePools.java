package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.CavePlacements;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class AncientCityStructurePools {
   public AncientCityStructurePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(CavePlacements.SCULK_PATCH_ANCIENT_CITY);
      HolderGetter var3 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var4 = var3.getOrThrow(ProcessorLists.ANCIENT_CITY_GENERIC_DEGRADATION);
      Holder.Reference var5 = var3.getOrThrow(ProcessorLists.ANCIENT_CITY_WALLS_DEGRADATION);
      HolderGetter var6 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var7 = var6.getOrThrow(Pools.EMPTY);
      Pools.register(
         var0,
         "ancient_city/structures",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(
               Pair.of(StructurePoolElement.empty(), 7),
               Pair.of(StructurePoolElement.single("ancient_city/structures/barracks", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/chamber_1", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/chamber_2", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/chamber_3", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/sauna_1", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/small_statue", var4), 4),
               Pair.of(StructurePoolElement.single("ancient_city/structures/large_ruin_1", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/structures/tall_ruin_1", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/structures/tall_ruin_2", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/structures/tall_ruin_3", var4), 2),
               Pair.of(StructurePoolElement.single("ancient_city/structures/tall_ruin_4", var4), 2),
               new Pair[]{
                  Pair.of(
                     StructurePoolElement.list(
                        ImmutableList.of(
                           StructurePoolElement.single("ancient_city/structures/camp_1", var4),
                           StructurePoolElement.single("ancient_city/structures/camp_2", var4),
                           StructurePoolElement.single("ancient_city/structures/camp_3", var4)
                        )
                     ),
                     1
                  ),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/medium_ruin_1", var4), 1),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/medium_ruin_2", var4), 1),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/small_ruin_1", var4), 1),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/small_ruin_2", var4), 1),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/large_pillar_1", var4), 1),
                  Pair.of(StructurePoolElement.single("ancient_city/structures/medium_pillar_1", var4), 1),
                  Pair.of(StructurePoolElement.list(ImmutableList.of(StructurePoolElement.single("ancient_city/structures/ice_box_1"))), 1)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "ancient_city/sculk",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(Pair.of(StructurePoolElement.feature(var2), 6), Pair.of(StructurePoolElement.empty(), 1)),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "ancient_city/walls",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_corner_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_intersection_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_lshape_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_2", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_2", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_3", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_4", var5), 4),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_passage_1", var5), 3),
               Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_corner_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_corner_wall_2", var5), 1),
               new Pair[]{
                  Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_horizontal_wall_stairs_1", var5), 2),
                  Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_horizontal_wall_stairs_2", var5), 2),
                  Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_horizontal_wall_stairs_3", var5), 3),
                  Pair.of(StructurePoolElement.single("ancient_city/walls/ruined_horizontal_wall_stairs_4", var5), 3)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "ancient_city/walls/no_corners",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_2", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_1", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_2", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_3", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_4", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_stairs_5", var5), 1),
               Pair.of(StructurePoolElement.single("ancient_city/walls/intact_horizontal_wall_bridge", var5), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "ancient_city/city_center/walls",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/bottom_1", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/bottom_2", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/bottom_left_corner", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/bottom_right_corner_1", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/bottom_right_corner_2", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/left", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/right", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/top", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/top_right_corner", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city_center/walls/top_left_corner", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "ancient_city/city/entrance",
         new StructureTemplatePool(
            var7,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_connector", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_path_1", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_path_2", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_path_3", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_path_4", var4), 1),
               Pair.of(StructurePoolElement.single("ancient_city/city/entrance/entrance_path_5", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
