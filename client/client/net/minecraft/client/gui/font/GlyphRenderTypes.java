package net.minecraft.client.gui.font;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public record GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset) {
   public GlyphRenderTypes(RenderType normal, RenderType seeThrough, RenderType polygonOffset) {
      super();
      this.normal = normal;
      this.seeThrough = seeThrough;
      this.polygonOffset = polygonOffset;
   }

   public static GlyphRenderTypes createForIntensityTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.textIntensity(var0), RenderType.textIntensitySeeThrough(var0), RenderType.textIntensityPolygonOffset(var0));
   }

   public static GlyphRenderTypes createForColorTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.text(var0), RenderType.textSeeThrough(var0), RenderType.textPolygonOffset(var0));
   }

   public RenderType select(Font.DisplayMode var1) {
      return switch (var1) {
         case NORMAL -> this.normal;
         case SEE_THROUGH -> this.seeThrough;
         case POLYGON_OFFSET -> this.polygonOffset;
      };
   }
}
