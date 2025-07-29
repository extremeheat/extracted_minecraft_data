package net.minecraft.client.gui;

import net.minecraft.client.gui.font.glyphs.BakeableGlyph;
import net.minecraft.util.RandomSource;

public interface GlyphSource {
   BakeableGlyph getGlyph(int var1);

   BakeableGlyph getRandomGlyph(RandomSource var1, int var2);
}
