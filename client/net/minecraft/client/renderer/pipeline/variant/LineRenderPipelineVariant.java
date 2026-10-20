package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import java.util.List;

public record LineRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, DepthStencilState nonOitDepthStencilState, boolean supportsOit) implements RenderPipelineVariant {
   private static final VariantValuesList<LineRenderPipelineVariant> VALUES = new VariantValuesList<LineRenderPipelineVariant>();
   public static final LineRenderPipelineVariant SOLID;
   public static final LineRenderPipelineVariant SOLID_WITH_DEPTH_BIAS;
   public static final LineRenderPipelineVariant TRANSLUCENT;
   public static final LineRenderPipelineVariant TRANSLUCENT_NO_DEPTH_WRITE;

   public LineRenderPipelineVariant {
      super();
   }

   public static List<LineRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      SOLID = VALUES.add(new LineRenderPipelineVariant("solid", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, false));
      SOLID_WITH_DEPTH_BIAS = VALUES.add(new LineRenderPipelineVariant("solid_with_depth_bias", ColorTargetState.DEFAULT, new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 1.0F, 1.0F), true));
      TRANSLUCENT = VALUES.add(new LineRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, false));
      TRANSLUCENT_NO_DEPTH_WRITE = VALUES.add(new LineRenderPipelineVariant("translucent_no_depth_write", new ColorTargetState(BlendFunction.TRANSLUCENT), new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false), true));
   }
}
