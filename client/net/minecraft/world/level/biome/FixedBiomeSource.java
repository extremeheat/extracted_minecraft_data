package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class FixedBiomeSource extends BiomeSource implements BiomeManager.NoiseBiomeSource {
   public static final Codec<FixedBiomeSource> CODEC;
   private final Supplier<Biome> biome;

   public FixedBiomeSource(Biome var1) {
      this(() -> {
         return var1;
      });
   }

   public FixedBiomeSource(Supplier<Biome> var1) {
      super((List)ImmutableList.of((Biome)var1.get()));
      this.biome = var1;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long var1) {
      return this;
   }

   public Biome getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return (Biome)this.biome.get();
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      return (Biome)this.biome.get();
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, int var5, Predicate<Biome> var6, Random var7, boolean var8, Climate.Sampler var9) {
      if (var6.test((Biome)this.biome.get())) {
         return var8 ? new BlockPos(var1, var2, var3) : new BlockPos(var1 - var4 + var7.nextInt(var4 * 2 + 1), var2, var3 - var4 + var7.nextInt(var4 * 2 + 1));
      } else {
         return null;
      }
   }

   public Set<Biome> getBiomesWithin(int var1, int var2, int var3, int var4, Climate.Sampler var5) {
      return Sets.newHashSet(new Biome[]{(Biome)this.biome.get()});
   }

   static {
      CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (var0) -> {
         return var0.biome;
      }).stable().codec();
   }
}
