package net.minecraft.client.gui.font;

import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.util.RandomSource;

public record SingleSpriteSource(BakedGlyph glyph) implements GlyphSource {
   public SingleSpriteSource(BakedGlyph var1) {
      super();
      this.glyph = var1;
   }

   public BakedGlyph getGlyph(int var1) {
      return this.glyph;
   }

   public BakedGlyph getRandomGlyph(RandomSource var1, int var2) {
      return this.glyph;
   }
}
