package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;

public class DecoratedFeatureConfiguration implements FeatureConfiguration {
   public final ConfiguredFeature feature;
   public final ConfiguredDecorator decorator;

   public DecoratedFeatureConfiguration(ConfiguredFeature var1, ConfiguredDecorator var2) {
      this.feature = var1;
      this.decorator = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("feature"), this.feature.serialize(var1).getValue(), var1.createString("decorator"), this.decorator.serialize(var1).getValue())));
   }

   public String toString() {
      return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this.feature.feature), Registry.DECORATOR.getKey(this.decorator.decorator));
   }

   public static DecoratedFeatureConfiguration deserialize(Dynamic var0) {
      ConfiguredFeature var1 = ConfiguredFeature.deserialize(var0.get("feature").orElseEmptyMap());
      ConfiguredDecorator var2 = ConfiguredDecorator.deserialize(var0.get("decorator").orElseEmptyMap());
      return new DecoratedFeatureConfiguration(var1, var2);
   }
}
