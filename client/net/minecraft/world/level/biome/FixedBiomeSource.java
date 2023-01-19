package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;

public class FixedBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final Codec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, var0 -> var0.biome).stable().codec();
   private final Holder<Biome> biome;

   public FixedBiomeSource(Holder<Biome> var1) {
      super(ImmutableList.of(var1));
      this.biome = var1;
   }

   @Override
   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.biome;
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.biome;
   }

   @Nullable
   @Override
   public Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
      int var1, int var2, int var3, int var4, int var5, Predicate<Holder<Biome>> var6, RandomSource var7, boolean var8, Climate.Sampler var9
   ) {
      if (var6.test(this.biome)) {
         return var8
            ? Pair.of(new BlockPos(var1, var2, var3), this.biome)
            : Pair.of(new BlockPos(var1 - var4 + var7.nextInt(var4 * 2 + 1), var2, var3 - var4 + var7.nextInt(var4 * 2 + 1)), this.biome);
      } else {
         return null;
      }
   }

   @Nullable
   @Override
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(
      BlockPos var1, int var2, int var3, int var4, Predicate<Holder<Biome>> var5, Climate.Sampler var6, LevelReader var7
   ) {
      return var5.test(this.biome) ? Pair.of(var1, this.biome) : null;
   }

   @Override
   public Set<Holder<Biome>> getBiomesWithin(int var1, int var2, int var3, int var4, Climate.Sampler var5) {
      return Sets.newHashSet(Set.of(this.biome));
   }
}
