package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
   int getHeight();

   int getMinY();

   default int getMaxY() {
      return this.getMinY() + this.getHeight() - 1;
   }

   default int getSectionsCount() {
      return this.getMaxSectionY() - this.getMinSectionY() + 1;
   }

   default int getMinSectionY() {
      return SectionPos.blockToSectionCoord(this.getMinY());
   }

   default int getMaxSectionY() {
      return SectionPos.blockToSectionCoord(this.getMaxY());
   }

   default boolean isInsideBuildHeight(int var1) {
      return var1 >= this.getMinY() && var1 <= this.getMaxY();
   }

   default boolean isOutsideBuildHeight(BlockPos var1) {
      return this.isOutsideBuildHeight(var1.getY());
   }

   default boolean isOutsideBuildHeight(int var1) {
      return var1 < this.getMinY() || var1 > this.getMaxY();
   }

   default int getSectionIndex(int var1) {
      return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(var1));
   }

   default int getSectionIndexFromSectionY(int var1) {
      return var1 - this.getMinSectionY();
   }

   default int getSectionYFromSectionIndex(int var1) {
      return var1 + this.getMinSectionY();
   }

   static LevelHeightAccessor create(final int var0, final int var1) {
      return new LevelHeightAccessor() {
         public int getHeight() {
            return var1;
         }

         public int getMinY() {
            return var0;
         }
      };
   }
}
