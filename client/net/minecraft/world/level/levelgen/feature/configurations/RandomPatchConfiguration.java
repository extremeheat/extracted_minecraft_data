package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record RandomPatchConfiguration(int b, int c, int d, Supplier<PlacedFeature> e) implements FeatureConfiguration {
   private final int tries;
   private final int xzSpread;
   private final int ySpread;
   private final Supplier<PlacedFeature> feature;
   public static final Codec<RandomPatchConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchConfiguration::tries), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchConfiguration::xzSpread), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchConfiguration::ySpread), PlacedFeature.CODEC.fieldOf("feature").forGetter(RandomPatchConfiguration::feature)).apply(var0, RandomPatchConfiguration::new);
   });

   public RandomPatchConfiguration(int var1, int var2, int var3, Supplier<PlacedFeature> var4) {
      super();
      this.tries = var1;
      this.xzSpread = var2;
      this.ySpread = var3;
      this.feature = var4;
   }

   public int tries() {
      return this.tries;
   }

   public int xzSpread() {
      return this.xzSpread;
   }

   public int ySpread() {
      return this.ySpread;
   }

   public Supplier<PlacedFeature> feature() {
      return this.feature;
   }
}
