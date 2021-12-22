package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;

public class ColumnFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<ColumnFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(IntProvider.codec(0, 3).fieldOf("reach").forGetter((var0x) -> {
         return var0x.reach;
      }), IntProvider.codec(1, 10).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      })).apply(var0, ColumnFeatureConfiguration::new);
   });
   private final IntProvider reach;
   private final IntProvider height;

   public ColumnFeatureConfiguration(IntProvider var1, IntProvider var2) {
      super();
      this.reach = var1;
      this.height = var2;
   }

   public IntProvider reach() {
      return this.reach;
   }

   public IntProvider height() {
      return this.height;
   }
}
