package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.FontOption;
import org.jspecify.annotations.Nullable;

public interface GlyphProvider extends AutoCloseable {
   float BASELINE = 7.0F;

   default void close() {
   }

   default @Nullable UnbakedGlyph getGlyph(final int codepoint) {
      return null;
   }

   IntSet getSupportedGlyphs();

   public static record Conditional(GlyphProvider provider, FontOption.Filter filter) implements AutoCloseable {
      public Conditional {
         super();
      }

      public void close() {
         this.provider.close();
      }
   }
}
