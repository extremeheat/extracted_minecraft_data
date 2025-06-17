package net.minecraft.client.gui.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset, RenderPipeline guiPipeline) {
   public GlyphRenderTypes(RenderType var1, RenderType var2, RenderType var3, RenderPipeline var4) {
      super();
      this.normal = var1;
      this.seeThrough = var2;
      this.polygonOffset = var3;
      this.guiPipeline = var4;
   }

   public static GlyphRenderTypes createForIntensityTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.textIntensity(var0), RenderType.textIntensitySeeThrough(var0), RenderType.textIntensityPolygonOffset(var0), RenderPipelines.TEXT_INTENSITY);
   }

   public static GlyphRenderTypes createForColorTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.text(var0), RenderType.textSeeThrough(var0), RenderType.textPolygonOffset(var0), RenderPipelines.TEXT);
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
