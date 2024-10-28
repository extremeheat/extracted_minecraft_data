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

   private MultiNoiseBiomeSource(Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> var1) {
      super();
      this.parameters = var1;
   }

   public static MultiNoiseBiomeSource createFromList(Climate.ParameterList<Holder<Biome>> var0) {
      return new MultiNoiseBiomeSource(Either.left(var0));
   }

   public static MultiNoiseBiomeSource createFromPreset(Holder<MultiNoiseBiomeSourceParameterList> var0) {
      return new MultiNoiseBiomeSource(Either.right(var0));
   }

   private Climate.ParameterList<Holder<Biome>> parameters() {
      return (Climate.ParameterList)this.parameters.map((var0) -> {
         return var0;
      }, (var0) -> {
         return ((MultiNoiseBiomeSourceParameterList)var0.value()).parameters();
      });
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return this.parameters().values().stream().map(Pair::getSecond);
   }

   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public boolean stable(ResourceKey<MultiNoiseBiomeSourceParameterList> var1) {
      Optional var2 = this.parameters.right();
      return var2.isPresent() && ((Holder)var2.get()).is(var1);
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.getNoiseBiome(var4.sample(var1, var2, var3));
   }

   @VisibleForDebug
   public Holder<Biome> getNoiseBiome(Climate.TargetPoint var1) {
      return (Holder)this.parameters().findValue(var1);
   }

   public void addDebugInfo(List<String> var1, BlockPos var2, Climate.Sampler var3) {
      int var4 = QuartPos.fromBlock(var2.getX());
      int var5 = QuartPos.fromBlock(var2.getY());
      int var6 = QuartPos.fromBlock(var2.getZ());
      Climate.TargetPoint var7 = var3.sample(var4, var5, var6);
      float var8 = Climate.unquantizeCoord(var7.continentalness());
      float var9 = Climate.unquantizeCoord(var7.erosion());
      float var10 = Climate.unquantizeCoord(var7.temperature());
      float var11 = Climate.unquantizeCoord(var7.humidity());
      float var12 = Climate.unquantizeCoord(var7.weirdness());
      double var13 = (double)NoiseRouterData.peaksAndValleys(var12);
      OverworldBiomeBuilder var15 = new OverworldBiomeBuilder();
      String var10001 = OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(var13);
      var1.add("Biome builder PV: " + var10001 + " C: " + var15.getDebugStringForContinentalness((double)var8) + " E: " + var15.getDebugStringForErosion((double)var9) + " T: " + var15.getDebugStringForTemperature((double)var10) + " H: " + var15.getDebugStringForHumidity((double)var11));
   }

   static {
      ENTRY_CODEC = Biome.CODEC.fieldOf("biome");
      DIRECT_CODEC = Climate.ParameterList.codec(ENTRY_CODEC).fieldOf("biomes");
      PRESET_CODEC = MultiNoiseBiomeSourceParameterList.CODEC.fieldOf("preset").withLifecycle(Lifecycle.stable());
      CODEC = Codec.mapEither(DIRECT_CODEC, PRESET_CODEC).xmap(MultiNoiseBiomeSource::new, (var0) -> {
         return var0.parameters;
      });
   }
}
