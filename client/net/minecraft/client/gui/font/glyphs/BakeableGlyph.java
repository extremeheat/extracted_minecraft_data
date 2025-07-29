package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;

public interface BakeableGlyph {
   GlyphInfo info();

   BakedGlyph baked();
}
