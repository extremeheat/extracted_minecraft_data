package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;

public class AllMissingGlyphProvider implements GlyphProvider {
   public AllMissingGlyphProvider() {
      super();
   }

   @Nullable
   public RawGlyph getGlyph(char var1) {
      return MissingGlyph.INSTANCE;
   }
}
