package net.minecraft.world.level.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

@FunctionalInterface
public interface BiomeResolver {
   Holder<Biome> getBiome(int x, int y, int z);

   default Holder<Biome> getBiome(final BlockPos pos) {
      return this.getBiome(pos.getX(), pos.getY(), pos.getZ());
   }

   default PalettedContainer<Holder<Biome>> fillSection(final PalettedContainerRO<Holder<Biome>> biomes, final int minX, final int minY, final int minZ) {
      PalettedContainer<Holder<Biome>> newBiomes = biomes.recreate();

      for(int y = 0; y < 16; ++y) {
         for(int z = 0; z < 16; ++z) {
            for(int x = 0; x < 16; ++x) {
               newBiomes.setUnchecked(x, y, z, this.getBiome(minX + x, minY + y, minZ + z));
            }
         }
      }

      return newBiomes;
   }
}
