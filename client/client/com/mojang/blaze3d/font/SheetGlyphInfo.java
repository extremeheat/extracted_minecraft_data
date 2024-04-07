package com.mojang.blaze3d.font;

public interface SheetGlyphInfo {
   int getPixelWidth();

   int getPixelHeight();

   void upload(int var1, int var2);

   boolean isColored();

   float getOversample();

   default float getLeft() {
      return this.getBearingLeft();
   }

   default float getRight() {
      return this.getLeft() + (float)this.getPixelWidth() / this.getOversample();
   }

   default float getTop() {
      return 7.0F - this.getBearingTop();
   }

   default float getBottom() {
      return this.getTop() + (float)this.getPixelHeight() / this.getOversample();
   }

   default float getBearingLeft() {
      return 0.0F;
   }

   default float getBearingTop() {
      return 7.0F;
   }
}
