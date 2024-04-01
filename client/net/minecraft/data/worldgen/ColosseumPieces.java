package net.minecraft.data.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ColosseumPieces {
   public static final ResourceKey<StructureTemplatePool> START = Pools.createKey("colosseum/starts");

   public ColosseumPieces() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      var0.register(
         START,
         new StructureTemplatePool(
            var0.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY),
            List.of(
               Pair.of(
                  StructurePoolElement.single(
                     "colosseum/treasure/big_air_full", var0.lookup(Registries.PROCESSOR_LIST).getOrThrow(ProcessorLists.COLOSSEUM_VEINS)
                  ),
                  1
               )
            ),
            StructureTemplatePool.Projection.RIGID
         )
      );
      ColosseumPools.bootstrap(var0);
      ColosseumMobs.bootstrap(var0);
   }
}
