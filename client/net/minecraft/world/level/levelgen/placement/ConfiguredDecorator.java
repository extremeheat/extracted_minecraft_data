package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.Decoratable;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class ConfiguredDecorator<DC extends DecoratorConfiguration> implements Decoratable<ConfiguredDecorator<?>> {
   public static final Codec<ConfiguredDecorator<?>> CODEC;
   private final FeatureDecorator<DC> decorator;
   private final DC config;

   public ConfiguredDecorator(FeatureDecorator<DC> var1, DC var2) {
      super();
      this.decorator = var1;
      this.config = var2;
   }

   public Stream<BlockPos> getPositions(DecorationContext var1, Random var2, BlockPos var3) {
      return this.decorator.getPositions(var1, var2, this.config, var3);
   }

   public String toString() {
      return String.format("[%s %s]", Registry.DECORATOR.getKey(this.decorator), this.config);
   }

   public ConfiguredDecorator<?> decorated(ConfiguredDecorator<?> var1) {
      return new ConfiguredDecorator(FeatureDecorator.DECORATED, new DecoratedDecoratorConfiguration(var1, this));
   }

   public DC config() {
      return this.config;
   }

   // $FF: synthetic method
   public Object decorated(ConfiguredDecorator var1) {
      return this.decorated(var1);
   }

   static {
      CODEC = Registry.DECORATOR.dispatch("type", (var0) -> {
         return var0.decorator;
      }, FeatureDecorator::configuredCodec);
   }
}
