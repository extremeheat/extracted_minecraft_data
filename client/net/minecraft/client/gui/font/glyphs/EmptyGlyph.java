package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.UnbakedGlyph;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.network.chat.Style;

public class EmptyGlyph implements UnbakedGlyph {
   final GlyphInfo info;

   public EmptyGlyph(float var1) {
      super();
      this.info = GlyphInfo.simple(var1);
   }

   public GlyphInfo info() {
      return this.info;
   }

   public BakedGlyph bake(UnbakedGlyph.Stitcher var1) {
      return new BakedGlyph() {
         public GlyphInfo info() {
            return EmptyGlyph.this.info;
         }

         @Nullable
         public TextRenderable createGlyph(float var1, float var2, int var3, int var4, Style var5, float var6, float var7) {
            return null;
         }
      };
   }
}
