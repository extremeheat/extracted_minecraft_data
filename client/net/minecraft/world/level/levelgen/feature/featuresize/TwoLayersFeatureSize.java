package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class TwoLayersFeatureSize extends FeatureSize {
   public static final MapCodec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter((var0x) -> {
         return var0x.limit;
      }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((var0x) -> {
         return var0x.lowerSize;
      }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((var0x) -> {
         return var0x.upperSize;
      }), minClippedHeightCodec()).apply(var0, TwoLayersFeatureSize::new);
   });
   private final int limit;
   private final int lowerSize;
   private final int upperSize;

   public TwoLayersFeatureSize(int var1, int var2, int var3) {
      this(var1, var2, var3, OptionalInt.empty());
   }

   public TwoLayersFeatureSize(int var1, int var2, int var3, OptionalInt var4) {
      super(var4);
      this.limit = var1;
      this.lowerSize = var2;
      this.upperSize = var3;
   }

   protected FeatureSizeType<?> type() {
      return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
   }

   public int getSizeAtHeight(int var1, int var2) {
      return var2 < this.limit ? this.lowerSize : this.upperSize;
   }
}
