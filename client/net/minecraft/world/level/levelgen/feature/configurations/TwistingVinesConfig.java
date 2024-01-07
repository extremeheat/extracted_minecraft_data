package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.ExtraCodecs;

public record TwistingVinesConfig(int b, int c, int d) implements FeatureConfiguration {
   private final int spreadWidth;
   private final int spreadHeight;
   private final int maxHeight;
   public static final Codec<TwistingVinesConfig> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesConfig::spreadWidth),
               ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesConfig::spreadHeight),
               ExtraCodecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesConfig::maxHeight)
            )
            .apply(var0, TwistingVinesConfig::new)
   );

   public TwistingVinesConfig(int var1, int var2, int var3) {
      super();
      this.spreadWidth = var1;
      this.spreadHeight = var2;
      this.maxHeight = var3;
   }
}
