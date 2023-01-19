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

public class PlainVillagePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("village/plains/town_centers");
   private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/plains/terminators");

   public PlainVillagePools() {
      super();
   }

   public static void bootstrap(BootstapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VillagePlacements.OAK_VILLAGE);
      Holder.Reference var3 = var1.getOrThrow(VillagePlacements.FLOWER_PLAIN_VILLAGE);
      Holder.Reference var4 = var1.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE);
      HolderGetter var5 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var6 = var5.getOrThrow(ProcessorLists.MOSSIFY_10_PERCENT);
      Holder.Reference var7 = var5.getOrThrow(ProcessorLists.MOSSIFY_20_PERCENT);
      Holder.Reference var8 = var5.getOrThrow(ProcessorLists.MOSSIFY_70_PERCENT);
      Holder.Reference var9 = var5.getOrThrow(ProcessorLists.ZOMBIE_PLAINS);
      Holder.Reference var10 = var5.getOrThrow(ProcessorLists.STREET_PLAINS);
      Holder.Reference var11 = var5.getOrThrow(ProcessorLists.FARM_PLAINS);
      HolderGetter var12 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var13 = var12.getOrThrow(Pools.EMPTY);
      Holder.Reference var14 = var12.getOrThrow(TERMINATORS_KEY);
      var0.register(
         START,
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/town_centers/plains_fountain_01", var7), 50),
               Pair.of(StructurePoolElement.legacy("village/plains/town_centers/plains_meeting_point_1", var7), 50),
               Pair.of(StructurePoolElement.legacy("village/plains/town_centers/plains_meeting_point_2"), 50),
               Pair.of(StructurePoolElement.legacy("village/plains/town_centers/plains_meeting_point_3", var8), 50),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/town_centers/plains_fountain_01", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/town_centers/plains_meeting_point_1", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/town_centers/plains_meeting_point_2", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/town_centers/plains_meeting_point_3", var9), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/plains/streets",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/streets/corner_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/corner_02", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/corner_03", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_01", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_02", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_03", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_04", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_05", var10), 3),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/straight_06", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_03", var10), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_04", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_05", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/streets/crossroad_06", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/streets/turn_01", var10), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/plains/zombie/streets",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/corner_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/corner_02", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/corner_03", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_01", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_02", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_03", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_04", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_05", var10), 3),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/straight_06", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_03", var10), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_04", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_05", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/crossroad_06", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/streets/turn_01", var10), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/plains/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_3", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_4", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_5", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_6", var6), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_7", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_house_8", var6), 3),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_medium_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_medium_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_big_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_butcher_shop_1", var6), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_butcher_shop_2", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_tool_smith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_fletcher_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_shepherds_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_armorer_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_fisher_cottage_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_tannery_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_cartographer_1", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_library_1", var6), 5),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_library_2", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_masons_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_weaponsmith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_temple_3", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_temple_4", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_stable_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_stable_2"), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_large_farm_1", var11), 4),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_farm_1", var11), 4),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_animal_pen_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_animal_pen_2"), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_animal_pen_3"), 5),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_accessory_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_meeting_point_4", var8), 3),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_meeting_point_5"), 1),
                  Pair.of(StructurePoolElement.empty(), 10)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/plains/zombie/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_2", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_3", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_4", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_5", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_6", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_7", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_small_house_8", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_medium_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_medium_house_2", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_big_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_butcher_shop_1", var9), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_butcher_shop_2", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_tool_smith_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_fletcher_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_shepherds_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_armorer_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_fisher_cottage_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_tannery_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_cartographer_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_library_1", var9), 3),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_library_2", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_masons_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_weaponsmith_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_temple_3", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_temple_4", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_stable_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_stable_2", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_large_farm_1", var9), 4),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_small_farm_1", var9), 4),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_animal_pen_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/houses/plains_animal_pen_2", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_animal_pen_3", var9), 5),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_meeting_point_4", var9), 3),
                  Pair.of(StructurePoolElement.legacy("village/plains/zombie/houses/plains_meeting_point_5", var9), 1),
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
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_01", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_03", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_04", var10), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/plains/trees",
         new StructureTemplatePool(var13, ImmutableList.of(Pair.of(StructurePoolElement.feature(var2), 1)), StructureTemplatePool.Projection.RIGID)
      );
      Pools.register(
         var0,
         "village/plains/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/plains_lamp_1"), 2),
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
         "village/plains/zombie/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/plains_lamp_1", var9), 1),
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
         "village/plains/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/villagers/baby"), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/plains/zombie/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/zombie/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/animals",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/common/animals/cows_1"), 7),
               Pair.of(StructurePoolElement.legacy("village/common/animals/pigs_1"), 7),
               Pair.of(StructurePoolElement.legacy("village/common/animals/horses_1"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/horses_2"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/horses_3"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/horses_4"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/horses_5"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_1"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_2"), 1),
               Pair.of(StructurePoolElement.empty(), 5)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/sheep",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_1"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_2"), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/cats",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_black"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_british"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_calico"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_persian"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_ragdoll"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_red"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_siamese"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_tabby"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_white"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/cat_jellie"), 1),
               Pair.of(StructurePoolElement.empty(), 3)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/butcher_animals",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/common/animals/cows_1"), 3),
               Pair.of(StructurePoolElement.legacy("village/common/animals/pigs_1"), 3),
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_1"), 1),
               Pair.of(StructurePoolElement.legacy("village/common/animals/sheep_2"), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/iron_golem",
         new StructureTemplatePool(
            var13, ImmutableList.of(Pair.of(StructurePoolElement.legacy("village/common/iron_golem"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/common/well_bottoms",
         new StructureTemplatePool(
            var13, ImmutableList.of(Pair.of(StructurePoolElement.legacy("village/common/well_bottom"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
