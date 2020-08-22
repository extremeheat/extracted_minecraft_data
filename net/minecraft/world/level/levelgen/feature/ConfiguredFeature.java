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
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature {
   public static final Logger LOGGER = LogManager.getLogger();
   public final Feature feature;
   public final FeatureConfiguration config;

   public ConfiguredFeature(Feature var1, FeatureConfiguration var2) {
      this.feature = var1;
      this.config = var2;
   }

   public ConfiguredFeature(Feature var1, Dynamic var2) {
      this(var1, var1.createSettings(var2));
   }

   public ConfiguredFeature decorated(ConfiguredDecorator var1) {
      Feature var2 = this.feature instanceof AbstractFlowerFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
      return var2.configured(new DecoratedFeatureConfiguration(this, var1));
   }

   public WeightedConfiguredFeature weighted(float var1) {
      return new WeightedConfiguredFeature(this, var1);
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("name"), var1.createString(Registry.FEATURE.getKey(this.feature).toString()), var1.createString("config"), this.config.serialize(var1).getValue())));
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4) {
      return this.feature.place(var1, var2, var3, var4, this.config);
   }

   public static ConfiguredFeature deserialize(Dynamic var0) {
      String var1 = var0.get("name").asString("");
      Feature var2 = (Feature)Registry.FEATURE.get(new ResourceLocation(var1));

      try {
         return new ConfiguredFeature(var2, var0.get("config").orElseEmptyMap());
      } catch (RuntimeException var4) {
         LOGGER.warn("Error while deserializing {}", var1);
         return new ConfiguredFeature(Feature.NO_OP, NoneFeatureConfiguration.NONE);
      }
   }
}
