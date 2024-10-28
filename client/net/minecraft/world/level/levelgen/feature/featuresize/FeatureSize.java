package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.registries.BuiltInRegistries;

public abstract class FeatureSize {
   public static final Codec<FeatureSize> CODEC;
   protected static final int MAX_WIDTH = 16;
   protected final OptionalInt minClippedHeight;

   protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> minClippedHeightCodec() {
      return Codec.intRange(0, 80).optionalFieldOf("min_clipped_height").xmap((var0) -> {
         return (OptionalInt)var0.map(OptionalInt::of).orElse(OptionalInt.empty());
      }, (var0) -> {
         return var0.isPresent() ? Optional.of(var0.getAsInt()) : Optional.empty();
      }).forGetter((var0) -> {
         return var0.minClippedHeight;
      });
   }

   public FeatureSize(OptionalInt var1) {
      super();
      this.minClippedHeight = var1;
   }

   protected abstract FeatureSizeType<?> type();

   public abstract int getSizeAtHeight(int var1, int var2);

   public OptionalInt minClippedHeight() {
      return this.minClippedHeight;
   }

   static {
      CODEC = BuiltInRegistries.FEATURE_SIZE_TYPE.byNameCodec().dispatch(FeatureSize::type, FeatureSizeType::codec);
   }
}
