package net.minecraft.world.level.levelgen.structure.pools.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface PoolAliasBinding {
   Codec<PoolAliasBinding> CODEC = BuiltInRegistries.POOL_ALIAS_BINDING_TYPE.byNameCodec().dispatch(PoolAliasBinding::codec, Function.identity());

   void forEachResolved(RandomSource var1, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> var2);

   Stream<ResourceKey<StructureTemplatePool>> allTargets();

   static Direct direct(String var0, String var1) {
      return direct(Pools.createKey(var0), Pools.createKey(var1));
   }

   static Direct direct(ResourceKey<StructureTemplatePool> var0, ResourceKey<StructureTemplatePool> var1) {
      return new Direct(var0, var1);
   }

   static Random random(String var0, SimpleWeightedRandomList<String> var1) {
      SimpleWeightedRandomList.Builder var2 = SimpleWeightedRandomList.builder();
      var1.unwrap().forEach((var1x) -> {
         var2.add(Pools.createKey((String)var1x.data()), var1x.getWeight().asInt());
      });
      return random(Pools.createKey(var0), var2.build());
   }

   static Random random(ResourceKey<StructureTemplatePool> var0, SimpleWeightedRandomList<ResourceKey<StructureTemplatePool>> var1) {
      return new Random(var0, var1);
   }

   static RandomGroup randomGroup(SimpleWeightedRandomList<List<PoolAliasBinding>> var0) {
      return new RandomGroup(var0);
   }

   MapCodec<? extends PoolAliasBinding> codec();
}
