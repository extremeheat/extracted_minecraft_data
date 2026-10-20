package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import java.util.List;

public record ParticleRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, DepthStencilState nonOitDepthStencilState, boolean supportsOit, boolean cull) implements RenderPipelineVariant {
   private static final VariantValuesList<ParticleRenderPipelineVariant> VALUES = new VariantValuesList<ParticleRenderPipelineVariant>();
   public static final ParticleRenderPipelineVariant OPAQUE;
   public static final ParticleRenderPipelineVariant TRANSLUCENT;
   public static final ParticleRenderPipelineVariant WEATHER;

   public ParticleRenderPipelineVariant {
      super();
   }

   public static List<ParticleRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      OPAQUE = VALUES.add(new ParticleRenderPipelineVariant("opaque", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, false, true));
      TRANSLUCENT = VALUES.add(new ParticleRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, true, true));
      WEATHER = VALUES.add(new ParticleRenderPipelineVariant("weather", new ColorTargetState(BlendFunction.TRANSLUCENT), new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false), true, false));
   }
}
