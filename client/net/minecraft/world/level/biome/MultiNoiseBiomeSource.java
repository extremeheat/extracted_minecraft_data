package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class MultiNoiseBiomeSource extends BiomeSource {
   private static final MapCodec<Holder<Biome>> ENTRY_CODEC;
   public static final MapCodec<Climate.ParameterList<Holder<Biome>>> DIRECT_CODEC;
   private static final MapCodec<Holder<MultiNoiseBiomeSourceParameterList>> PRESET_CODEC;
   public static final MapCodec<MultiNoiseBiomeSource> CODEC;
   private final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

   private MultiNoiseBiomeSource(final Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters) {
      super();
      this.parameters = parameters;
   }

   public static MultiNoiseBiomeSource createFromList(final Climate.ParameterList<Holder<Biome>> parameters) {
      return new MultiNoiseBiomeSource(Either.left(parameters));
   }

   public static MultiNoiseBiomeSource createFromPreset(final Holder<MultiNoiseBiomeSourceParameterList> preset) {
      return new MultiNoiseBiomeSource(Either.right(preset));
   }

   private Climate.ParameterList<Holder<Biome>> parameters() {
      return (Climate.ParameterList)this.parameters.map((direct) -> direct, (preset) -> ((MultiNoiseBiomeSourceParameterList)preset.value()).parameters());
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return this.parameters().values().stream().map(Pair::getSecond);
   }

   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public boolean stable(final ResourceKey<MultiNoiseBiomeSourceParameterList> expected) {
      Optional<Holder<MultiNoiseBiomeSourceParameterList>> preset = this.parameters.right();
      return preset.isPresent() && ((Holder)preset.get()).is(expected);
   }

   public Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ, final Climate.Sampler sampler) {
      return this.getNoiseBiome(sampler.sample(quartX, quartY, quartZ));
   }

   @VisibleForDebug
   public Holder<Biome> getNoiseBiome(final Climate.TargetPoint target) {
      return (Holder)this.parameters().findValue(target);
   }

   public void addDebugInfo(final List<String> result, final BlockPos feetPos, final Climate.Sampler sampler) {
      int quartX = QuartPos.fromBlock(feetPos.getX());
      int quartY = QuartPos.fromBlock(feetPos.getY());
      int quartZ = QuartPos.fromBlock(feetPos.getZ());
      Climate.TargetPoint sampleQuantized = sampler.sample(quartX, quartY, quartZ);
      float continentalness = Climate.unquantizeCoord(sampleQuantized.continentalness());
      float erosion = Climate.unquantizeCoord(sampleQuantized.erosion());
      float temperature = Climate.unquantizeCoord(sampleQuantized.temperature());
      float humidity = Climate.unquantizeCoord(sampleQuantized.humidity());
      float weirdness = Climate.unquantizeCoord(sampleQuantized.weirdness());
      double peaksAndValleys = (double)NoiseRouterData.peaksAndValleys(weirdness);
      OverworldBiomeBuilder biomeBuilder = new OverworldBiomeBuilder();
      String var10001 = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(peaksAndValleys);
      result.add("Biome builder PV: " + var10001 + " C: " + biomeBuilder.getDebugStringForContinentalness((double)continentalness) + " E: " + biomeBuilder.getDebugStringForErosion((double)erosion) + " T: " + biomeBuilder.getDebugStringForTemperature((double)temperature) + " H: " + biomeBuilder.getDebugStringForHumidity((double)humidity));
   }

   static {
      ENTRY_CODEC = Biome.CODEC.fieldOf("biome");
      DIRECT_CODEC = Climate.ParameterList.codec(ENTRY_CODEC).fieldOf("biomes");
      PRESET_CODEC = MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable());
      CODEC = Codec.mapEither(DIRECT_CODEC, PRESET_CODEC).xmap(MultiNoiseBiomeSource::new, (o) -> o.parameters);
   }
}
