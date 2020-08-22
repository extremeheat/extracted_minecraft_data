package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class TreeConfiguration implements FeatureConfiguration {
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider leavesProvider;
   public final List decorators;
   public final int baseHeight;
   public transient boolean fromSapling;

   protected TreeConfiguration(BlockStateProvider var1, BlockStateProvider var2, List var3, int var4) {
      this.trunkProvider = var1;
      this.leavesProvider = var2;
      this.decorators = var3;
      this.baseHeight = var4;
   }

   public void setFromSapling() {
      this.fromSapling = true;
   }

   public Dynamic serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("trunk_provider"), this.trunkProvider.serialize(var1)).put(var1.createString("leaves_provider"), this.leavesProvider.serialize(var1)).put(var1.createString("decorators"), var1.createList(this.decorators.stream().map((var1x) -> {
         return var1x.serialize(var1);
      }))).put(var1.createString("base_height"), var1.createInt(this.baseHeight));
      return new Dynamic(var1, var1.createMap(var2.build()));
   }

   public static TreeConfiguration deserialize(Dynamic var0) {
      BlockStateProviderType var1 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType var2 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new TreeConfiguration(var1.deserialize(var0.get("trunk_provider").orElseEmptyMap()), var2.deserialize(var0.get("leaves_provider").orElseEmptyMap()), var0.get("decorators").asList((var0x) -> {
         return ((TreeDecoratorType)Registry.TREE_DECORATOR_TYPES.get(new ResourceLocation((String)var0x.get("type").asString().orElseThrow(RuntimeException::new)))).deserialize(var0x);
      }), var0.get("base_height").asInt(0));
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      public final BlockStateProvider leavesProvider;
      private List decorators = Lists.newArrayList();
      private int baseHeight = 0;

      public TreeConfigurationBuilder(BlockStateProvider var1, BlockStateProvider var2) {
         this.trunkProvider = var1;
         this.leavesProvider = var2;
      }

      public TreeConfiguration.TreeConfigurationBuilder baseHeight(int var1) {
         this.baseHeight = var1;
         return this;
      }

      public TreeConfiguration build() {
         return new TreeConfiguration(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight);
      }
   }
}
