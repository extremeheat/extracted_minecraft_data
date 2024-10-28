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
      RenderType var10000;
      switch (var1) {
         case NORMAL -> var10000 = this.normal;
         case SEE_THROUGH -> var10000 = this.seeThrough;
         case POLYGON_OFFSET -> var10000 = this.polygonOffset;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public RenderType normal() {
      return this.normal;
   }

   public RenderType seeThrough() {
      return this.seeThrough;
   }

   public RenderType polygonOffset() {
      return this.polygonOffset;
   }
}
