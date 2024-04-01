package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ColosseumPools {
   public ColosseumPools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      Holder.Reference var1 = var0.lookup(Registries.PROCESSOR_LIST).getOrThrow(ProcessorLists.COLOSSEUM_VEINS);
      Holder.Reference var2 = var0.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
      Pools.register(
         var0,
         "colosseum/treasure/bases",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/treasure/bases/lava_basin", var1), 1)),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/stairs",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/treasure/stairs/lower_stairs", var1), 1)),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/bases/centers",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/bases/centers/center_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/bases/centers/center_1", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/bases/centers/center_2", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/bases/centers/center_3", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/brains",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/treasure/brains/center_brain", var1), 1)),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/walls",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/lava_wall", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/entrance_wall", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/walls/outer",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/top_corner", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/mid_corner", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/bottom_corner", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/outer_wall", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/medium_outer_wall", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/outer/tall_outer_wall", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/walls/bottom",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/bottom/wall_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/bottom/wall_1", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/bottom/wall_2", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/bottom/wall_3", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/walls/mid",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/mid/wall_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/mid/wall_1", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/mid/wall_2", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/walls/top",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/top/main_entrance", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/top/wall_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/walls/top/wall_1", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/connectors",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/connectors/center_to_wall_middle", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/connectors/center_to_wall_top", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/connectors/center_to_wall_top_entrance", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/corners/bottom",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/bottom/corner_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/bottom/corner_1", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/corners/edges",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/edges/bottom", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/edges/middle", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/edges/top", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/corners/middle",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/middle/corner_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/middle/corner_1", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/corners/top",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/top/corner_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/corners/top/corner_1", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/extensions/large_pool",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/empty", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/empty", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/fire_room", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/large_bridge_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/large_bridge_1", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/large_bridge_2", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/large_bridge_3", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/roofed_bridge", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/empty", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/extensions/small_pool",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/empty", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/fire_room", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/empty", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/small_bridge_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/small_bridge_1", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/small_bridge_2", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/small_bridge_3", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/extensions/houses",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/house_0", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/extensions/house_1", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/treasure/roofs",
         new StructureTemplatePool(
            var2,
            ImmutableList.of(
               Pair.of(StructurePoolElement.single("colosseum/treasure/roofs/wall_roof", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/roofs/corner_roof", var1), 1),
               Pair.of(StructurePoolElement.single("colosseum/treasure/roofs/center_roof", var1), 1)
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
