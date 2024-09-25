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

public class DesertVillagePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("village/desert/town_centers");
   private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/desert/terminators");
   private static final ResourceKey<StructureTemplatePool> ZOMBIE_TERMINATORS_KEY = Pools.createKey("village/desert/zombie/terminators");

   public DesertVillagePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VillagePlacements.PATCH_CACTUS_VILLAGE);
      Holder.Reference var3 = var1.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE);
      HolderGetter var4 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var5 = var4.getOrThrow(ProcessorLists.ZOMBIE_DESERT);
      Holder.Reference var6 = var4.getOrThrow(ProcessorLists.FARM_DESERT);
      HolderGetter var7 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var8 = var7.getOrThrow(Pools.EMPTY);
      Holder.Reference var9 = var7.getOrThrow(TERMINATORS_KEY);
      Holder.Reference var10 = var7.getOrThrow(ZOMBIE_TERMINATORS_KEY);
      var0.register(
         START,
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/town_centers/desert_meeting_point_1"), 98),
               Pair.of(StructurePoolElement.legacy("village/desert/town_centers/desert_meeting_point_2"), 98),
               Pair.of(StructurePoolElement.legacy("village/desert/town_centers/desert_meeting_point_3"), 49),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/town_centers/desert_meeting_point_1", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/town_centers/desert_meeting_point_2", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/town_centers/desert_meeting_point_3", var5), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/streets",
         new StructureTemplatePool(
            var9,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/streets/corner_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/corner_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/straight_01"), 4),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/straight_02"), 4),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/straight_03"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/crossroad_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/crossroad_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/crossroad_03"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/square_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/square_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/streets/turn_01"), 3)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/desert/zombie/streets",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/corner_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/corner_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/straight_01"), 4),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/straight_02"), 4),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/straight_03"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/crossroad_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/crossroad_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/crossroad_03"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/square_01"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/square_02"), 3),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/streets/turn_01"), 3)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/desert/houses",
         new StructureTemplatePool(
            var9,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_2"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_3"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_4"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_5"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_6"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_7"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_small_house_8"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_medium_house_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_medium_house_2"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_butcher_shop_1"), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_tool_smith_1"), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_fletcher_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_shepherd_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_armorer_1"), 1),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_fisher_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_tannery_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_cartographer_house_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_library_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_mason_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_weaponsmith_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_temple_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_temple_2"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_large_farm_1", var6), 11),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_farm_1", var6), 4),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_farm_2", var6), 4),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_animal_pen_1"), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_animal_pen_2"), 2),
                  Pair.of(StructurePoolElement.empty(), 5)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/zombie/houses",
         new StructureTemplatePool(
            var10,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_1", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_2", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_3", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_4", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_5", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_6", var5), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_7", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_small_house_8", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_medium_house_1", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/houses/desert_medium_house_2", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_butcher_shop_1", var5), 2),
               Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_tool_smith_1", var5), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_fletcher_house_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_shepherd_house_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_armorer_1", var5), 1),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_fisher_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_tannery_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_cartographer_house_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_library_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_mason_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_weaponsmith_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_temple_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_temple_2", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_large_farm_1", var5), 7),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_farm_1", var5), 4),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_farm_2", var5), 4),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_animal_pen_1", var5), 2),
                  Pair.of(StructurePoolElement.legacy("village/desert/houses/desert_animal_pen_2", var5), 2),
                  Pair.of(StructurePoolElement.empty(), 5)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      var0.register(
         TERMINATORS_KEY,
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/terminators/terminator_01"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/terminators/terminator_02"), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      var0.register(
         ZOMBIE_TERMINATORS_KEY,
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/terminators/terminator_01"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/terminators/terminator_02"), 1)
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/desert/decor",
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/desert_lamp_1"), 10),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.empty(), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/zombie/decor",
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/desert_lamp_1", var5), 10),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.empty(), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/villagers",
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/villagers/baby"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/camel",
         new StructureTemplatePool(
            var8, ImmutableList.of(Pair.of(StructurePoolElement.legacy("village/desert/camel_spawn"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/desert/zombie/villagers",
         new StructureTemplatePool(
            var8,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/desert/zombie/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}