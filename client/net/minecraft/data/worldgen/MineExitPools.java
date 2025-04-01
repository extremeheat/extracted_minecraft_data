package net.minecraft.data.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class MineExitPools {
   public static final ResourceKey<StructureTemplatePool> ROOT = Pools.createKey("mine_exits/root");
   public static final ResourceKey<StructureTemplatePool> STARTS = Pools.createKey("mine_exits/starts");

   public MineExitPools() {
      super();
   }

   public static void bootstrap(BootstrapContext<StructureTemplatePool> var0) {
      HolderGetter var1 = var0.lookup(Registries.TEMPLATE_POOL);
      Holder.Reference var2 = var1.getOrThrow(Pools.EMPTY);
      var0.register(ROOT, new StructureTemplatePool(var2, List.of(Pair.of(StructurePoolElement.single("mine_exits/root"), 1)), StructureTemplatePool.Projection.RIGID));
      var0.register(STARTS, new StructureTemplatePool(var2, List.of(Pair.of(StructurePoolElement.single("mine_exits/exit_01"), 1)), StructureTemplatePool.Projection.RIGID));
   }
}
