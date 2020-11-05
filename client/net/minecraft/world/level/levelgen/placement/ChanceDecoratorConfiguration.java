package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class ChanceDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<ChanceDecoratorConfiguration> CODEC;
   public final int chance;

   public ChanceDecoratorConfiguration(int var1) {
      super();
      this.chance = var1;
   }

   static {
      CODEC = Codec.INT.fieldOf("chance").xmap(ChanceDecoratorConfiguration::new, (var0) -> {
         return var0.chance;
      }).codec();
   }
}
