package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import java.util.List;

public record ItemRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, boolean supportsOit, GlintType glintType) implements RenderPipelineVariant {
   private static final VariantValuesList<ItemRenderPipelineVariant> VALUES = new VariantValuesList<ItemRenderPipelineVariant>();
   public static final ItemRenderPipelineVariant CUTOUT;
   public static final ItemRenderPipelineVariant CUTOUT_GLINT;
   public static final ItemRenderPipelineVariant CUTOUT_GLINT_SPECIAL;
   public static final ItemRenderPipelineVariant TRANSLUCENT;
   public static final ItemRenderPipelineVariant TRANSLUCENT_GLINT;
   public static final ItemRenderPipelineVariant TRANSLUCENT_GLINT_SPECIAL;

   public ItemRenderPipelineVariant {
      super();
   }

   public static List<ItemRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      CUTOUT = VALUES.add(new ItemRenderPipelineVariant("cutout", ColorTargetState.DEFAULT, false, ItemRenderPipelineVariant.GlintType.NONE));
      CUTOUT_GLINT = VALUES.add(new ItemRenderPipelineVariant("cutout_glint", ColorTargetState.DEFAULT, false, ItemRenderPipelineVariant.GlintType.NORMAL));
      CUTOUT_GLINT_SPECIAL = VALUES.add(new ItemRenderPipelineVariant("cutout_glint_special", ColorTargetState.DEFAULT, false, ItemRenderPipelineVariant.GlintType.SPECIAL));
      TRANSLUCENT = VALUES.add(new ItemRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), true, ItemRenderPipelineVariant.GlintType.NONE));
      TRANSLUCENT_GLINT = VALUES.add(new ItemRenderPipelineVariant("translucent_glint", new ColorTargetState(BlendFunction.TRANSLUCENT), true, ItemRenderPipelineVariant.GlintType.NORMAL));
      TRANSLUCENT_GLINT_SPECIAL = VALUES.add(new ItemRenderPipelineVariant("translucent_glint_special", new ColorTargetState(BlendFunction.TRANSLUCENT), true, ItemRenderPipelineVariant.GlintType.SPECIAL));
   }

   public static enum GlintType {
      NONE,
      NORMAL,
      SPECIAL;

      private GlintType() {
      }

      // $FF: synthetic method
      private static GlintType[] $values() {
         return new GlintType[]{NONE, NORMAL, SPECIAL};
      }
   }
}
