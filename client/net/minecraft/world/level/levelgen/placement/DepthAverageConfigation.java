package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class DepthAverageConfigation implements DecoratorConfiguration {
   public static final Codec<DepthAverageConfigation> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("baseline").forGetter((var0x) -> {
         return var0x.baseline;
      }), Codec.INT.fieldOf("spread").forGetter((var0x) -> {
         return var0x.spread;
      })).apply(var0, DepthAverageConfigation::new);
   });
   public final int baseline;
   public final int spread;

   public DepthAverageConfigation(int var1, int var2) {
      super();
      this.baseline = var1;
      this.spread = var2;
   }
}
