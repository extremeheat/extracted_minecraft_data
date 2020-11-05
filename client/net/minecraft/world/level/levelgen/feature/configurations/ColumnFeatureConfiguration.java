package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.UniformInt;

public class ColumnFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<ColumnFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(UniformInt.codec(0, 2, 1).fieldOf("reach").forGetter((var0x) -> {
         return var0x.reach;
      }), UniformInt.codec(1, 5, 5).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, ColumnFeatureConfiguration::new);
   });
   private final UniformInt reach;
   private final UniformInt height;

   public ColumnFeatureConfiguration(UniformInt var1, UniformInt var2) {
      super();
      this.reach = var1;
      this.height = var2;
   }

   public UniformInt reach() {
      return this.reach;
   }

   public UniformInt height() {
      return this.height;
   }
}
