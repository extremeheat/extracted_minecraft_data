package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import java.util.List;

public record TerrainRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, boolean supportsOit, boolean hasAlphaCutout, float alphaCutoutThreshold, int vertexFaceSize, int vertexBakedFaceSize) implements RenderPipelineVariant {
   private static final VariantValuesList<TerrainRenderPipelineVariant> VALUES = new VariantValuesList<TerrainRenderPipelineVariant>();
   public static final TerrainRenderPipelineVariant SOLID;
   public static final TerrainRenderPipelineVariant CUTOUT;
   public static final TerrainRenderPipelineVariant TRANSLUCENT;

   public TerrainRenderPipelineVariant {
      super();
   }

   public static List<TerrainRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      SOLID = VALUES.add(new TerrainRenderPipelineVariant("solid", ColorTargetState.DEFAULT, false, false, 0.0F, 32, 128));
      CUTOUT = VALUES.add(new TerrainRenderPipelineVariant("cutout", ColorTargetState.DEFAULT, false, true, 0.5F, 32, 128));
      TRANSLUCENT = VALUES.add(new TerrainRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), true, true, 0.1F, 32, 128));
   }
}
