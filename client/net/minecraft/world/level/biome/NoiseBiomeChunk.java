package net.minecraft.world.level.biome;

import java.util.Arrays;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.jspecify.annotations.Nullable;

public class NoiseBiomeChunk implements NoiseBiomeResolver {
   private static final int QUART_PER_SECTION = 4;
   private static final int QUART_PER_SECTION_MASK = 3;
   private final PalettedContainerRO<Holder<Biome>>[] noiseBiomeSections;
   private final int ySize;
   private final int minQuartX;
   private final int minQuartY;
   private final int minQuartZ;

   public NoiseBiomeChunk(final LevelHeightAccessor levelHeightAccessor, final ChunkPos pos, final PalettedContainerFactory containerFactory) {
      this(pos, levelHeightAccessor, makeEmptySections(containerFactory, levelHeightAccessor.getSectionsCount()));
   }

   private static PalettedContainerRO<Holder<Biome>>[] makeEmptySections(final PalettedContainerFactory containerFactory, final int sectionsCount) {
      PalettedContainer<Holder<Biome>>[] result = new PalettedContainer[sectionsCount];
      Arrays.fill(result, containerFactory.createForNoiseBiomes());
      return result;
   }

   private NoiseBiomeChunk(final ChunkPos pos, final LevelHeightAccessor levelHeightAccessor, final PalettedContainerRO<Holder<Biome>>[] noiseBiomeSections) {
      super();
      this.minQuartX = QuartPos.fromSection(pos.x());
      this.minQuartY = QuartPos.fromSection(levelHeightAccessor.getMinSectionY());
      this.minQuartZ = QuartPos.fromSection(pos.z());
      this.ySize = levelHeightAccessor.getSectionsCount() * 4;
      this.noiseBiomeSections = noiseBiomeSections;
   }

   public void fillSections(final PalettedContainerFactory containerFactory, final NoiseBiomeResolver noiseBiomeResolver, final LevelHeightAccessor heightAccessor) {
      int minSectionY = heightAccessor.getMinSectionY() - QuartPos.toSection(this.minQuartY);

      for(int i = minSectionY; i < heightAccessor.getSectionsCount(); ++i) {
         int minQuartYInSection = this.minQuartY + i * 4;
         this.noiseBiomeSections[i] = containerFactory.createForNoiseBiomes((quartX, quartY, quartZ) -> noiseBiomeResolver.getNoiseBiome(this.minQuartX + quartX, minQuartYInSection + quartY, this.minQuartZ + quartZ));
      }

   }

   public Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ) {
      int clampedQuartY = Math.clamp((long)(quartY - this.minQuartY), 0, this.ySize - 1);
      int sectionIndex = QuartPos.toSection(clampedQuartY);
      return (Holder)this.noiseBiomeSections[sectionIndex].get(quartX & 3, clampedQuartY & 3, quartZ & 3);
   }

   public PalettedContainerRO<Holder<Biome>> getSection(final int y) {
      return this.noiseBiomeSections[y - QuartPos.toSection(this.minQuartY)];
   }

   public static class Builder {
      private final @Nullable PalettedContainerRO<Holder<Biome>>[] noiseBiomeSections;
      private final LevelHeightAccessor levelHeight;
      private final ChunkPos pos;
      private boolean hasSections;

      public Builder(final LevelHeightAccessor levelHeight, final ChunkPos pos) {
         super();
         this.noiseBiomeSections = new PalettedContainerRO[levelHeight.getSectionsCount()];
         this.levelHeight = levelHeight;
         this.pos = pos;
      }

      public Builder addSection(final int sectionY, final PalettedContainerRO<Holder<Biome>> biomes) {
         this.noiseBiomeSections[sectionY - this.levelHeight.getMinSectionY()] = biomes;
         this.hasSections = true;
         return this;
      }

      public @Nullable NoiseBiomeChunk build(final PalettedContainerFactory containerFactory) {
         if (!this.hasSections) {
            return null;
         } else {
            for(int i = 0; i < this.noiseBiomeSections.length; ++i) {
               if (this.noiseBiomeSections[i] == null) {
                  this.noiseBiomeSections[i] = containerFactory.createForNoiseBiomes();
               }
            }

            return new NoiseBiomeChunk(this.pos, this.levelHeight, this.noiseBiomeSections);
         }
      }
   }
}
