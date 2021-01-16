package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.UniformInt;

public class CountConfiguration implements DecoratorConfiguration, FeatureConfiguration {
   public static final Codec<CountConfiguration> CODEC = UniformInt.codec(-10, 128, 128).fieldOf("count").xmap(CountConfiguration::new, CountConfiguration::count).codec();
   private final UniformInt count;

   public CountConfiguration(int var1) {
      super();
      this.count = UniformInt.fixed(var1);
   }

   public CountConfiguration(UniformInt var1) {
      super();
      this.count = var1;
   }

   public UniformInt count() {
      return this.count;
   }
}
