package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class FixedBiomeSource extends BiomeSource {
   private final Biome biome;

   public FixedBiomeSource(FixedBiomeSourceSettings var1) {
      super(ImmutableSet.of(var1.getBiome()));
      this.biome = var1.getBiome();
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      return this.biome;
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int var1, int var2, int var3, int var4, List var5, Random var6) {
      return var5.contains(this.biome) ? new BlockPos(var1 - var4 + var6.nextInt(var4 * 2 + 1), var2, var3 - var4 + var6.nextInt(var4 * 2 + 1)) : null;
   }

   public Set getBiomesWithin(int var1, int var2, int var3, int var4) {
      return Sets.newHashSet(new Biome[]{this.biome});
   }
}
