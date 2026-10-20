package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import java.util.List;

public record BlockRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, boolean supportsOit, boolean hasAlphaCutout, float alphaCutoutThreshold) implements RenderPipelineVariant {
   private static final VariantValuesList<BlockRenderPipelineVariant> VALUES = new VariantValuesList<BlockRenderPipelineVariant>();
   public static final BlockRenderPipelineVariant SOLID;
   public static final BlockRenderPipelineVariant CUTOUT;
   public static final BlockRenderPipelineVariant TRANSLUCENT;

   public BlockRenderPipelineVariant {
      super();
   }

   public static List<BlockRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      SOLID = VALUES.add(new BlockRenderPipelineVariant("solid", ColorTargetState.DEFAULT, false, false, 0.0F));
      CUTOUT = VALUES.add(new BlockRenderPipelineVariant("cutout", ColorTargetState.DEFAULT, false, true, 0.5F));
      TRANSLUCENT = VALUES.add(new BlockRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), true, true, 0.1F));
   }
}
