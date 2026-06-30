package net.minecraft.client.renderer;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.UniformType;

public class BindGroupLayouts {
   public static final BindGroupLayout DYNAMIC_TRANSFORMS;
   public static final BindGroupLayout PROJECTION;
   public static final BindGroupLayout CHUNK_SECTION;
   public static final BindGroupLayout FOG;
   public static final BindGroupLayout GLOBALS;
   public static final BindGroupLayout LIGHTING;
   public static final BindGroupLayout SAMPLER0;
   public static final BindGroupLayout SAMPLER1;
   public static final BindGroupLayout SAMPLER2;
   public static final BindGroupLayout SAMPLER0_SAMPLER2;
   public static final BindGroupLayout SAMPLER0_SAMPLER1;
   public static final BindGroupLayout SAMPLER0_SAMPLER1_SAMPLER2;
   public static final BindGroupLayout CLOUD_INFO;
   public static final BindGroupLayout DISSOLVE_MASK_SAMPLER;
   public static final BindGroupLayout IN_SAMPLER;
   public static final BindGroupLayout LIGHTMAP_INFO;
   public static final BindGroupLayout SPRITE_ANIMATION_INFO;
   public static final BindGroupLayout SPRITE;
   public static final BindGroupLayout CURRENT_SPRITE_NEXT_SPRITE;
   public static final BindGroupLayout GLINT_SAMPLER;
   public static final BindGroupLayout DEPTH_BOUNDS_SAMPLER;
   public static final BindGroupLayout OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;
   public static final BindGroupLayout SAMPLER0_OIT_COEFFS;
   public static final BindGroupLayout SAMPLER0_SAMPLER2_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;
   public static final BindGroupLayout CLOUD_INFO_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;

   private BindGroupLayouts() {
      super();
   }

   static {
      DYNAMIC_TRANSFORMS = BindGroupLayout.builder().withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER).build();
      PROJECTION = BindGroupLayout.builder().withUniform("Projection", UniformType.UNIFORM_BUFFER).build();
      CHUNK_SECTION = BindGroupLayout.builder().withUniform("ChunkSection", UniformType.UNIFORM_BUFFER).build();
      FOG = BindGroupLayout.builder().withUniform("Fog", UniformType.UNIFORM_BUFFER).build();
      GLOBALS = BindGroupLayout.builder().withUniform("Globals", UniformType.UNIFORM_BUFFER).build();
      LIGHTING = BindGroupLayout.builder().withUniform("Lighting", UniformType.UNIFORM_BUFFER).build();
      SAMPLER0 = BindGroupLayout.builder().withSampler("Sampler0").build();
      SAMPLER1 = BindGroupLayout.builder().withSampler("Sampler1").build();
      SAMPLER2 = BindGroupLayout.builder().withSampler("Sampler2").build();
      SAMPLER0_SAMPLER2 = BindGroupLayout.builder().withSampler("Sampler0").withSampler("Sampler2").build();
      SAMPLER0_SAMPLER1 = BindGroupLayout.builder().withSampler("Sampler0").withSampler("Sampler1").build();
      SAMPLER0_SAMPLER1_SAMPLER2 = BindGroupLayout.builder().withSampler("Sampler0").withSampler("Sampler1").withSampler("Sampler2").build();
      CLOUD_INFO = BindGroupLayout.builder().withUniform("CloudInfo", UniformType.UNIFORM_BUFFER).withUniform("CloudFaces", UniformType.TEXEL_BUFFER, GpuFormat.R8_SINT).build();
      DISSOLVE_MASK_SAMPLER = BindGroupLayout.builder().withSampler("DissolveMaskSampler").build();
      IN_SAMPLER = BindGroupLayout.builder().withSampler("InSampler").build();
      LIGHTMAP_INFO = BindGroupLayout.builder().withUniform("LightmapInfo", UniformType.UNIFORM_BUFFER).build();
      SPRITE_ANIMATION_INFO = BindGroupLayout.builder().withUniform("SpriteAnimationInfo", UniformType.UNIFORM_BUFFER).build();
      SPRITE = BindGroupLayout.builder().withSampler("Sprite").build();
      CURRENT_SPRITE_NEXT_SPRITE = BindGroupLayout.builder().withSampler("CurrentSprite").withSampler("NextSprite").build();
      GLINT_SAMPLER = BindGroupLayout.builder().withSampler("GlintSampler").build();
      DEPTH_BOUNDS_SAMPLER = BindGroupLayout.builder().withSampler("DepthBoundsSampler").build();
      BindGroupLayout.Builder builder = BindGroupLayout.builder().withSampler("DepthBoundsSampler");

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withSampler("Coeff" + i);
      }

      OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
      builder = BindGroupLayout.builder().withSampler("Sampler0");

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withSampler("Coeff" + i);
      }

      SAMPLER0_OIT_COEFFS = builder.build();
      builder = BindGroupLayout.builder().withSampler("Sampler0").withSampler("Sampler2").withSampler("DepthBoundsSampler");

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withSampler("Coeff" + i);
      }

      SAMPLER0_SAMPLER2_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
      builder = BindGroupLayout.builder().withUniform("CloudInfo", UniformType.UNIFORM_BUFFER).withUniform("CloudFaces", UniformType.TEXEL_BUFFER, GpuFormat.R8_SINT).withSampler("DepthBoundsSampler");

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withSampler("Coeff" + i);
      }

      CLOUD_INFO_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
   }
}
