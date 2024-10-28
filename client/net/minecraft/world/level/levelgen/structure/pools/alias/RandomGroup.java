package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

record RandomGroup(SimpleWeightedRandomList<List<PoolAliasBinding>> groups) implements PoolAliasBinding {
   static MapCodec<RandomGroup> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SimpleWeightedRandomList.wrappedCodec(Codec.list(PoolAliasBinding.CODEC)).fieldOf("groups").forGetter(RandomGroup::groups)).apply(var0, RandomGroup::new);
   });

   RandomGroup(SimpleWeightedRandomList<List<PoolAliasBinding>> var1) {
      super();
      this.groups = var1;
   }

   public void forEachResolved(RandomSource var1, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> var2) {
      this.groups.getRandom(var1).ifPresent((var2x) -> {
         ((List)var2x.data()).forEach((var2xx) -> {
            var2xx.forEachResolved(var1, var2);
         });
      });
   }

   public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
      return this.groups.unwrap().stream().flatMap((var0) -> {
         return ((List)var0.data()).stream();
      }).flatMap(PoolAliasBinding::allTargets);
   }

   public MapCodec<RandomGroup> codec() {
      return CODEC;
   }

   public SimpleWeightedRandomList<List<PoolAliasBinding>> groups() {
      return this.groups;
   }
}
