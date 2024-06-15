package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class TrapezoidHeight extends HeightProvider {
   public static final MapCodec<TrapezoidHeight> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter(var0x -> var0x.minInclusive),
               VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter(var0x -> var0x.maxInclusive),
               Codec.INT.optionalFieldOf("plateau", 0).forGetter(var0x -> var0x.plateau)
            )
            .apply(var0, TrapezoidHeight::new)
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final int plateau;

   private TrapezoidHeight(VerticalAnchor var1, VerticalAnchor var2, int var3) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
      this.plateau = var3;
   }

   public static TrapezoidHeight of(VerticalAnchor var0, VerticalAnchor var1, int var2) {
      return new TrapezoidHeight(var0, var1, var2);
   }

   public static TrapezoidHeight of(VerticalAnchor var0, VerticalAnchor var1) {
      return of(var0, var1, 0);
   }

   @Override
   public int sample(RandomSource var1, WorldGenerationContext var2) {
      int var3 = this.minInclusive.resolveY(var2);
      int var4 = this.maxInclusive.resolveY(var2);
      if (var3 > var4) {
         LOGGER.warn("Empty height range: {}", this);
         return var3;
      } else {
         int var5 = var4 - var3;
         if (this.plateau >= var5) {
            return Mth.randomBetweenInclusive(var1, var3, var4);
         } else {
            int var6 = (var5 - this.plateau) / 2;
            int var7 = var5 - var6;
            return var3 + Mth.randomBetweenInclusive(var1, 0, var7) + Mth.randomBetweenInclusive(var1, 0, var6);
         }
      }
   }

   @Override
   public HeightProviderType<?> getType() {
      return HeightProviderType.TRAPEZOID;
   }

   @Override
   public String toString() {
      return this.plateau == 0
         ? "triangle (" + this.minInclusive + "-" + this.maxInclusive + ")"
         : "trapezoid(" + this.plateau + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
