package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class ShipwreckConfiguration implements FeatureConfiguration {
   public static final Codec<ShipwreckConfiguration> CODEC;
   public final boolean isBeached;

   public ShipwreckConfiguration(boolean var1) {
      super();
      this.isBeached = var1;
   }

   static {
      CODEC = Codec.BOOL.fieldOf("is_beached").orElse(false).xmap(ShipwreckConfiguration::new, (var0) -> {
         return var0.isBeached;
      }).codec();
   }
}
