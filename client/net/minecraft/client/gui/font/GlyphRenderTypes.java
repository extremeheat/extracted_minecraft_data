package net.minecraft.client.gui.font;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public record GlyphRenderTypes(RenderType a, RenderType b, RenderType c) {
   private final RenderType normal;
   private final RenderType seeThrough;
   private final RenderType polygonOffset;

   public GlyphRenderTypes(RenderType var1, RenderType var2, RenderType var3) {
      super();
      this.normal = var1;
      this.seeThrough = var2;
      this.polygonOffset = var3;
   }

   public static GlyphRenderTypes createForIntensityTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.textIntensity(var0), RenderType.textIntensitySeeThrough(var0), RenderType.textIntensityPolygonOffset(var0));
   }

   public static GlyphRenderTypes createForColorTexture(ResourceLocation var0) {
      return new GlyphRenderTypes(RenderType.text(var0), RenderType.textSeeThrough(var0), RenderType.textPolygonOffset(var0));
   }

   public RenderType select(Font.DisplayMode var1) {
      return switch(var1) {
         case NORMAL -> this.normal;
         case SEE_THROUGH -> this.seeThrough;
         case POLYGON_OFFSET -> this.polygonOffset;
      };
   }
}
