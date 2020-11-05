package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RangeDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<RangeDecoratorConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter((var0x) -> {
         return var0x.bottomOffset;
      }), Codec.INT.fieldOf("top_offset").orElse(0).forGetter((var0x) -> {
         return var0x.topOffset;
      }), Codec.INT.fieldOf("maximum").orElse(0).forGetter((var0x) -> {
         return var0x.maximum;
      })).apply(var0, RangeDecoratorConfiguration::new);
   });
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public RangeDecoratorConfiguration(int var1, int var2, int var3) {
      super();
      this.bottomOffset = var1;
      this.topOffset = var2;
      this.maximum = var3;
   }
}
