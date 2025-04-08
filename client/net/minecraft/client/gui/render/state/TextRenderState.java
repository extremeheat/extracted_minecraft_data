package net.minecraft.client.gui.render.state;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;

public record TextRenderState(boolean drawShadow, int color, int backgroundColor, Font.DisplayMode mode, int packedLightCoords, List<BakedGlyph.GlyphInstance> glyphInstances, @Nullable List<BakedGlyph.Effect> effects, int lineHeight, BakedGlyph whiteGlyph, float endX) {
   public TextRenderState(boolean var1, int var2, int var3, Font.DisplayMode var4, int var5, List<BakedGlyph.GlyphInstance> var6, @Nullable List<BakedGlyph.Effect> var7, int var8, BakedGlyph var9, float var10) {
      super();
      this.drawShadow = var1;
      this.color = var2;
      this.backgroundColor = var3;
      this.mode = var4;
      this.packedLightCoords = var5;
      this.glyphInstances = var6;
      this.effects = var7;
      this.lineHeight = var8;
      this.whiteGlyph = var9;
      this.endX = var10;
   }
}
