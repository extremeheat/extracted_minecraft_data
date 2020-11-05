package net.minecraft.world.level;

import net.minecraft.core.BlockPos;

public interface LevelHeightAccessor {
   int getSectionsCount();

   int getMinSection();

   default int getMaxSection() {
      return this.getMinSection() + this.getSectionsCount();
   }

   default int getHeight() {
      return this.getSectionsCount() * 16;
   }

   default int getMinBuildHeight() {
      return this.getMinSection() * 16;
   }

   default int getMaxBuildHeight() {
      return this.getMinBuildHeight() + this.getHeight();
   }

   default boolean isOutsideBuildHeight(BlockPos var1) {
      return this.isOutsideBuildHeight(var1.getY());
   }

   default boolean isOutsideBuildHeight(int var1) {
      return var1 < this.getMinBuildHeight() || var1 >= this.getMaxBuildHeight();
   }

   default int getSectionIndex(int var1) {
      return this.getSectionIndexFromSectionY(var1 >> 4);
   }

   default int getSectionIndexFromSectionY(int var1) {
      return var1 - this.getMinSection();
   }

   default int getSectionYFromSectionIndex(int var1) {
      return var1 + this.getMinSection();
   }
}
