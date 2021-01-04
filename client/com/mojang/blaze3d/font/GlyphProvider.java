package com.mojang.blaze3d.font;

import java.io.Closeable;
import javax.annotation.Nullable;

public interface GlyphProvider extends Closeable {
   default void close() {
   }

   @Nullable
   default RawGlyph getGlyph(char var1) {
      return null;
   }
}
