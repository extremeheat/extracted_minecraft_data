package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class CountConfiguration implements FeatureConfiguration {
   public static final Codec<CountConfiguration> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountConfiguration::new, CountConfiguration::count).codec();
   private final IntProvider count;

   public CountConfiguration(int var1) {
      super();
      this.count = ConstantInt.of(var1);
   }

   public CountConfiguration(IntProvider var1) {
      super();
      this.count = var1;
   }

   public IntProvider count() {
      return this.count;
   }
}
