package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.UnbakedGlyph;
import java.util.Objects;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public class EmptyGlyph implements UnbakedGlyph {
   private final GlyphInfo info;

   public EmptyGlyph(final float advance) {
      super();
      this.info = GlyphInfo.simple(advance);
   }

   public GlyphInfo info() {
      return this.info;
   }

   public BakedGlyph bake(final UnbakedGlyph.Stitcher stitcher) {
      return new BakedGlyph() {
         {
            Objects.requireNonNull(EmptyGlyph.this);
         }

         public GlyphInfo info() {
            return EmptyGlyph.this.info;
         }

         public TextRenderable.@Nullable Styled createGlyph(final float x, final float y, final int color, final int shadowColor, final Style style, final float boldOffset, final float shadowOffset) {
            return null;
         }
      };
   }
}
