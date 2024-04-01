package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class BiasedToBottomHeight extends HeightProvider {
   public static final Codec<BiasedToBottomHeight> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(var0x -> var0x.minInclusive),
               VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(var0x -> var0x.maxInclusive),
               Codec.intRange(1, 2147483647).optionalFieldOf("inner", 1).forGetter(var0x -> var0x.inner)
            )
            .apply(var0, BiasedToBottomHeight::new)
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int inner;

   private BiasedToBottomHeight(VerticalAnchor var1, VerticalAnchor var2, int var3) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
      this.inner = var3;
   }

   public static BiasedToBottomHeight of(VerticalAnchor var0, VerticalAnchor var1, int var2) {
      return new BiasedToBottomHeight(var0, var1, var2);
   }

   @Override
   public int sample(RandomSource var1, WorldGenerationContext var2) {
      int var3 = this.minInclusive.resolveY(var2);
      int var4 = this.maxInclusive.resolveY(var2);
      if (var4 - var3 - this.inner + 1 <= 0) {
         LOGGER.warn("Empty height range: {}", this);
         return var3;
      } else {
         int var5 = var1.nextInt(var4 - var3 - this.inner + 1);
         return var1.nextInt(var5 + this.inner) + var3;
      }
   }

   @Override
   public HeightProviderType<?> getType() {
      return HeightProviderType.BIASED_TO_BOTTOM;
   }

   @Override
   public String toString() {
      return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
   }
}
