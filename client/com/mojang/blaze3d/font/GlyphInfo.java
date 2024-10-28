package com.mojang.blaze3d.font;

import java.util.function.Function;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;

public interface GlyphInfo {
   float getAdvance();

   default float getAdvance(boolean var1) {
      return this.getAdvance() + (var1 ? this.getBoldOffset() : 0.0F);
   }

   default float getBoldOffset() {
      return 1.0F;
   }

   default float getShadowOffset() {
      return 1.0F;
   }

   BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1);

   public interface SpaceGlyphInfo extends GlyphInfo {
      default BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return EmptyGlyph.INSTANCE;
      }
   }
}
