package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class ThreeLayersFeatureSize extends FeatureSize {
   public static final MapCodec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter((var0x) -> {
         return var0x.limit;
      }), Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter((var0x) -> {
         return var0x.upperLimit;
      }), Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter((var0x) -> {
         return var0x.lowerSize;
      }), Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter((var0x) -> {
         return var0x.middleSize;
      }), Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter((var0x) -> {
         return var0x.upperSize;
      }), minClippedHeightCodec()).apply(var0, ThreeLayersFeatureSize::new);
   });
   private final int limit;
   private final int upperLimit;
   private final int lowerSize;
   private final int middleSize;
   private final int upperSize;

   public ThreeLayersFeatureSize(int var1, int var2, int var3, int var4, int var5, OptionalInt var6) {
      super(var6);
      this.limit = var1;
      this.upperLimit = var2;
      this.lowerSize = var3;
      this.middleSize = var4;
      this.upperSize = var5;
   }

   protected FeatureSizeType<?> type() {
      return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
   }

   public int getSizeAtHeight(int var1, int var2) {
      if (var2 < this.limit) {
         return this.lowerSize;
      } else {
         return var2 >= var1 - this.upperLimit ? this.upperSize : this.middleSize;
      }
   }
}
