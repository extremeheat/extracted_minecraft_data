package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class PoolAliasBindings {
   public PoolAliasBindings() {
      super();
   }

   public static Codec<? extends PoolAliasBinding> bootstrap(Registry<Codec<? extends PoolAliasBinding>> var0) {
      Registry.register(var0, "random", Random.CODEC);
      Registry.register(var0, "random_group", RandomGroup.CODEC);
      return Registry.register(var0, "direct", Direct.CODEC);
   }

   public static void registerTargetsAsPools(BootstrapContext<StructureTemplatePool> var0, Holder<StructureTemplatePool> var1, List<PoolAliasBinding> var2) {
      var2.stream()
         .flatMap(PoolAliasBinding::allTargets)
         .map(var0x -> var0x.location().getPath())
         .forEach(
            var2x -> Pools.register(
                  var0, var2x, new StructureTemplatePool(var1, List.of(Pair.of(StructurePoolElement.single(var2x), 1)), StructureTemplatePool.Projection.RIGID)
               )
         );
   }
}
