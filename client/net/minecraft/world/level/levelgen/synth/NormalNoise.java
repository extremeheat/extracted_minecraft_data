package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.RandomSource;

public class NormalNoise {
   private static final double INPUT_FACTOR = 1.0181268882175227D;
   private static final double TARGET_DEVIATION = 0.3333333333333333D;
   private final double valueFactor;
   private final PerlinNoise first;
   private final PerlinNoise second;

   /** @deprecated */
   @Deprecated
   public static NormalNoise createLegacyNetherBiome(RandomSource var0, NormalNoise.NoiseParameters var1) {
      return new NormalNoise(var0, var1.firstOctave(), var1.amplitudes(), false);
   }

   public static NormalNoise create(RandomSource var0, int var1, double... var2) {
      return new NormalNoise(var0, var1, new DoubleArrayList(var2), true);
   }

   public static NormalNoise create(RandomSource var0, NormalNoise.NoiseParameters var1) {
      return new NormalNoise(var0, var1.firstOctave(), var1.amplitudes(), true);
   }

   public static NormalNoise create(RandomSource var0, int var1, DoubleList var2) {
      return new NormalNoise(var0, var1, var2, true);
   }

   private NormalNoise(RandomSource var1, int var2, DoubleList var3, boolean var4) {
      super();
      if (var4) {
         this.first = PerlinNoise.create(var1, var2, var3);
         this.second = PerlinNoise.create(var1, var2, var3);
      } else {
         this.first = PerlinNoise.createLegacyForLegacyNormalNoise(var1, var2, var3);
         this.second = PerlinNoise.createLegacyForLegacyNormalNoise(var1, var2, var3);
      }

      int var5 = 2147483647;
      int var6 = -2147483648;
      DoubleListIterator var7 = var3.iterator();

      while(var7.hasNext()) {
         int var8 = var7.nextIndex();
         double var9 = var7.nextDouble();
         if (var9 != 0.0D) {
            var5 = Math.min(var5, var8);
            var6 = Math.max(var6, var8);
         }
      }

      this.valueFactor = 0.16666666666666666D / expectedDeviation(var6 - var5);
   }

   private static double expectedDeviation(int var0) {
      return 0.1D * (1.0D + 1.0D / (double)(var0 + 1));
   }

   public double getValue(double var1, double var3, double var5) {
      double var7 = var1 * 1.0181268882175227D;
      double var9 = var3 * 1.0181268882175227D;
      double var11 = var5 * 1.0181268882175227D;
      return (this.first.getValue(var1, var3, var5) + this.second.getValue(var7, var9, var11)) * this.valueFactor;
   }

   public NormalNoise.NoiseParameters parameters() {
      return new NormalNoise.NoiseParameters(this.first.firstOctave(), this.first.amplitudes());
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder var1) {
      var1.append("NormalNoise {");
      var1.append("first: ");
      this.first.parityConfigString(var1);
      var1.append(", second: ");
      this.second.parityConfigString(var1);
      var1.append("}");
   }

   public static class NoiseParameters {
      private final int firstOctave;
      private final DoubleList amplitudes;
      public static final Codec<NormalNoise.NoiseParameters> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.INT.fieldOf("firstOctave").forGetter(NormalNoise.NoiseParameters::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NormalNoise.NoiseParameters::amplitudes)).apply(var0, NormalNoise.NoiseParameters::new);
      });
      public static final Codec<Supplier<NormalNoise.NoiseParameters>> CODEC;

      public NoiseParameters(int var1, List<Double> var2) {
         super();
         this.firstOctave = var1;
         this.amplitudes = new DoubleArrayList(var2);
      }

      public NoiseParameters(int var1, double var2, double... var4) {
         super();
         this.firstOctave = var1;
         this.amplitudes = new DoubleArrayList(var4);
         this.amplitudes.add(0, var2);
      }

      public int firstOctave() {
         return this.firstOctave;
      }

      public DoubleList amplitudes() {
         return this.amplitudes;
      }

      static {
         CODEC = RegistryFileCodec.create(Registry.NOISE_REGISTRY, DIRECT_CODEC);
      }
   }
}
