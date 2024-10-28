package net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;

public class FixedBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final MapCodec<FixedBiomeSource> CODEC;
   private final Holder<Biome> biome;

   public FixedBiomeSource(Holder<Biome> var1) {
      super();
      this.biome = var1;
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return Stream.of(this.biome);
   }

   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.biome;
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.biome;
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int var1, int var2, int var3, int var4, int var5, Predicate<Holder<Biome>> var6, RandomSource var7, boolean var8, Climate.Sampler var9) {
      if (var6.test(this.biome)) {
         return var8 ? Pair.of(new BlockPos(var1, var2, var3), this.biome) : Pair.of(new BlockPos(var1 - var4 + var7.nextInt(var4 * 2 + 1), var2, var3 - var4 + var7.nextInt(var4 * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(BlockPos var1, int var2, int var3, int var4, Predicate<Holder<Biome>> var5, Climate.Sampler var6, LevelReader var7) {
      return var5.test(this.biome) ? Pair.of(var1, this.biome) : null;
   }

   public Set<Holder<Biome>> getBiomesWithin(int var1, int var2, int var3, int var4, Climate.Sampler var5) {
      return Sets.newHashSet(Set.of(this.biome));
   }

   static {
      CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (var0) -> {
         return var0.biome;
      }).stable();
   }
}
