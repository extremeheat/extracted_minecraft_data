package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import javax.annotation.Nullable;

public interface GlyphProvider extends Closeable {
   default void close() {
   }

   @Nullable
   default RawGlyph getGlyph(int var1) {
      return null;
   }

   IntSet getSupportedGlyphs();
}
