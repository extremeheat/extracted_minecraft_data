package net.minecraft.client.gui.font;

import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.util.RandomSource;

public record SingleSpriteSource(BakedGlyph glyph) implements GlyphSource {
   public SingleSpriteSource {
      super();
   }

   public BakedGlyph getGlyph(final int codepoint) {
      return this.glyph;
   }

   public BakedGlyph getRandomGlyph(final RandomSource random, final int width) {
      return this.glyph;
   }
}
