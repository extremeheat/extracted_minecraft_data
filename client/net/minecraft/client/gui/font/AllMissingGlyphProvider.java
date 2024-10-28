package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;

public class AllMissingGlyphProvider implements GlyphProvider {
   public AllMissingGlyphProvider() {
      super();
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      return SpecialGlyphs.MISSING;
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.EMPTY_SET;
   }
}
