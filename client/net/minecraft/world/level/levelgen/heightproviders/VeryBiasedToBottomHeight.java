package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class VeryBiasedToBottomHeight extends HeightProvider {
   public static final Codec<VeryBiasedToBottomHeight> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(var0x -> var0x.minInclusive),
               VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(var0x -> var0x.maxInclusive),
               Codec.intRange(1, 2147483647).optionalFieldOf("inner", 1).forGetter(var0x -> var0x.inner)
            )
            .apply(var0, VeryBiasedToBottomHeight::new)
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int inner;

   private VeryBiasedToBottomHeight(VerticalAnchor var1, VerticalAnchor var2, int var3) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
      this.inner = var3;
   }

   public static VeryBiasedToBottomHeight of(VerticalAnchor var0, VerticalAnchor var1, int var2) {
      return new VeryBiasedToBottomHeight(var0, var1, var2);
   }

   @Override
   public int sample(RandomSource var1, WorldGenerationContext var2) {
      int var3 = this.minInclusive.resolveY(var2);
      int var4 = this.maxInclusive.resolveY(var2);
      if (var4 - var3 - this.inner + 1 <= 0) {
         LOGGER.warn("Empty height range: {}", this);
         return var3;
      } else {
         int var5 = Mth.nextInt(var1, var3 + this.inner, var4);
         int var6 = Mth.nextInt(var1, var3, var5 - 1);
         return Mth.nextInt(var1, var3, var6 - 1 + this.inner);
      }
   }

   @Override
   public HeightProviderType<?> getType() {
      return HeightProviderType.VERY_BIASED_TO_BOTTOM;
   }

   @Override
   public String toString() {
      return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
   }
}
