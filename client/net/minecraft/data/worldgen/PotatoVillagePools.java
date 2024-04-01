package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.apache.commons.lang3.tuple.Triple;

public class PotatoVillagePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("village/potato/town_centers");
   private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/potato/terminators");

   public PotatoVillagePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VillagePlacements.POTATO_VILLAGE);
      Holder.Reference var3 = var1.getOrThrow(VillagePlacements.FLOWER_PLAIN_VILLAGE);
      Holder.Reference var4 = var1.getOrThrow(VillagePlacements.PILE_POTATO_FRUIT_VILLAGE);
      HolderGetter var5 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var6 = var5.getOrThrow(ProcessorLists.SPOIL_10_PERCENT);
      Holder.Reference var7 = var5.getOrThrow(ProcessorLists.SPOIL_20_PERCENT);
      Holder.Reference var8 = var5.getOrThrow(ProcessorLists.SPOIL_70_PERCENT);
      Holder.Reference var9 = var5.getOrThrow(ProcessorLists.ZOMBIE_POTATO);
      Holder.Reference var10 = var5.getOrThrow(ProcessorLists.STREET_POTATO);
      Holder.Reference var11 = var5.getOrThrow(ProcessorLists.FARM_POTATO);
      HolderGetter var12 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var13 = var12.getOrThrow(Pools.EMPTY);
      Holder.Reference var14 = var12.getOrThrow(TERMINATORS_KEY);
      var0.register(
         START,
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/town_centers/plains_fountain_01", var7), 50),
               Pair.of(StructurePoolElement.legacy("village/potato/town_centers/plains_meeting_point_1", var7), 50),
               Pair.of(StructurePoolElement.legacy("village/potato/town_centers/plains_meeting_point_2"), 50),
               Pair.of(StructurePoolElement.legacy("village/potato/town_centers/plains_meeting_point_3", var8), 50),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/town_centers/plains_fountain_01", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/town_centers/plains_meeting_point_1", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/town_centers/plains_meeting_point_2", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/town_centers/plains_meeting_point_3", var9), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/streets",
         new StructureTemplatePool(
            ImmutableList.of(
               Triple.of(StructurePoolElement.legacy("village/potato/streets/corner_01", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/corner_02", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/corner_03", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_01", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_02", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_03", var10), 7, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_04", var10), 7, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_05", var10), 3, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/straight_06", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_01", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_02", var10), 1, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_03", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               new Triple[]{
                  Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_04", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
                  Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_05", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
                  Triple.of(StructurePoolElement.legacy("village/potato/streets/crossroad_06", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
                  Triple.of(StructurePoolElement.legacy("village/potato/streets/turn_01", var10), 3, StructureTemplatePool.Projection.TERRAIN_MATCHING),
                  Triple.of(StructurePoolElement.legacy("village/potato/houses/potato_maze"), 2, StructureTemplatePool.Projection.RIGID)
               }
            ),
            var14
         )
      );
      Pools.register(
         var0,
         "village/potato/zombie/streets",
         new StructureTemplatePool(
            ImmutableList.of(
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/corner_01", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/corner_02", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/corner_03", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_01", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_02", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_03", var10), 7, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_04", var10), 7, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_05", var10), 3, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/straight_06", var10), 4, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_01", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_02", var10), 1, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_03", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING),
               new Triple[]{
                  Triple.of(
                     StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_04", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING
                  ),
                  Triple.of(
                     StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_05", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING
                  ),
                  Triple.of(
                     StructurePoolElement.legacy("village/potato/zombie/streets/crossroad_06", var10), 2, StructureTemplatePool.Projection.TERRAIN_MATCHING
                  ),
                  Triple.of(StructurePoolElement.legacy("village/potato/zombie/streets/turn_01", var10), 3, StructureTemplatePool.Projection.TERRAIN_MATCHING),
                  Triple.of(StructurePoolElement.legacy("village/potato/houses/potato_maze"), 1, StructureTemplatePool.Projection.RIGID)
               }
            ),
            var14
         )
      );
      Pools.register(
         var0,
         "village/potato/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_3", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_4", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_5", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_6", var6), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_7", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_house_8", var6), 3),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_medium_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_medium_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_big_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_butcher_shop_1", var6), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_butcher_shop_2", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_tool_smith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_fletcher_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_shepherds_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_armorer_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_fisher_cottage_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_tannery_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_cartographer_1", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_library_1", var6), 5),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_library_2", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_masons_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_weaponsmith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_temple_3", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_temple_4", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_stable_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_stable_2"), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_large_farm_1", var11), 4),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_farm_1", var11), 4),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_animal_pen_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_animal_pen_2"), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_animal_pen_3"), 5),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_accessory_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_meeting_point_4", var8), 3),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_meeting_point_5"), 1),
                  Pair.of(StructurePoolElement.empty(), 10)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/zombie/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_2", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_3", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_4", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_5", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_6", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_7", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_small_house_8", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_medium_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_medium_house_2", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_big_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_butcher_shop_1", var9), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_butcher_shop_2", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_tool_smith_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_fletcher_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_shepherds_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_armorer_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_fisher_cottage_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_tannery_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_cartographer_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_library_1", var9), 3),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_library_2", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_masons_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_weaponsmith_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_temple_3", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_temple_4", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_stable_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_stable_2", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_large_farm_1", var9), 4),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_small_farm_1", var9), 4),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_animal_pen_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/houses/plains_animal_pen_2", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_animal_pen_3", var9), 5),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_meeting_point_4", var9), 3),
                  Pair.of(StructurePoolElement.legacy("village/potato/zombie/houses/plains_meeting_point_5", var9), 1),
                  Pair.of(StructurePoolElement.empty(), 10)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      var0.register(
         TERMINATORS_KEY,
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/terminators/terminator_01", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/terminators/terminator_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/terminators/terminator_03", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/terminators/terminator_04", var10), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/potato/trees",
         new StructureTemplatePool(var13, ImmutableList.of(Pair.of(StructurePoolElement.feature(var2), 1)), StructureTemplatePool.Projection.RIGID)
      );
      Pools.register(
         var0,
         "village/potato/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/plains_lamp_1"), 3),
               Pair.of(StructurePoolElement.feature(var2), 1),
               Pair.of(StructurePoolElement.feature(var3), 1),
               Pair.of(StructurePoolElement.feature(var4), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/frying_table_1"), 1),
               Pair.of(StructurePoolElement.empty(), 2)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/zombie/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/plains_lamp_1", var9), 1),
               Pair.of(StructurePoolElement.feature(var2), 1),
               Pair.of(StructurePoolElement.feature(var3), 1),
               Pair.of(StructurePoolElement.feature(var4), 1),
               Pair.of(StructurePoolElement.empty(), 2)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/villagers/baby"), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/zombie/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/potato/zombie/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/potato/well_bottoms",
         new StructureTemplatePool(
            var13, ImmutableList.of(Pair.of(StructurePoolElement.legacy("village/potato/well_bottom"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
