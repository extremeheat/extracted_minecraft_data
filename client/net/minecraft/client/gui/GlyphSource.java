package net.minecraft.client.gui;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.util.RandomSource;

public interface GlyphSource {
   BakedGlyph getGlyph(int var1);

   BakedGlyph getRandomGlyph(RandomSource var1, int var2);
}
