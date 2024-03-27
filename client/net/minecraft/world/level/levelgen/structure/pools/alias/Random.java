package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

record Random(ResourceKey<StructureTemplatePool> c, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> d) implements PoolAliasBinding {
   private final ResourceKey<StructureTemplatePool> alias;
   private final SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> targets;
   static MapCodec<Random> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Random::alias),
               SimpleWeightedRandomList.wrappedCodec(ResourceKey.codec(Registries.TEMPLATE_POOL)).fieldOf("targets").forGetter(Random::targets)
            )
            .apply(var0, Random::new)
   );

   Random(ResourceKey<StructureTemplatePool> var1, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> var2) {
      super();
      this.alias = var1;
      this.targets = var2;
   }

   @Override
   public void forEachResolved(RandomSource var1, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> var2) {
      this.targets.getRandom(var1).ifPresent(var2x -> var2.accept(this.alias, var2x.data()));
   }

   @Override
   public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
      return this.targets.unwrap().stream().map(WeightedEntry.Wrapper::data);
   }

   @Override
   public MapCodec<Random> codec() {
      return CODEC;
   }
}
