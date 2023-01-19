package com.mojang.blaze3d.font;

public interface SheetGlyphInfo {
   int getPixelWidth();

   int getPixelHeight();

   void upload(int var1, int var2);

   boolean isColored();

   float getOversample();

   default float getLeft() {
      return this.getBearingX();
   }

   default float getRight() {
      return this.getLeft() + (float)this.getPixelWidth() / this.getOversample();
   }

   default float getUp() {
      return this.getBearingY();
   }

   default float getDown() {
      return this.getUp() + (float)this.getPixelHeight() / this.getOversample();
   }

   default float getBearingX() {
      return 0.0F;
   }

   default float getBearingY() {
      return 3.0F;
   }
}
