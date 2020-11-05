package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;

public class NoneCarverConfiguration implements CarverConfiguration {
   public static final Codec<NoneCarverConfiguration> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final NoneCarverConfiguration INSTANCE = new NoneCarverConfiguration();

   public NoneCarverConfiguration() {
      super();
   }
}
