package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class ConfiguredFeature<FC extends FeatureConfiguration> {
   public final Feature<FC> feature;
   public final FC config;

   public ConfiguredFeature(Feature<FC> var1, FC var2) {
      super();
      this.feature = var1;
      this.config = var2;
   }

   public ConfiguredFeature(Feature<FC> var1, Dynamic<?> var2) {
      this(var1, var1.createSettings(var2));
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("name"), var1.createString(Registry.FEATURE.getKey(this.feature).toString()), var1.createString("config"), this.config.serialize(var1).getValue())));
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4) {
      return this.feature.place(var1, var2, var3, var4, this.config);
   }

   public static <T> ConfiguredFeature<?> deserialize(Dynamic<T> var0) {
      Feature var1 = (Feature)Registry.FEATURE.get(new ResourceLocation(var0.get("name").asString("")));
      return new ConfiguredFeature(var1, var0.get("config").orElseEmptyMap());
   }
}
