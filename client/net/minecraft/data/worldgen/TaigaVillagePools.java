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

public class TaigaVillagePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("village/taiga/town_centers");
   private static final ResourceKey<StructureTemplatePool> TERMINATORS_KEY = Pools.createKey("village/taiga/terminators");

   public TaigaVillagePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.PLACED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VillagePlacements.SPRUCE_VILLAGE);
      Holder.Reference var3 = var1.getOrThrow(VillagePlacements.PINE_VILLAGE);
      Holder.Reference var4 = var1.getOrThrow(VillagePlacements.PILE_PUMPKIN_VILLAGE);
      Holder.Reference var5 = var1.getOrThrow(VillagePlacements.PATCH_TAIGA_GRASS_VILLAGE);
      Holder.Reference var6 = var1.getOrThrow(VillagePlacements.PATCH_BERRY_BUSH_VILLAGE);
      HolderGetter var7 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var8 = var7.getOrThrow(ProcessorLists.MOSSIFY_10_PERCENT);
      Holder.Reference var9 = var7.getOrThrow(ProcessorLists.ZOMBIE_TAIGA);
      Holder.Reference var10 = var7.getOrThrow(ProcessorLists.STREET_SNOWY_OR_TAIGA);
      Holder.Reference var11 = var7.getOrThrow(ProcessorLists.FARM_TAIGA);
      HolderGetter var12 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var13 = var12.getOrThrow(Pools.EMPTY);
      Holder.Reference var14 = var12.getOrThrow(TERMINATORS_KEY);
      var0.register(
         START,
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/town_centers/taiga_meeting_point_1", var8), 49),
               Pair.of(StructurePoolElement.legacy("village/taiga/town_centers/taiga_meeting_point_2", var8), 49),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/town_centers/taiga_meeting_point_1", var9), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/town_centers/taiga_meeting_point_2", var9), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/taiga/streets",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/corner_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/corner_02", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/corner_03", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_01", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_02", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_03", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_04", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_05", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/straight_06", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_01", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_03", var10), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_04", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_05", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/streets/crossroad_06", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/streets/turn_01", var10), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/taiga/zombie/streets",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/corner_01", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/corner_02", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/corner_03", var10), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_01", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_02", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_03", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_04", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_05", var10), 7),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/straight_06", var10), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_01", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_02", var10), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_03", var10), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_04", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_05", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/crossroad_06", var10), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/streets/turn_01", var10), 3)
               }
            ),
            StructureTemplatePool.Projection.TERRAIN_MATCHING
         )
      );
      Pools.register(
         var0,
         "village/taiga/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_house_1", var8), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_house_2", var8), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_house_3", var8), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_house_4", var8), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_house_5", var8), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_medium_house_1", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_medium_house_2", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_medium_house_3", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_medium_house_4", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_butcher_shop_1", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_tool_smith_1", var8), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_fletcher_house_1", var8), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_shepherds_house_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_armorer_house_1", var8), 1),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_armorer_2", var8), 1),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_fisher_cottage_1", var8), 3),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_tannery_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_cartographer_house_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_library_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_masons_house_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_weaponsmith_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_weaponsmith_2", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_temple_1", var8), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_large_farm_1", var11), 6),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_large_farm_2", var11), 6),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_farm_1", var8), 1),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_animal_pen_1", var8), 2),
                  Pair.of(StructurePoolElement.empty(), 6)
               }
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/taiga/zombie/houses",
         new StructureTemplatePool(
            var14,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_small_house_1", var9), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_small_house_2", var9), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_small_house_3", var9), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_small_house_4", var9), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_small_house_5", var9), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_medium_house_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_medium_house_2", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_medium_house_3", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_medium_house_4", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_butcher_shop_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_tool_smith_1", var9), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_fletcher_house_1", var9), 2),
               new Pair[]{
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_shepherds_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_armorer_house_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_fisher_cottage_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_tannery_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_cartographer_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_library_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_masons_house_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_weaponsmith_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_weaponsmith_2", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_temple_1", var9), 2),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_large_farm_1", var9), 6),
                  Pair.of(StructurePoolElement.legacy("village/taiga/zombie/houses/taiga_large_farm_2", var9), 6),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_small_farm_1", var9), 1),
                  Pair.of(StructurePoolElement.legacy("village/taiga/houses/taiga_animal_pen_1", var9), 2),
                  Pair.of(StructurePoolElement.empty(), 6)
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
         "village/taiga/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_lamp_post_1"), 10),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_1"), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_2"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_3"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_4"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_5"), 2),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_6"), 1),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.feature(var4), 2),
               Pair.of(StructurePoolElement.feature(var5), 4),
               Pair.of(StructurePoolElement.feature(var6), 1),
               new Pair[]{Pair.of(StructurePoolElement.empty(), 4)}
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/taiga/zombie/decor",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_1"), 4),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_2"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_3"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/taiga_decoration_4"), 1),
               Pair.of(StructurePoolElement.feature(var2), 4),
               Pair.of(StructurePoolElement.feature(var3), 4),
               Pair.of(StructurePoolElement.feature(var4), 2),
               Pair.of(StructurePoolElement.feature(var5), 4),
               Pair.of(StructurePoolElement.feature(var6), 1),
               Pair.of(StructurePoolElement.empty(), 4)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/taiga/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/villagers/baby"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "village/taiga/zombie/villagers",
         new StructureTemplatePool(
            var13,
            ImmutableList.of(
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/villagers/nitwit"), 1),
               Pair.of(StructurePoolElement.legacy("village/taiga/zombie/villagers/unemployed"), 10)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
