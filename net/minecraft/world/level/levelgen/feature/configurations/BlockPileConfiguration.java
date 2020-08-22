package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class BlockPileConfiguration implements FeatureConfiguration {
   public final BlockStateProvider stateProvider;

   public BlockPileConfiguration(BlockStateProvider var1) {
      this.stateProvider = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("state_provider"), this.stateProvider.serialize(var1));
      return new Dynamic(var1, var1.createMap(var2.build()));
   }

   public static BlockPileConfiguration deserialize(Dynamic var0) {
      BlockStateProviderType var1 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new BlockPileConfiguration(var1.deserialize(var0.get("state_provider").orElseEmptyMap()));
   }
}
