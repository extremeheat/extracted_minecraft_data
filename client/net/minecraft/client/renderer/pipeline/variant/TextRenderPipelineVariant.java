package net.minecraft.client.renderer.pipeline.variant;

import java.util.List;

public record TextRenderPipelineVariant(String name, boolean supportsOit, boolean isGrayscale, boolean hasPolygonOffset, boolean affectedByFogAndLight) implements RenderPipelineVariant {
   private static final VariantValuesList<TextRenderPipelineVariant> VALUES = new VariantValuesList<TextRenderPipelineVariant>();
   public static final TextRenderPipelineVariant WORLD;
   public static final TextRenderPipelineVariant WORLD_GRAYSCALE;
   public static final TextRenderPipelineVariant GUI;
   public static final TextRenderPipelineVariant GUI_GRAYSCALE;
   public static final TextRenderPipelineVariant WORLD_POLYGON_OFFSET;
   public static final TextRenderPipelineVariant WORLD_GRAYSCALE_POLYGON_OFFSET;
   public static final TextRenderPipelineVariant WORLD_SEE_THROUGH;
   public static final TextRenderPipelineVariant WORLD_GRAYSCALE_SEE_THROUGH;

   public TextRenderPipelineVariant {
      super();
   }

   public static List<TextRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      WORLD = VALUES.add(new TextRenderPipelineVariant("world", true, false, false, true));
      WORLD_GRAYSCALE = VALUES.add(new TextRenderPipelineVariant("world_grayscale", true, true, false, true));
      GUI = VALUES.add(new TextRenderPipelineVariant("gui", false, false, false, false));
      GUI_GRAYSCALE = VALUES.add(new TextRenderPipelineVariant("gui_grayscale", false, true, false, false));
      WORLD_POLYGON_OFFSET = VALUES.add(new TextRenderPipelineVariant("world_polygon_offset", true, false, true, true));
      WORLD_GRAYSCALE_POLYGON_OFFSET = VALUES.add(new TextRenderPipelineVariant("world_grayscale_polygon_offset", true, true, true, true));
      WORLD_SEE_THROUGH = VALUES.add(new TextRenderPipelineVariant("world_see_through", false, false, false, false));
      WORLD_GRAYSCALE_SEE_THROUGH = VALUES.add(new TextRenderPipelineVariant("world_grayscale_see_through", false, true, false, false));
   }
}
