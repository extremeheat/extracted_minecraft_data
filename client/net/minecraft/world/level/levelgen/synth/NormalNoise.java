package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public final class NormalNoise {
   private static final double SAMPLE_COUNT = 2.0;
   private static final double INPUT_FACTOR = 1.0181268882175227;
   @VisibleForTesting
   public static final double TARGET_DEVIATION = 0.3333333333333333;
   private static final int MAX_OCTAVE_COUNT = 32;
   private static final double MAX_AMPLITUDE = 1000000.0;
   private static final double PERSISTENCE = 0.5;
   private static final double LACUNARITY = 2.0;
   public static final Codec<NormalNoise> DIRECT_CODEC;
   public static final Codec<Holder<NormalNoise>> CODEC;
   private final Parameters parameters;
   private final List<OctaveInfo> octaves;
   private final double normalizationFactor;
   private final Interval range;

   private NormalNoise(final Parameters parameters) {
      super();
      this.parameters = parameters;
      this.octaves = buildOctaves(parameters.baseOctave, parameters.baseAmplitude, parameters.octaveCount, parameters.normalize != NormalNoise.Normalization.DISABLED, parameters.amplitudeModifiers);
      double targetAmplitude = this.octaves.stream().mapToDouble(OctaveInfo::absAmplitude).sum();
      double normalizationFactor = computeNormalizationFactor(targetAmplitude, this.octaves);
      if (parameters.normalize == NormalNoise.Normalization.LEGACY && normalizationFactor != 0.0) {
         double parityNormalizationFactor = computeParityNormalizationFactor(parameters.baseAmplitude, parameters.octaveCount, parameters.amplitudeModifiers);
         targetAmplitude *= parityNormalizationFactor / normalizationFactor;
         normalizationFactor = parityNormalizationFactor;
      }

      this.normalizationFactor = normalizationFactor;
      this.range = Interval.ofSymmetric((float)(targetAmplitude * 0.3333333333333333 * 6.0));
   }

   private static List<OctaveInfo> buildOctaves(final int baseOctave, final double baseAmplitude, final int octaveCount, final boolean normalize, final DoubleList amplitudeModifiers) {
      double frequency = Math.pow(2.0, (double)baseOctave);
      double amplitude = baseAmplitude;
      if (normalize) {
         amplitude = baseAmplitude * (Math.pow(0.5, (double)(-(octaveCount - 1))) / (Math.pow(0.5, (double)(-octaveCount)) - 1.0));
      }

      ImmutableList.Builder<OctaveInfo> octaves = ImmutableList.builderWithExpectedSize(octaveCount);

      for(int i = 0; i < octaveCount; ++i) {
         double amplitudeModifier = getAmplitudeModifier(amplitudeModifiers, i);
         if (amplitudeModifier != 0.0) {
            double modifiedAmplitude = amplitude * amplitudeModifier;
            octaves.add(new OctaveInfo(baseOctave + i, frequency, modifiedAmplitude));
         }

         frequency *= 2.0;
         amplitude *= 0.5;
      }

      return octaves.build();
   }

   private static double getAmplitudeModifier(final DoubleList amplitudeModifiers, final int index) {
      return amplitudeModifiers.isEmpty() ? 1.0 : amplitudeModifiers.getDouble(index);
   }

   private static double computeNormalizationFactor(final double targetAmplitude, final List<OctaveInfo> octaves) {
      double inputDeviation = estimateDeviation(octaves);
      if (inputDeviation == 0.0) {
         return 0.0;
      } else {
         double inputSumDeviation = inputDeviation * Math.sqrt(2.0);
         double targetDeviation = targetAmplitude * 0.3333333333333333;
         return targetDeviation / inputSumDeviation;
      }
   }

   private static double estimateDeviation(final List<OctaveInfo> octaves) {
      double variance = 0.0;

      for(OctaveInfo octave : octaves) {
         double layerDeviation = 0.2702247831245211 * octave.absAmplitude();
         variance += Mth.square(layerDeviation);
      }

      return Math.sqrt(variance);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static NormalNoise createParity(final int firstOctave, final DoubleList amplitudes) {
      if (amplitudes.isEmpty()) {
         throw new IllegalArgumentException("Need at least 1 amplitude");
      } else {
         Builder parameters = builder();
         parameters.setBaseOctave(firstOctave);
         parameters.setOctaveCount(amplitudes.size());
         parameters.setBaseAmplitude(computeParityBaseAmplitude(parameters.baseOctave, amplitudes));

         for(int i = 0; i < amplitudes.size(); ++i) {
            double modifier = amplitudes.getDouble(i);
            if (modifier != 1.0) {
               parameters.setAmplitudeModifier(i, modifier);
            }
         }

         return parameters.build();
      }
   }

   public static NormalNoise createParity(final int firstOctave, final double... amplitudes) {
      return createParity(firstOctave, DoubleList.of(amplitudes));
   }

   private static double computeParityBaseAmplitude(final int baseOctave, final DoubleList amplitudes) {
      List<OctaveInfo> octaves = buildOctaves(baseOctave, 1.0, amplitudes.size(), true, amplitudes);
      double targetAmplitude = octaves.stream().mapToDouble(OctaveInfo::absAmplitude).sum();
      double newNormalizationFactor = computeNormalizationFactor(targetAmplitude, octaves);
      if (newNormalizationFactor == 0.0) {
         return 1.0;
      } else {
         double oldNormalizationFactor = computeParityNormalizationFactor(1.0, amplitudes.size(), amplitudes);
         return oldNormalizationFactor / newNormalizationFactor;
      }
   }

   private static double computeParityNormalizationFactor(final double baseAmplitude, final int octaveCount, final DoubleList amplitudeModifiers) {
      int minOctave = 2147483647;
      int maxOctave = -2147483648;

      for(int i = 0; i < octaveCount; ++i) {
         double modifier = getAmplitudeModifier(amplitudeModifiers, i);
         if (modifier != 0.0) {
            minOctave = Math.min(minOctave, i);
            maxOctave = Math.max(maxOctave, i);
         }
      }

      return baseAmplitude * 0.5 * 0.3333333333333333 / parityExpectedDeviation(maxOctave - minOctave);
   }

   private static double parityExpectedDeviation(final int octaveSpan) {
      return 0.1 * (1.0 + 1.0 / (double)(octaveSpan + 1));
   }

   public Noise create(final RandomSource random) {
      PositionalRandomFactory firstRandom = random.forkPositional();
      PositionalRandomFactory secondRandom = random.forkPositional();
      NoiseStack.Builder stack = NoiseStack.builder();

      for(OctaveInfo octave : this.octaves) {
         String octaveSeed = octave.seed();
         Noise firstNoise = new PerlinNoise(firstRandom.fromHashOf(octaveSeed));
         Noise secondNoise = new PerlinNoise(secondRandom.fromHashOf(octaveSeed));
         float valueFactor = (float)(this.normalizationFactor * octave.amplitude);
         stack.add(firstNoise, octave.frequency, valueFactor);
         stack.add(secondNoise, octave.frequency * 1.0181268882175227, valueFactor);
      }

      return stack.build();
   }

   /** @deprecated */
   @Deprecated
   public Noise createForLegacyNetherBiome(final RandomSource random) {
      DoubleList amplitudes = this.parameters.amplitudeModifiers();
      if (amplitudes.isEmpty()) {
         amplitudes = new DoubleArrayList(Collections.nCopies(this.parameters.octaveCount(), 1.0));
      }

      NoiseStack first = LegacyFbmInitializer.createForLegacyNetherBiome(random, this.parameters.baseOctave(), amplitudes);
      NoiseStack second = LegacyFbmInitializer.createForLegacyNetherBiome(random, this.parameters.baseOctave(), amplitudes);
      float valueFactor = (float)(this.normalizationFactor * this.parameters.baseAmplitude());
      return NoiseStack.builder().addStack(first, 1.0, valueFactor).addStack(second, 1.0181268882175227, valueFactor).build();
   }

   public Interval range() {
      return this.range;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof NormalNoise) {
            NormalNoise noise = (NormalNoise)obj;
            if (this.parameters.equals(noise.parameters)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.parameters.hashCode();
   }

   public String toString() {
      return "NormalNoise[parameters=" + String.valueOf(this.parameters) + "]";
   }

   @VisibleForTesting
   public void parityConfigString(final StringBuilder sb, final RandomSource random) {
      sb.append("NormalNoise {");
      sb.append("first: ");
      this.perlinParityConfigString(sb, random);
      sb.append(", second: ");
      this.perlinParityConfigString(sb, random);
      sb.append("}");
   }

   @VisibleForTesting
   private void perlinParityConfigString(final StringBuilder sb, final RandomSource random) {
      sb.append("PerlinNoise{");
      List<String> amplitudeStrings = IntStream.range(0, this.parameters.octaveCount).mapToDouble((ix) -> getAmplitudeModifier(this.parameters.amplitudeModifiers, ix)).mapToObj((d) -> String.format(Locale.ROOT, "%.2f", d)).toList();
      sb.append("first octave: ").append(this.parameters.baseOctave).append(", amplitudes: ").append(amplitudeStrings).append(", noise levels: [");
      PositionalRandomFactory positional = random.forkPositional();

      for(int i = 0; i < this.parameters.octaveCount; ++i) {
         sb.append(i).append(": ");
         int octaveIndex = this.parameters.baseOctave + i;
         Optional<OctaveInfo> octave = this.octaves.stream().filter((info) -> info.octaveIndex == octaveIndex).findFirst();
         if (octave.isPresent()) {
            (new PerlinNoise(positional.fromHashOf(((OctaveInfo)octave.get()).seed()))).parityConfigString(sb);
         } else {
            sb.append("null");
         }

         sb.append(", ");
      }

      sb.append("]");
      sb.append("}");
   }

   static {
      DIRECT_CODEC = NormalNoise.Parameters.CODEC.xmap(NormalNoise::new, (n) -> n.parameters);
      CODEC = RegistryCodecs.holder(Registries.NOISE, DIRECT_CODEC);
   }

   private static record OctaveInfo(int octaveIndex, double frequency, double amplitude) {
      private OctaveInfo {
         super();
      }

      public String seed() {
         return "octave_" + this.octaveIndex;
      }

      public double absAmplitude() {
         return Math.abs(this.amplitude);
      }
   }

   public static class Builder {
      private double baseAmplitude = 1.0;
      private int baseOctave;
      private int octaveCount = 1;
      private Normalization normalize;
      private DoubleList amplitudeModifiers;

      private Builder() {
         super();
         this.normalize = NormalNoise.Normalization.ENABLED;
         this.amplitudeModifiers = DoubleList.of();
      }

      public Builder setBaseAmplitude(final double baseAmplitude) {
         this.baseAmplitude = baseAmplitude;
         return this;
      }

      public Builder setBaseOctave(final int baseOctave) {
         this.baseOctave = baseOctave;
         return this;
      }

      public Builder setOctaveCount(final int octaveCount) {
         if (!this.amplitudeModifiers.isEmpty()) {
            throw new IllegalArgumentException("Cannot set octave count after setting amplitude modifier");
         } else {
            this.octaveCount = octaveCount;
            return this;
         }
      }

      public Builder setNormalize(final boolean normalize) {
         this.normalize = normalize ? NormalNoise.Normalization.ENABLED : NormalNoise.Normalization.DISABLED;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder setLegacyNormalization() {
         this.normalize = NormalNoise.Normalization.LEGACY;
         return this;
      }

      public Builder setAmplitudeModifier(final int index, final double value) {
         if (index >= 0 && index < this.octaveCount) {
            if (this.amplitudeModifiers.isEmpty()) {
               this.amplitudeModifiers = new DoubleArrayList(this.octaveCount);

               for(int i = 0; i < this.octaveCount; ++i) {
                  this.amplitudeModifiers.add(1.0);
               }
            }

            this.amplitudeModifiers.set(index, value);
            return this;
         } else {
            throw new IllegalArgumentException(index + " outside of octave range [0; " + this.octaveCount + ")");
         }
      }

      public NormalNoise build() {
         Parameters parameters = new Parameters(this.baseAmplitude, this.baseOctave, this.octaveCount, this.normalize, this.amplitudeModifiers);
         return new NormalNoise(parameters);
      }
   }

   private static record Parameters(double baseAmplitude, int baseOctave, int octaveCount, Normalization normalize, DoubleList amplitudeModifiers) {
      private static final Codec<DoubleList> AMPLITUDE_MODIFIERS_CODEC = Codec.doubleRange(0.0, 1000000.0).listOf(0, 32).xmap(DoubleArrayList::new, Function.identity());
      public static final Codec<Parameters> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.doubleRange(9.999999747378752E-6, 1000000.0).optionalFieldOf("base_amplitude", 1.0).forGetter(Parameters::baseAmplitude), Codec.intRange(-32, 32).fieldOf("base_octave").forGetter(Parameters::baseOctave), Codec.intRange(1, 32).optionalFieldOf("octave_count", 1).forGetter(Parameters::octaveCount), NormalNoise.Normalization.CODEC.optionalFieldOf("normalize", NormalNoise.Normalization.ENABLED).forGetter(Parameters::normalize), AMPLITUDE_MODIFIERS_CODEC.optionalFieldOf("amplitude_modifiers", DoubleList.of()).forGetter(Parameters::amplitudeModifiers)).apply(i, Parameters::new)).validate(Parameters::validate);

      private Parameters {
         super();
      }

      private static DataResult<Parameters> validate(final Parameters parameters) {
         return !parameters.amplitudeModifiers().isEmpty() && parameters.amplitudeModifiers().size() != parameters.octaveCount() ? DataResult.error(() -> {
            int var10000 = parameters.amplitudeModifiers().size();
            return "amplitude_modifiers had size " + var10000 + ", but octave_count was " + parameters.octaveCount();
         }) : DataResult.success(parameters);
      }
   }

   public static enum Normalization {
      DISABLED,
      ENABLED,
      /** @deprecated */
      @Deprecated
      LEGACY;

      public static final Codec<Normalization> CODEC = Codec.either(Codec.BOOL, Codec.STRING.validate((string) -> string.equals("legacy") ? DataResult.success(string) : DataResult.error(() -> "Invalid normalization type: " + string))).xmap((either) -> (Normalization)either.map((enabled) -> enabled ? ENABLED : DISABLED, (var0) -> LEGACY), (normalization) -> {
         Either var10000;
         switch (normalization.ordinal()) {
            case 0 -> var10000 = Either.left(false);
            case 1 -> var10000 = Either.left(true);
            case 2 -> var10000 = Either.right("legacy");
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      private Normalization() {
      }

      // $FF: synthetic method
      private static Normalization[] $values() {
         return new Normalization[]{DISABLED, ENABLED, LEGACY};
      }
   }
}
