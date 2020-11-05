package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class FrequencyWithExtraChanceDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<FrequencyWithExtraChanceDecoratorConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("count").forGetter((var0x) -> {
         return var0x.count;
      }), Codec.FLOAT.fieldOf("extra_chance").forGetter((var0x) -> {
         return var0x.extraChance;
      }), Codec.INT.fieldOf("extra_count").forGetter((var0x) -> {
         return var0x.extraCount;
      })).apply(var0, FrequencyWithExtraChanceDecoratorConfiguration::new);
   });
   public final int count;
   public final float extraChance;
   public final int extraCount;

   public FrequencyWithExtraChanceDecoratorConfiguration(int var1, float var2, int var3) {
      super();
      this.count = var1;
      this.extraChance = var2;
      this.extraCount = var3;
   }
}
