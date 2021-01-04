package net.minecraft.world.level.levelgen.placement;

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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class ConfiguredDecorator<DC extends DecoratorConfiguration> {
   public final FeatureDecorator<DC> decorator;
   public final DC config;

   public ConfiguredDecorator(FeatureDecorator<DC> var1, Dynamic<?> var2) {
      this(var1, var1.createSettings(var2));
   }

   public ConfiguredDecorator(FeatureDecorator<DC> var1, DC var2) {
      super();
      this.decorator = var1;
      this.config = var2;
   }

   public <FC extends FeatureConfiguration> boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, ConfiguredFeature<FC> var5) {
      return this.decorator.placeFeature(var1, var2, var3, var4, this.config, var5);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("name"), var1.createString(Registry.DECORATOR.getKey(this.decorator).toString()), var1.createString("config"), this.config.serialize(var1).getValue())));
   }

   public static <T> ConfiguredDecorator<?> deserialize(Dynamic<T> var0) {
      FeatureDecorator var1 = (FeatureDecorator)Registry.DECORATOR.get(new ResourceLocation(var0.get("name").asString("")));
      return new ConfiguredDecorator(var1, var0.get("config").orElseEmptyMap());
   }
}
