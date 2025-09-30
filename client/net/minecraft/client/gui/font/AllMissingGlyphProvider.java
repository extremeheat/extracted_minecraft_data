package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.UnbakedGlyph;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;

public class AllMissingGlyphProvider implements GlyphProvider {
   private static final UnbakedGlyph MISSING_INSTANCE = new UnbakedGlyph() {
      public GlyphInfo info() {
         return SpecialGlyphs.MISSING;
      }

      public BakedGlyph bake(UnbakedGlyph.Stitcher var1) {
         return var1.getMissing();
      }
   };

   public AllMissingGlyphProvider() {
      super();
   }

   @Nullable
   public UnbakedGlyph getGlyph(int var1) {
      return MISSING_INSTANCE;
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.EMPTY_SET;
   }
}
