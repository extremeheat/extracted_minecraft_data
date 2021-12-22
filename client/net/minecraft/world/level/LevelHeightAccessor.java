package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
   int getHeight();

   int getMinBuildHeight();

   default int getMaxBuildHeight() {
      return this.getMinBuildHeight() + this.getHeight();
   }

   default int getSectionsCount() {
      return this.getMaxSection() - this.getMinSection();
   }

   default int getMinSection() {
      return SectionPos.blockToSectionCoord(this.getMinBuildHeight());
   }

   default int getMaxSection() {
      return SectionPos.blockToSectionCoord(this.getMaxBuildHeight() - 1) + 1;
   }

   default boolean isOutsideBuildHeight(BlockPos var1) {
      return this.isOutsideBuildHeight(var1.getY());
   }

   default boolean isOutsideBuildHeight(int var1) {
      return var1 < this.getMinBuildHeight() || var1 >= this.getMaxBuildHeight();
   }

   default int getSectionIndex(int var1) {
      return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(var1));
   }

   default int getSectionIndexFromSectionY(int var1) {
      return var1 - this.getMinSection();
   }

   default int getSectionYFromSectionIndex(int var1) {
      return var1 + this.getMinSection();
   }

   static LevelHeightAccessor create(final int var0, final int var1) {
      return new LevelHeightAccessor() {
         public int getHeight() {
            return var1;
         }

         public int getMinBuildHeight() {
            return var0;
         }
      };
   }
}
