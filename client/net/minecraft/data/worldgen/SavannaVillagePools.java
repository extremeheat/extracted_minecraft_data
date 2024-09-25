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

public class SavannaVillagePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("village/savanna/town_centers");
   private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/savanna/terminators");
   private static final ResourceKey<StructureTemplatePool> ZOMBIE_TERMINATORS_KEY = Pools.createKey("village/savanna/zombie/terminators");

   public SavannaVillagePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VillagePlacements.ACACIA_VILLAGE);
      Holder.Reference var3 = var1.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE);
      Holder.Reference var4 = var1.getOrThrow(VillagePlacements.PILE_MELON_VILLAGE);
      HolderGetter var5 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var6 = var5.getOrThrow(ProcessorLists.ZOMBIE_SAVANNA);
      Holder.Reference var7 = var5.getOrThrow(ProcessorLists.STREET_SAVANNA);
      Holder.Reference var8 = var5.getOrThrow(ProcessorLists.FARM_SAVANNA);
      HolderGetter var9 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var10 = var9.getOrThrow(Pools.EMPTY);
      Holder.Reference var11 = var9.getOrThrow(TERMINATORS_KEY);
      Holder.Reference var12 = var9.getOrThrow(ZOMBIE_TERMINATORS_KEY);
      var0.register(
         START,
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/town_centers/savanna_meeting_point_1"), 100),
               Pair.of(StructurePoolElement.legacy("village/savanna/town_centers/savanna_meeting_point_2"), 50),
               Pair.of(StructurePoolElement.legacy("village/savanna/town_centers/savanna_meeting_point_3"), 150),
               Pair.of(StructurePoolElement.legacy("village/savanna/town_centers/savanna_meeting_point_4"), 150),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/town_centers/savanna_meeting_point_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/town_centers/savanna_meeting_point_2", var6), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/town_centers/savanna_meeting_point_3", var6), 3),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/town_centers/savanna_meeting_point_4", var6), 3)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/savanna/streets",
         new StructureTemplatePool(
            var11,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/corner_01", var7), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/corner_03", var7), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_02", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_04", var7), 7),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_05", var7), 3),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_06", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_08", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_09", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_10", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/straight_11", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_02", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_03", var7), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_04", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_05", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_06", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/crossroad_07", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/split_01", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/split_02", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/streets/turn_01", var7), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/savanna/zombie/streets",
         new StructureTemplatePool(
            var12,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/corner_01", var7), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/corner_03", var7), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_02", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_04", var7), 7),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_05", var7), 3),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_06", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_08", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_09", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_10", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/straight_11", var7), 4),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_02", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_03", var7), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_04", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_05", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_06", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/crossroad_07", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/split_01", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/split_02", var7), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/streets/turn_01", var7), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/savanna/houses",
         new StructureTemplatePool(
            var11,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_2"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_3"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_4"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_5"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_6"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_7"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_house_8"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_medium_house_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_medium_house_2"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_butchers_shop_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_butchers_shop_2"), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_tool_smith_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_fletcher_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_shepherd_1"), 7),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_armorer_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_fisher_cottage_1"), 3),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_tannery_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_cartographer_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_library_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_mason_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_weaponsmith_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_weaponsmith_2"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_temple_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_temple_2"), 3),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_large_farm_1", var8), 4),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_large_farm_2", var8), 6),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_farm", var8), 4),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_animal_pen_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_animal_pen_2"), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_animal_pen_3"), 2),
                  Pair.of(StructurePoolElement.empty(), 5)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/savanna/zombie/houses",
         new StructureTemplatePool(
            var12,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_3", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_4", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_5", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_6", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_7", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_small_house_8", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_medium_house_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_medium_house_2", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_butchers_shop_1", var6), 2),
               Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_butchers_shop_2", var6), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_tool_smith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_fletcher_house_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_shepherd_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_armorer_1", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_fisher_cottage_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_tannery_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_cartographer_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_library_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_mason_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_weaponsmith_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_weaponsmith_2", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_temple_1", var6), 1),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_temple_2", var6), 3),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_large_farm_1", var6), 4),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_large_farm_2", var6), 4),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_small_farm", var6), 4),
                  Pair.of(StructurePoolElement.legacy("village/savanna/houses/savanna_animal_pen_1", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_animal_pen_2", var6), 2),
                  Pair.of(StructurePoolElement.legacy("village/savanna/zombie/houses/savanna_animal_pen_3", var6), 2),
                  Pair.of(StructurePoolElement.empty(), 5)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      var0.register(
         TERMINATORS_KEY,
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_01", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_02", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_03", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_04", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/terminators/terminator_05", var7), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      var0.register(
         ZOMBIE_TERMINATORS_KEY,
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_01", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_02", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_03", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/plains/terminators/terminator_04", var7), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/terminators/terminator_05", var7), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/savanna/trees",
         new StructureTemplatePool(var10, ImmutableList.of(Pair.of(StructurePoolElement.feature(var2), 1)), StructureTemplatePool.Projection.RIGID)
      );
      Pools.register(
         var0,
         "village/savanna/decor",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/savanna_lamp_post_01"), 4),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.feature(var4), 1),
               Pair.of(StructurePoolElement.empty(), 4)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/savanna/zombie/decor",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/savanna_lamp_post_01", var6), 4),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.feature(var4), 1),
               Pair.of(StructurePoolElement.empty(), 4)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/savanna/villagers",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/villagers/baby"), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/savanna/zombie/villagers",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/savanna/zombie/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}