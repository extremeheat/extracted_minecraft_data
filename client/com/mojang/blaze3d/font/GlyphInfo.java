package com.mojang.blaze3d.font;

public interface GlyphInfo {
   float getAdvance();

   default float getAdvance(boolean var1) {
      return this.getAdvance() + (var1 ? this.getBoldOffset() : 0.0F);
   }

   default float getBearingX() {
      return 0.0F;
   }

   default float getBoldOffset() {
      return 1.0F;
   }

   default float getShadowOffset() {
      return 1.0F;
   }
}
