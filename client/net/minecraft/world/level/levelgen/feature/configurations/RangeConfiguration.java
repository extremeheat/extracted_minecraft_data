package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class RangeConfiguration implements FeatureConfiguration {
   public static final Codec<RangeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(HeightProvider.CODEC.fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, RangeConfiguration::new);
   });
   public final HeightProvider height;

   public RangeConfiguration(HeightProvider var1) {
      super();
      this.height = var1;
   }
}
