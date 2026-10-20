package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;

public record DebugPrimitiveRenderPipelineVariant(String name, PrimitiveTopology primitiveTopology) {
   public static final DebugPrimitiveRenderPipelineVariant POINTS;
   public static final DebugPrimitiveRenderPipelineVariant QUADS;
   public static final DebugPrimitiveRenderPipelineVariant TRIANGLE_FAN;

   public DebugPrimitiveRenderPipelineVariant {
      super();
   }

   static {
      POINTS = new DebugPrimitiveRenderPipelineVariant("points", PrimitiveTopology.POINTS);
      QUADS = new DebugPrimitiveRenderPipelineVariant("quads", PrimitiveTopology.QUADS);
      TRIANGLE_FAN = new DebugPrimitiveRenderPipelineVariant("triangle_fan", PrimitiveTopology.TRIANGLE_FAN);
   }
}
