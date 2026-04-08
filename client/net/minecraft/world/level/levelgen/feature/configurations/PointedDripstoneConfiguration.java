package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PointedDripstoneConfiguration implements FeatureConfiguration {
   public static final Codec<PointedDripstoneConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_taller_dripstone").orElse(0.2F).forGetter((c) -> c.chanceOfTallerDripstone), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_directional_spread").orElse(0.7F).forGetter((c) -> c.chanceOfDirectionalSpread), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius2").orElse(0.5F).forGetter((c) -> c.chanceOfSpreadRadius2), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius3").orElse(0.5F).forGetter((c) -> c.chanceOfSpreadRadius3)).apply(i, PointedDripstoneConfiguration::new));
   public final float chanceOfTallerDripstone;
   public final float chanceOfDirectionalSpread;
   public final float chanceOfSpreadRadius2;
   public final float chanceOfSpreadRadius3;

   public PointedDripstoneConfiguration(final float chanceOfTallerDripstone, final float chanceOfDirectionalSpread, final float chanceOfSpreadRadius2, final float chanceOfSpreadRadius3) {
      super();
      this.chanceOfTallerDripstone = chanceOfTallerDripstone;
      this.chanceOfDirectionalSpread = chanceOfDirectionalSpread;
      this.chanceOfSpreadRadius2 = chanceOfSpreadRadius2;
      this.chanceOfSpreadRadius3 = chanceOfSpreadRadius3;
   }
}
