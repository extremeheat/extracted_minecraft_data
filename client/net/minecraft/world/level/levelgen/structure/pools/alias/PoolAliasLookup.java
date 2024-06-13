package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@FunctionalInterface
public interface PoolAliasLookup {
   PoolAliasLookup EMPTY = var0 -> var0;

   ResourceKey<StructureTemplatePool> lookup(ResourceKey<StructureTemplatePool> var1);

   static PoolAliasLookup create(List<PoolAliasBinding> var0, BlockPos var1, long var2) {
      if (var0.isEmpty()) {
         return EMPTY;
      } else {
         RandomSource var4 = RandomSource.create(var2).forkPositional().at(var1);
         Builder var5 = ImmutableMap.builder();
         var0.forEach(var2x -> var2x.forEachResolved(var4, var5::put));
         ImmutableMap var6 = var5.build();
         return var1x -> Objects.requireNonNull(var6.getOrDefault(var1x, var1x), () -> "alias " + var1x + " was mapped to null value");
      }
   }
}