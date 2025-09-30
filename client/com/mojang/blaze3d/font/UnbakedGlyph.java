package com.mojang.blaze3d.font;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;

public interface UnbakedGlyph {
   GlyphInfo info();

   BakedGlyph bake(Stitcher var1);

   public interface Stitcher {
      BakedGlyph stitch(GlyphInfo var1, GlyphBitmap var2);

      BakedGlyph getMissing();
   }
}
