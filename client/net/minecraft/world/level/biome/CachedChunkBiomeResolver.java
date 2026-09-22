package net.minecraft.world.level.biome;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public class CachedChunkBiomeResolver implements BiomeResolver {
   private static final int QUART_PER_SECTION = 4;
   private static final int MARGIN = 1;
   private static final int SIZE = 6;
   private static final int X_OFFSET = 0;
   private static final int Y_OFFSET = 1;
   private static final int Z_OFFSET = 2;
   private static final int AXIS_COUNT = 3;
   private final Holder<Biome>[] noiseBiomes;
   private final double[] fiddle;
   private final int ySize;
   private final int minQuartX;
   private final int minQuartY;
   private final int minQuartZ;

   public CachedChunkBiomeResolver(final ChunkAccess chunk, final NoiseBiomeResolver noiseBiomeResolver, final long biomeZoomSeed) {
      super();
      this.minQuartX = QuartPos.fromSection(chunk.getPos().x()) - 1;
      this.minQuartY = QuartPos.fromSection(chunk.getMinSectionY()) - 1;
      this.minQuartZ = QuartPos.fromSection(chunk.getPos().z()) - 1;
      this.ySize = chunk.getSectionsCount() * 4 + 2;
      this.noiseBiomes = new Holder[6 * this.ySize * 6];
      this.fiddle = new double[6 * this.ySize * 6 * 3];
      this.fill(noiseBiomeResolver, biomeZoomSeed);
   }

   public void fill(final NoiseBiomeResolver noiseBiomeResolver, final long seed) {
      for(int x = 0; x < 6; ++x) {
         for(int z = 0; z < 6; ++z) {
            for(int y = 0; y < this.ySize; ++y) {
               int index = this.getIndex(x, y, z);
               int quartX = this.minQuartX + x;
               int quartY = this.minQuartY + y;
               int quartZ = this.minQuartZ + z;
               this.noiseBiomes[index] = noiseBiomeResolver.getNoiseBiome(quartX, quartY, quartZ);
               int fiddleIndex = getFiddleIndex(index);
               long fiddleSeed = BiomeManager.prepareFiddleSeed(seed, quartX, quartY, quartZ);
               this.fiddle[fiddleIndex + 0] = BiomeManager.getFiddle(fiddleSeed);
               fiddleSeed = BiomeManager.nextFiddleSeed(seed, fiddleSeed);
               this.fiddle[fiddleIndex + 1] = BiomeManager.getFiddle(fiddleSeed);
               fiddleSeed = BiomeManager.nextFiddleSeed(seed, fiddleSeed);
               this.fiddle[fiddleIndex + 2] = BiomeManager.getFiddle(fiddleSeed);
            }
         }
      }

   }

   private static int getFiddleIndex(final int index) {
      return 3 * index;
   }

   private int getIndex(final int quartX, final int quartY, final int quartZ) {
      return (quartX * 6 + quartZ) * this.ySize + quartY;
   }

   private Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      return this.noiseBiomes[this.getIndex(quartX, quartY, quartZ)];
   }

   public PalettedContainer<Holder<Biome>> fillSection(final PalettedContainerRO<Holder<Biome>> biomes, final int minX, final int minY, final int minZ) {
      int minQuartYInSection = QuartPos.fromBlock(minY) - this.minQuartY;
      int biomeCount = this.countPossibleBiomesInSection(minQuartYInSection);
      Holder<Biome> noiseBiome = this.getNoiseBiome(1, minQuartYInSection, 1);
      PalettedContainer<Holder<Biome>> newBiomes = biomes.recreate(noiseBiome, Mth.ceillog2(biomeCount));
      if (biomeCount > 1) {
         this.fillUpscaledBiomes(newBiomes, minX, minY, minZ);
      }

      return newBiomes;
   }

   private int countPossibleBiomesInSection(final int minQuartYInSection) {
      Set<Holder<Biome>> biomeSet = new ObjectArraySet(1);

      for(int x = 0; x < 6; ++x) {
         for(int z = 0; z < 6; ++z) {
            for(int y = 0; y < 6; ++y) {
               biomeSet.add(this.getNoiseBiome(x, minQuartYInSection + y - 1, z));
            }
         }
      }

      return biomeSet.size();
   }

   private void fillUpscaledBiomes(final PalettedContainer<Holder<Biome>> newBiomes, final int minX, final int minY, final int minZ) {
      int minQuartYBelowSection = QuartPos.fromBlock(minY) - this.minQuartY - 1;

      for(int quartX = 0; quartX < 5; ++quartX) {
         int fromX = getInterpolationCellStart(quartX);
         int toX = getInterpolationCellEnd(quartX);

         for(int quartZ = 0; quartZ < 5; ++quartZ) {
            int fromZ = getInterpolationCellStart(quartZ);
            int toZ = getInterpolationCellEnd(quartZ);

            for(int quartY = 0; quartY < 5; ++quartY) {
               int fromY = getInterpolationCellStart(quartY);
               int toY = getInterpolationCellEnd(quartY);
               if (this.isUniformCell(quartX, minQuartYBelowSection + quartY, quartZ)) {
                  newBiomes.fillUnchecked(fromX, fromY, fromZ, toX, toY, toZ, this.getNoiseBiome(quartX, minQuartYBelowSection + quartY, quartZ));
               } else {
                  this.fillWithQuery(newBiomes, minX, minY, minZ, fromX, fromY, fromZ, toX, toZ, toY);
               }
            }
         }
      }

   }

   private static int getInterpolationCellEnd(final int quart) {
      return Math.min(QuartPos.toBlock(quart) + 2, 16) - 1;
   }

   private static int getInterpolationCellStart(final int quart) {
      return Math.max(QuartPos.toBlock(quart) - 2, 0);
   }

   private boolean isUniformCell(final int quartX, final int quartY, final int quartZ) {
      Holder<Biome> firstBiome = this.getNoiseBiome(quartX, quartY, quartZ);
      return firstBiome.equals(this.getNoiseBiome(quartX, quartY, quartZ + 1)) && firstBiome.equals(this.getNoiseBiome(quartX, quartY + 1, quartZ)) && firstBiome.equals(this.getNoiseBiome(quartX, quartY + 1, quartZ + 1)) && firstBiome.equals(this.getNoiseBiome(quartX + 1, quartY, quartZ)) && firstBiome.equals(this.getNoiseBiome(quartX + 1, quartY, quartZ + 1)) && firstBiome.equals(this.getNoiseBiome(quartX + 1, quartY + 1, quartZ)) && firstBiome.equals(this.getNoiseBiome(quartX + 1, quartY + 1, quartZ + 1));
   }

   private void fillWithQuery(final PalettedContainer<Holder<Biome>> newBiomes, final int offsetX, final int offsetY, final int offsetZ, final int fromX, final int fromY, final int fromZ, final int toX, final int toZ, final int toY) {
      for(int x = fromX; x <= toX; ++x) {
         for(int z = fromZ; z <= toZ; ++z) {
            for(int y = fromY; y <= toY; ++y) {
               newBiomes.setUnchecked(x, y, z, this.getBiome(offsetX + x, offsetY + y, offsetZ + z));
            }
         }
      }

   }

   public Holder<Biome> getBiome(final int x, final int y, final int z) {
      int absX = x - 2;
      int absY = y - 2;
      int absZ = z - 2;
      int parentX = (absX >> 2) - this.minQuartX;
      int parentY = (absY >> 2) - this.minQuartY;
      int parentZ = (absZ >> 2) - this.minQuartZ;
      double fractX = (double)(absX & 3) / 4.0;
      double fractY = (double)(absY & 3) / 4.0;
      double fractZ = (double)(absZ & 3) / 4.0;
      int minI = 0;
      double minFiddledDistance = 1.0 / 0.0;

      for(int i = 0; i < 8; ++i) {
         boolean xEven = (i & 4) == 0;
         boolean yEven = (i & 2) == 0;
         boolean zEven = (i & 1) == 0;
         int cornerX = xEven ? parentX : parentX + 1;
         int cornerY = yEven ? parentY : parentY + 1;
         int cornerZ = zEven ? parentZ : parentZ + 1;
         double distanceX = xEven ? fractX : fractX - 1.0;
         double distanceY = yEven ? fractY : fractY - 1.0;
         double distanceZ = zEven ? fractZ : fractZ - 1.0;
         double next = this.getFiddledDistance(this.getIndex(cornerX, cornerY, cornerZ), distanceX, distanceY, distanceZ);
         if (minFiddledDistance > next) {
            minI = i;
            minFiddledDistance = next;
         }
      }

      int biomeX = (minI & 4) == 0 ? parentX : parentX + 1;
      int biomeY = (minI & 2) == 0 ? parentY : parentY + 1;
      int biomeZ = (minI & 1) == 0 ? parentZ : parentZ + 1;
      return this.getNoiseBiome(biomeX, biomeY, biomeZ);
   }

   private double getFiddledDistance(final int index, final double distanceX, final double distanceY, final double distanceZ) {
      int fiddleIndex = getFiddleIndex(index);
      return Mth.square(distanceX + this.fiddle[fiddleIndex + 0]) + Mth.square(distanceY + this.fiddle[fiddleIndex + 1]) + Mth.square(distanceZ + this.fiddle[fiddleIndex + 2]);
   }
}
