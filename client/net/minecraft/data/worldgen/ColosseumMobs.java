package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ColosseumMobs {
   public ColosseumMobs() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      Holder.Reference var1 = var0.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
      Pools.register(
         var0,
         "colosseum/mobs/toxifin",
         new StructureTemplatePool(
            var1, ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/mobs/toxifin"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/mobs/plaguewhale",
         new StructureTemplatePool(
            var1, ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/mobs/plaguewhale"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
      Pools.register(
         var0,
         "colosseum/mobs/mega_spud",
         new StructureTemplatePool(
            var1, ImmutableList.of(Pair.of(StructurePoolElement.single("colosseum/mobs/mega_spud"), 1)), StructureTemplatePool.Projection.RIGID
         )
      );
   }
}
