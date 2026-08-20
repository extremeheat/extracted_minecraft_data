package net.minecraft.client.renderer;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.UniformType;

public class BindGroupLayouts {
   public static final BindGroupLayout DYNAMIC_TRANSFORMS;
   public static final BindGroupLayout PROJECTION;
   public static final BindGroupLayout CHUNK_SECTION;
   public static final BindGroupLayout TERRAIN_INFO;
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
   public static final BindGroupLayout SAMPLER0_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;
   public static final BindGroupLayout SAMPLER0_SAMPLER2_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;
   public static final BindGroupLayout CLOUD_INFO_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER;

   private BindGroupLayouts() {
      super();
   }

   static {
      DYNAMIC_TRANSFORMS = BindGroupLayout.builder().withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER).build();
      PROJECTION = BindGroupLayout.builder().withUniform("Projection", UniformType.UNIFORM_BUFFER).build();
      CHUNK_SECTION = BindGroupLayout.builder().withUniform("ChunkSection", UniformType.UNIFORM_BUFFER).build();
      TERRAIN_INFO = BindGroupLayout.builder().withUniform("TerrainUniform", UniformType.UNIFORM_BUFFER).build();
      FOG = BindGroupLayout.builder().withUniform("Fog", UniformType.UNIFORM_BUFFER).build();
      GLOBALS = BindGroupLayout.builder().withUniform("Globals", UniformType.UNIFORM_BUFFER).build();
      LIGHTING = BindGroupLayout.builder().withUniform("Lighting", UniformType.UNIFORM_BUFFER).build();
      SAMPLER0 = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build();
      SAMPLER1 = BindGroupLayout.builder().withUniform("Sampler1", UniformType.COMBINED_IMAGE_SAMPLER).build();
      SAMPLER2 = BindGroupLayout.builder().withUniform("Sampler2", UniformType.COMBINED_IMAGE_SAMPLER).build();
      SAMPLER0_SAMPLER2 = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("Sampler2", UniformType.COMBINED_IMAGE_SAMPLER).build();
      SAMPLER0_SAMPLER1 = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("Sampler1", UniformType.COMBINED_IMAGE_SAMPLER).build();
      SAMPLER0_SAMPLER1_SAMPLER2 = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("Sampler1", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("Sampler2", UniformType.COMBINED_IMAGE_SAMPLER).build();
      CLOUD_INFO = BindGroupLayout.builder().withUniform("CloudInfo", UniformType.UNIFORM_BUFFER).withUniform("CloudFaces", UniformType.TEXEL_BUFFER, GpuFormat.R8_SINT).build();
      DISSOLVE_MASK_SAMPLER = BindGroupLayout.builder().withUniform("DissolveMaskSampler", UniformType.COMBINED_IMAGE_SAMPLER).build();
      IN_SAMPLER = BindGroupLayout.builder().withUniform("InSampler", UniformType.COMBINED_IMAGE_SAMPLER).build();
      LIGHTMAP_INFO = BindGroupLayout.builder().withUniform("LightmapInfo", UniformType.UNIFORM_BUFFER).build();
      SPRITE_ANIMATION_INFO = BindGroupLayout.builder().withUniform("SpriteAnimationInfo", UniformType.UNIFORM_BUFFER).build();
      SPRITE = BindGroupLayout.builder().withUniform("Sprite", UniformType.COMBINED_IMAGE_SAMPLER).build();
      CURRENT_SPRITE_NEXT_SPRITE = BindGroupLayout.builder().withUniform("CurrentSprite", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("NextSprite", UniformType.COMBINED_IMAGE_SAMPLER).build();
      GLINT_SAMPLER = BindGroupLayout.builder().withUniform("GlintSampler", UniformType.COMBINED_IMAGE_SAMPLER).build();
      DEPTH_BOUNDS_SAMPLER = BindGroupLayout.builder().withUniform("DepthBoundsSampler", UniformType.COMBINED_IMAGE_SAMPLER).build();
      BindGroupLayout.Builder builder = BindGroupLayout.builder().withUniform("DepthBoundsSampler", UniformType.COMBINED_IMAGE_SAMPLER);

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withUniform("Coeff" + i, UniformType.COMBINED_IMAGE_SAMPLER);
      }

      OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
      builder = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("DepthBoundsSampler", UniformType.COMBINED_IMAGE_SAMPLER);

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withUniform("Coeff" + i, UniformType.COMBINED_IMAGE_SAMPLER);
      }

      SAMPLER0_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
      builder = BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("Sampler2", UniformType.COMBINED_IMAGE_SAMPLER).withUniform("DepthBoundsSampler", UniformType.COMBINED_IMAGE_SAMPLER);

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withUniform("Coeff" + i, UniformType.COMBINED_IMAGE_SAMPLER);
      }

      SAMPLER0_SAMPLER2_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
      builder = BindGroupLayout.builder().withUniform("CloudInfo", UniformType.UNIFORM_BUFFER).withUniform("CloudFaces", UniformType.TEXEL_BUFFER, GpuFormat.R8_SINT).withUniform("DepthBoundsSampler", UniformType.COMBINED_IMAGE_SAMPLER);

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         builder.withUniform("Coeff" + i, UniformType.COMBINED_IMAGE_SAMPLER);
      }

      CLOUD_INFO_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER = builder.build();
   }
}
