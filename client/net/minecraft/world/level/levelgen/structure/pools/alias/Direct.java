package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

record Direct(ResourceKey<StructureTemplatePool> c, ResourceKey<StructureTemplatePool> d) implements PoolAliasBinding {
   private final ResourceKey<StructureTemplatePool> alias;
   private final ResourceKey<StructureTemplatePool> target;
   static MapCodec<Direct> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("alias").forGetter(Direct::alias),
               ResourceKey.codec(Registries.TEMPLATE_POOL).fieldOf("target").forGetter(Direct::target)
            )
            .apply(var0, Direct::new)
   );

   Direct(ResourceKey<StructureTemplatePool> var1, ResourceKey<StructureTemplatePool> var2) {
      super();
      this.alias = var1;
      this.target = var2;
   }

   @Override
   public void forEachResolved(RandomSource var1, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> var2) {
      var2.accept(this.alias, this.target);
   }

   @Override
   public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
      return Stream.of(this.target);
   }

   @Override
   public MapCodec<Direct> codec() {
      return CODEC;
   }
}
