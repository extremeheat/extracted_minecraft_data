package net.minecraft.data.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class TrailRuinsStructurePools {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("trail_ruins/tower");

   public TrailRuinsStructurePools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var2 = var1.getOrThrow(Pools.EMPTY);
      HolderGetter var3 = var0.lookup(Registries.PROCESSOR_LIST);
      Holder.Reference var4 = var3.getOrThrow(ProcessorLists.TRAIL_RUINS_HOUSES_ARCHAEOLOGY);
      Holder.Reference var5 = var3.getOrThrow(ProcessorLists.TRAIL_RUINS_ROADS_ARCHAEOLOGY);
      Holder.Reference var6 = var3.getOrThrow(ProcessorLists.TRAIL_RUINS_TOWER_TOP_ARCHAEOLOGY);
      var0.register(
         START,
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_5", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/tower/tower_top",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_top_1", var6), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_top_2", var6), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_top_3", var6), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_top_4", var6), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/tower_top_5", var6), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/tower/additions",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/tower/hall_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/hall_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/hall_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/hall_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/hall_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/large_hall_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/large_hall_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/large_hall_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/large_hall_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/large_hall_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/one_room_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/one_room_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/one_room_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/one_room_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/one_room_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/platform_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/platform_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/platform_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/platform_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/platform_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/stable_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/stable_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/stable_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/stable_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/tower/stable_5", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/roads",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/roads/long_road_end", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_end_1", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_section_1", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_section_2", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_section_3", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_section_4", var5), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/roads/road_spacer_1", var5), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/buildings",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_hall_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_hall_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_hall_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_hall_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_hall_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/large_room_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/large_room_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/large_room_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/large_room_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/large_room_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/one_room_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/one_room_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/one_room_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/one_room_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/one_room_5", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/buildings/grouped",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_full_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_full_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_full_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_full_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_full_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_lower_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_lower_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_lower_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_lower_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_lower_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_upper_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_upper_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_upper_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_upper_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_upper_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_room_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_room_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_room_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_room_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/buildings/group_room_5", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "trail_ruins/decor",
         new StructureTemplatePool(
            var2,
            List.of(
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_1", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_2", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_3", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_4", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_5", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_6", var4), 1),
               Pair.of(StructurePoolElement.single("trail_ruins/decor/decor_7", var4), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
