package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableSet;

public class CheckerboardColumnBiomeSource extends BiomeSource {
   private final Biome[] allowedBiomes;
   private final int bitShift;

   public CheckerboardColumnBiomeSource(CheckerboardBiomeSourceSettings var1) {
      super(ImmutableSet.copyOf(var1.getAllowedBiomes()));
      this.allowedBiomes = var1.getAllowedBiomes();
      this.bitShift = var1.getSize() + 2;
   }

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      return this.allowedBiomes[Math.abs(((var1 >> this.bitShift) + (var3 >> this.bitShift)) % this.allowedBiomes.length)];
   }
}
