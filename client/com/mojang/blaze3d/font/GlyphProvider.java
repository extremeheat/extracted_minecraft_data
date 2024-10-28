package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.FontOption;

public interface GlyphProvider extends AutoCloseable {
   float BASELINE = 7.0F;

   default void close() {
   }

   @Nullable
   default GlyphInfo getGlyph(int var1) {
      return null;
   }

   IntSet getSupportedGlyphs();

   public static record Conditional(GlyphProvider provider, FontOption.Filter filter) implements AutoCloseable {
      public Conditional(GlyphProvider var1, FontOption.Filter var2) {
         super();
         this.provider = var1;
         this.filter = var2;
      }

      public void close() {
         this.provider.close();
      }

      public GlyphProvider provider() {
         return this.provider;
      }

      public FontOption.Filter filter() {
         return this.filter;
      }
   }
}
