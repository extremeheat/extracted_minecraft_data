package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import java.util.List;
import java.util.Optional;

public record GuiRenderPipelineVariant(String name, boolean isTextured, ColorTargetState colorTargetState) implements RenderPipelineVariant {
   private static final VariantValuesList<GuiRenderPipelineVariant> VALUES = new VariantValuesList<GuiRenderPipelineVariant>();
   public static final GuiRenderPipelineVariant DEFAULT;
   public static final GuiRenderPipelineVariant INVERT;
   public static final GuiRenderPipelineVariant TEXT_HIGHLIGHT;
   public static final GuiRenderPipelineVariant TEXTURED;
   public static final GuiRenderPipelineVariant TEXTURED_PREMULTIPLIED_ALPHA;
   public static final GuiRenderPipelineVariant OPAQUE_TEXTURED_BACKGROUND;
   public static final GuiRenderPipelineVariant NAUSEA_OVERLAY;
   public static final GuiRenderPipelineVariant VIGNETTE;
   public static final GuiRenderPipelineVariant CROSSHAIR;
   public static final GuiRenderPipelineVariant MOJANG_LOGO;

   public GuiRenderPipelineVariant {
      super();
   }

   public static List<GuiRenderPipelineVariant> values() {
      return VALUES.values();
   }

   public boolean supportsOit() {
      return false;
   }

   static {
      DEFAULT = VALUES.add(new GuiRenderPipelineVariant("default", false, new ColorTargetState(BlendFunction.TRANSLUCENT)));
      INVERT = VALUES.add(new GuiRenderPipelineVariant("invert", false, new ColorTargetState(BlendFunction.INVERT)));
      TEXT_HIGHLIGHT = VALUES.add(new GuiRenderPipelineVariant("text_highlight", false, new ColorTargetState(BlendFunction.ADDITIVE)));
      TEXTURED = VALUES.add(new GuiRenderPipelineVariant("textured", true, new ColorTargetState(BlendFunction.TRANSLUCENT)));
      TEXTURED_PREMULTIPLIED_ALPHA = VALUES.add(new GuiRenderPipelineVariant("textured_premultiplied_alpha", true, new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)));
      OPAQUE_TEXTURED_BACKGROUND = VALUES.add(new GuiRenderPipelineVariant("opaque_textured_background", true, new ColorTargetState(Optional.empty(), 15)));
      NAUSEA_OVERLAY = VALUES.add(new GuiRenderPipelineVariant("nausea_overlay", true, new ColorTargetState(BlendFunction.ADDITIVE)));
      VIGNETTE = VALUES.add(new GuiRenderPipelineVariant("vignette", true, new ColorTargetState(new BlendFunction(BlendFactor.ZERO, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.ZERO, BlendFactor.ONE))));
      CROSSHAIR = VALUES.add(new GuiRenderPipelineVariant("crosshair", true, new ColorTargetState(BlendFunction.INVERT)));
      MOJANG_LOGO = VALUES.add(new GuiRenderPipelineVariant("mojang_logo", true, new ColorTargetState(new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE))));
   }
}
