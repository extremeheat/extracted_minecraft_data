package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;

public interface GlyphProvider extends AutoCloseable {
   @Override
   default void close() {
   }

   @Nullable
   default GlyphInfo getGlyph(int var1) {
      return null;
   }

   IntSet getSupportedGlyphs();
}
