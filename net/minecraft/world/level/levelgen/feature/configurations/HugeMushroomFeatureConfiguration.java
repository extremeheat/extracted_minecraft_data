package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

public class HugeMushroomFeatureConfiguration implements FeatureConfiguration {
   public final BlockStateProvider capProvider;
   public final BlockStateProvider stemProvider;
   public final int foliageRadius;

   public HugeMushroomFeatureConfiguration(BlockStateProvider var1, BlockStateProvider var2, int var3) {
      this.capProvider = var1;
      this.stemProvider = var2;
      this.foliageRadius = var3;
   }

   public Dynamic serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("cap_provider"), this.capProvider.serialize(var1)).put(var1.createString("stem_provider"), this.stemProvider.serialize(var1)).put(var1.createString("foliage_radius"), var1.createInt(this.foliageRadius));
      return new Dynamic(var1, var1.createMap(var2.build()));
   }

   public static HugeMushroomFeatureConfiguration deserialize(Dynamic var0) {
      BlockStateProviderType var1 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      BlockStateProviderType var2 = (BlockStateProviderType)Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation((String)var0.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
      return new HugeMushroomFeatureConfiguration(var1.deserialize(var0.get("cap_provider").orElseEmptyMap()), var2.deserialize(var0.get("stem_provider").orElseEmptyMap()), var0.get("foliage_radius").asInt(2));
   }
}
