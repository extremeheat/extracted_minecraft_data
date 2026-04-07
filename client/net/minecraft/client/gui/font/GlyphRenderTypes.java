package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderPipeline guiPipeline) {
   public GlyphRenderTypes {
      super();
   }

   public static GlyphRenderTypes createForGrayscaleTexture(final Identifier name) {
      return new GlyphRenderTypes(RenderTypes.textGrayscale(name), RenderTypes.textGrayscaleSeeThrough(name), RenderTypes.textGrayscalePolygonOffset(name), RenderPipelines.GUI_TEXT_GRAYSCALE);
   }

   public static GlyphRenderTypes createForColorTexture(final Identifier name) {
      return new GlyphRenderTypes(RenderTypes.text(name), RenderTypes.textSeeThrough(name), RenderTypes.textPolygonOffset(name), RenderPipelines.GUI_TEXT);
   }

   public RenderType select(final Font.DisplayMode mode) {
      RenderType var10000;
      switch (mode) {
         case NORMAL -> var10000 = this.normal;
         case SEE_THROUGH -> var10000 = this.seeThrough;
         case POLYGON_OFFSET -> var10000 = this.polygonOffset;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
