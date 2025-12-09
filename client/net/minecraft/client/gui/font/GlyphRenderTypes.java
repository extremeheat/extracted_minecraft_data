package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderPipeline guiPipeline) {
   public GlyphRenderTypes(RenderType var1, RenderType var2, RenderType var3, RenderPipeline var4) {
      super();
      this.normal = var1;
      this.seeThrough = var2;
      this.polygonOffset = var3;
      this.guiPipeline = var4;
   }

   public static GlyphRenderTypes createForIntensityTexture(Identifier var0) {
      return new GlyphRenderTypes(RenderTypes.textIntensity(var0), RenderTypes.textIntensitySeeThrough(var0), RenderTypes.textIntensityPolygonOffset(var0), RenderPipelines.GUI_TEXT_INTENSITY);
   }

   public static GlyphRenderTypes createForColorTexture(Identifier var0) {
      return new GlyphRenderTypes(RenderTypes.text(var0), RenderTypes.textSeeThrough(var0), RenderTypes.textPolygonOffset(var0), RenderPipelines.GUI_TEXT);
   }

   public RenderType select(Font.DisplayMode var1) {
      RenderType var10000;
      switch (var1) {
         case NORMAL -> var10000 = this.normal;
         case SEE_THROUGH -> var10000 = this.seeThrough;
         case POLYGON_OFFSET -> var10000 = this.polygonOffset;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
