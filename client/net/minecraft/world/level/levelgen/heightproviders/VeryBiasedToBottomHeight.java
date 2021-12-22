package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VeryBiasedToBottomHeight extends HeightProvider {
   public static final Codec<VeryBiasedToBottomHeight> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      }), Codec.intRange(1, 2147483647).optionalFieldOf("inner", 1).forGetter((var0x) -> {
         return var0x.inner;
      })).apply(var0, VeryBiasedToBottomHeight::new);
   });
   private static final Logger LOGGER = LogManager.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int inner;

   private VeryBiasedToBottomHeight(VerticalAnchor var1, VerticalAnchor var2, int var3) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
      this.inner = var3;
   }

   // $FF: renamed from: of (net.minecraft.world.level.levelgen.VerticalAnchor, net.minecraft.world.level.levelgen.VerticalAnchor, int) net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight
   public static VeryBiasedToBottomHeight method_27(VerticalAnchor var0, VerticalAnchor var1, int var2) {
      return new VeryBiasedToBottomHeight(var0, var1, var2);
   }

   public int sample(Random var1, WorldGenerationContext var2) {
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

   public HeightProviderType<?> getType() {
      return HeightProviderType.VERY_BIASED_TO_BOTTOM;
   }

   public String toString() {
      return "biased[" + this.minInclusive + "-" + this.maxInclusive + " inner: " + this.inner + "]";
   }
}
