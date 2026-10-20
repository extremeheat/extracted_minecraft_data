package net.minecraft.client.renderer.pipeline.variant;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import java.util.List;

public record EntityRenderPipelineVariant(String name, ColorTargetState nonOitColorTargetState, DepthStencilState nonOitDepthStencilState, Lighting lighting, boolean supportsOit, boolean hasOverlay, boolean hasAlphaCutout, boolean hasBackFaceCulling, boolean hasGlint, boolean isEmissive, boolean applyTextureMatrix, boolean dissolve, boolean additive) implements RenderPipelineVariant {
   private static final VariantValuesList<EntityRenderPipelineVariant> VALUES = new VariantValuesList<EntityRenderPipelineVariant>();
   public static final EntityRenderPipelineVariant SOLID;
   public static final EntityRenderPipelineVariant SOLID_GLINT;
   public static final EntityRenderPipelineVariant CUTOUT;
   public static final EntityRenderPipelineVariant CUTOUT_CULL;
   public static final EntityRenderPipelineVariant CUTOUT_DISSOLVE;
   public static final EntityRenderPipelineVariant ARMOR_CUTOUT_NO_CULL;
   public static final EntityRenderPipelineVariant ARMOR_CUTOUT_NO_CULL_GLINT;
   public static final EntityRenderPipelineVariant ARMOR_DECAL_CUTOUT_NO_CULL;
   public static final EntityRenderPipelineVariant WOLF_ARMOR_CRACKS;
   public static final EntityRenderPipelineVariant END_CRYSTAL_BEAM;
   public static final EntityRenderPipelineVariant BANNER_PATTERN;
   public static final EntityRenderPipelineVariant TRANSLUCENT;
   public static final EntityRenderPipelineVariant TRANSLUCENT_CULL;
   public static final EntityRenderPipelineVariant TRANSLUCENT_EMISSIVE;
   public static final EntityRenderPipelineVariant BREEZE_WIND;
   public static final EntityRenderPipelineVariant EYES;
   public static final EntityRenderPipelineVariant ENERGY_SWIRL;

   public EntityRenderPipelineVariant {
      super();
   }

   public static List<EntityRenderPipelineVariant> values() {
      return VALUES.values();
   }

   static {
      SOLID = VALUES.add(new EntityRenderPipelineVariant("solid", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.DEFAULT, false, true, false, true, false, false, false, false, false));
      SOLID_GLINT = VALUES.add(new EntityRenderPipelineVariant("solid_glint", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.DEFAULT, false, true, false, true, true, false, false, false, false));
      CUTOUT = VALUES.add(new EntityRenderPipelineVariant("cutout", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, false, true, true, false, false, false, false, false, false));
      CUTOUT_CULL = VALUES.add(new EntityRenderPipelineVariant("cutout_cull", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.DEFAULT, false, true, true, true, false, false, false, false, false));
      CUTOUT_DISSOLVE = VALUES.add(new EntityRenderPipelineVariant("cutout_dissolve", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, false, true, true, false, false, false, false, true, false));
      ARMOR_CUTOUT_NO_CULL = VALUES.add(new EntityRenderPipelineVariant("armor_cutout_no_cull", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, false, false, true, false, false, false, false, false, false));
      ARMOR_CUTOUT_NO_CULL_GLINT = VALUES.add(new EntityRenderPipelineVariant("armor_cutout_no_cull_glint", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, false, false, true, false, true, false, false, false, false));
      ARMOR_DECAL_CUTOUT_NO_CULL = VALUES.add(new EntityRenderPipelineVariant("armor_decal_cutout_no_cull", ColorTargetState.DEFAULT, new DepthStencilState(CompareOp.EQUAL, false), EntityRenderPipelineVariant.Lighting.PER_FACE, false, false, true, false, false, false, false, false, false));
      WOLF_ARMOR_CRACKS = VALUES.add(new EntityRenderPipelineVariant("wolf_armor_cracks", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, false, false, true, false, false, false, false, false, false));
      END_CRYSTAL_BEAM = VALUES.add(new EntityRenderPipelineVariant("end_crystal_beam", ColorTargetState.DEFAULT, DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.DEFAULT, false, false, true, false, false, false, false, false, false));
      BANNER_PATTERN = VALUES.add(new EntityRenderPipelineVariant("banner_pattern", new ColorTargetState(BlendFunction.TRANSLUCENT), new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false), EntityRenderPipelineVariant.Lighting.DEFAULT, false, false, false, true, false, false, false, false, false));
      TRANSLUCENT = VALUES.add(new EntityRenderPipelineVariant("translucent", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.PER_FACE, true, true, true, false, false, false, false, false, false));
      TRANSLUCENT_CULL = VALUES.add(new EntityRenderPipelineVariant("translucent_cull", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.DEFAULT, true, true, true, true, false, false, false, false, false));
      TRANSLUCENT_EMISSIVE = VALUES.add(new EntityRenderPipelineVariant("translucent_emissive", new ColorTargetState(BlendFunction.TRANSLUCENT), new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false), EntityRenderPipelineVariant.Lighting.PER_FACE, true, true, true, false, false, true, false, false, false));
      BREEZE_WIND = VALUES.add(new EntityRenderPipelineVariant("breeze_wind", new ColorTargetState(BlendFunction.TRANSLUCENT), DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.NONE, true, false, true, false, false, false, true, false, false));
      EYES = VALUES.add(new EntityRenderPipelineVariant("eyes", new ColorTargetState(BlendFunction.TRANSLUCENT), new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false), EntityRenderPipelineVariant.Lighting.NONE, true, false, true, true, false, true, false, false, false));
      ENERGY_SWIRL = VALUES.add(new EntityRenderPipelineVariant("energy_swirl", new ColorTargetState(BlendFunction.ADDITIVE), DepthStencilState.DEFAULT, EntityRenderPipelineVariant.Lighting.NONE, true, false, true, false, false, true, true, false, true));
   }

   public static enum Lighting {
      NONE,
      DEFAULT,
      PER_FACE;

      private Lighting() {
      }

      // $FF: synthetic method
      private static Lighting[] $values() {
         return new Lighting[]{NONE, DEFAULT, PER_FACE};
      }
   }
}
