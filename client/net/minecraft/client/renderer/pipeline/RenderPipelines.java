package net.minecraft.client.renderer.pipeline;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.pipeline.variant.BlockRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.DebugPrimitiveRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.EntityRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.ItemRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.LineRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.ParticleRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.RenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.TerrainRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.TextRenderPipelineVariant;
import org.jspecify.annotations.Nullable;

public class RenderPipelines {
   private static final Set<RenderPipeline> STATIC_PIPELINES_TO_PRECOMPILE;
   private static @Nullable Set<RenderPipeline> STATIC_PIPELINES_TO_PRECOMPILE_INIT = new HashSet();
   private static final Map<DynamicPipelineKey<?>, RenderPipeline> DYNAMIC_PIPELINES_CACHE = new ConcurrentHashMap();
   public static final float ALPHA_CUTOUT_THRESHOLD_DEFAULT = 0.1F;
   public static final float ALPHA_CUTOUT_THRESHOLD_CUTOUT_TERRAIN = 0.5F;
   public static final RenderPipeline.Snippet GLOBALS_SNIPPET;
   private static final RenderPipeline.Snippet MATRICES_FOG_SNIPPET;
   private static final RenderPipeline.Snippet OIT_SNIPPET;
   public static final RenderPipeline.Snippet OIT_DEPTH_BOUNDS_SNIPPET;
   public static final RenderPipeline.Snippet OIT_TRANSMITTANCE_SNIPPET;
   public static final RenderPipeline.Snippet OIT_ACCUMULATE_SNIPPET;
   public static final RenderPipeline.Snippet GLINT_SNIPPET;
   private static final RenderPipeline.Snippet IMPROVED_FOG_SNIPPET;
   private static final RenderPipeline.Snippet GUI_SNIPPET;
   private static final RenderPipeline.Snippet GUI_TEXTURED_SNIPPET;
   private static final RenderPipeline.Snippet OUTLINE_SNIPPET;
   public static final RenderPipeline LEASH;
   public static final RenderPipeline GLINT;
   public static final RenderPipeline GUI;
   public static final RenderPipeline GUI_INVERT;
   public static final RenderPipeline GUI_TEXT_HIGHLIGHT;
   public static final RenderPipeline GUI_TEXTURED;
   public static final RenderPipeline GUI_TEXTURED_PREMULTIPLIED_ALPHA;
   public static final RenderPipeline BLOCK_SCREEN_EFFECT;
   public static final RenderPipeline FIRE_SCREEN_EFFECT;
   public static final RenderPipeline GUI_OPAQUE_TEXTURED_BACKGROUND;
   public static final RenderPipeline GUI_NAUSEA_OVERLAY;
   public static final RenderPipeline VIGNETTE;
   public static final RenderPipeline CROSSHAIR;
   public static final RenderPipeline MOJANG_LOGO;
   public static final RenderPipeline ENTITY_OUTLINE_BLIT;
   public static final RenderPipeline TRACY_BLIT;
   public static final RenderPipeline PANORAMA;
   public static final RenderPipeline OUTLINE_CULL;
   public static final RenderPipeline OUTLINE_NO_CULL;
   public static final RenderPipeline LIGHTMAP;
   public static final RenderPipeline.Snippet ANIMATE_SPRITE_SNIPPET;
   public static final RenderPipeline ANIMATE_SPRITE_BLIT;
   public static final RenderPipeline ANIMATE_SPRITE_INTERPOLATE;
   public static final RenderPipeline BLIT_DEPTH_BOUNDS;
   public static final RenderPipeline OIT_DEPTH_BOUNDS_CULL;
   public static final RenderPipeline BLIT_SKY;
   public static final RenderPipeline BLIT_DEPTH;
   public static final RenderPipeline OIT_COMPOSITE;
   public static final RenderPipeline INTEGRATE_DEPTH;

   public RenderPipelines() {
      super();
   }

   private static RenderPipeline precompiled(final RenderPipeline pipeline) {
      if (STATIC_PIPELINES_TO_PRECOMPILE_INIT == null) {
         throw new IllegalStateException("Too late to register a static pipeline `" + String.valueOf(pipeline.getLocation()) + "` for precompilation.");
      } else {
         STATIC_PIPELINES_TO_PRECOMPILE_INIT.add(pipeline);
         return pipeline;
      }
   }

   private static void precompiledDynamic(final List<RenderPipeline> dynamicPipelines, final RenderPipeline pipeline) {
      dynamicPipelines.add(pipeline);
   }

   private static void precompiledVariant(final List<RenderPipeline> dynamicPipelines, final RenderPipelineVariant variant, final Function<@Nullable OitStage, RenderPipeline> func, final boolean improvedTransparency) {
      if (variant.supportsOit()) {
         precompiledOitPipelines(dynamicPipelines, func, improvedTransparency);
      } else {
         precompiledDynamic(dynamicPipelines, (RenderPipeline)func.apply((Object)null));
      }

   }

   public static List<RenderPipeline> dynamicPipelinesToPrecompile(final ShaderManager.PipelineCompilationContext context) {
      List<RenderPipeline> dynamicPipelines = new ArrayList();
      boolean improvedTransparency = context.options().improvedTransparency;

      for(TerrainRenderPipelineVariant variant : TerrainRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> terrain(new TerrainParameters(variant, improvedTransparency, context.multidraw(), false, stage)), improvedTransparency);
      }

      for(SkyRenderer.SkyElement skyElement : SkyRenderer.SkyElement.values()) {
         precompiledDynamic(dynamicPipelines, sky(new SkyParameters(skyElement, !improvedTransparency)));
      }

      for(EntityRenderPipelineVariant variant : EntityRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> entity(new EntityParameters(variant, stage)), improvedTransparency);
      }

      for(LineRenderPipelineVariant variant : LineRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> lines(new LinesParameters(variant, stage)), improvedTransparency);
      }

      for(ParticleRenderPipelineVariant variant : ParticleRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> particle(new ParticleParameters(variant, stage)), improvedTransparency);
      }

      for(ItemRenderPipelineVariant variant : ItemRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> item(new ItemParameters(variant, stage)), improvedTransparency);
      }

      for(BlockRenderPipelineVariant variant : BlockRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> block(new BlockParameters(variant, stage)), improvedTransparency);
      }

      for(TextRenderPipelineVariant variant : TextRenderPipelineVariant.values()) {
         precompiledVariant(dynamicPipelines, variant, (stage) -> text(new TextParameters(variant, stage)), improvedTransparency);
      }

      precompiledOitPipelines(dynamicPipelines, RenderPipelines::entityShadow, improvedTransparency);
      precompiledOitPipelines(dynamicPipelines, (stage) -> beaconBeam(new BeaconBeamParameters(true, stage)), improvedTransparency);
      precompiledDynamic(dynamicPipelines, beaconBeam(new BeaconBeamParameters(false, (OitStage)null)));
      precompiledOitPipelines(dynamicPipelines, RenderPipelines::crumbling, improvedTransparency);
      precompiledOitPipelines(dynamicPipelines, RenderPipelines::worldBorder, improvedTransparency);
      if (improvedTransparency) {
         precompiledOitPipelines(dynamicPipelines, RenderPipelines::blitClouds, improvedTransparency);
         precompiledDynamic(dynamicPipelines, waterMask(new WaterMaskParameters(true)));
      } else {
         precompiledDynamic(dynamicPipelines, waterMask(new WaterMaskParameters(false)));
      }

      precompiledOitPipelines(dynamicPipelines, (stage) -> additiveVisualEffect(new AdditiveVisualEffectParameters(PrimitiveTopology.QUADS, stage)), improvedTransparency);
      precompiledOitPipelines(dynamicPipelines, (stage) -> additiveVisualEffect(new AdditiveVisualEffectParameters(PrimitiveTopology.TRIANGLES, stage)), improvedTransparency);
      CloudStatus cloudStatus = context.options().cloudStatus;
      if (cloudStatus == CloudStatus.FANCY) {
         precompiledDynamic(dynamicPipelines, clouds(new CloudParameters(true)));
      } else if (cloudStatus == CloudStatus.FAST) {
         precompiledDynamic(dynamicPipelines, clouds(new CloudParameters(false)));
      }

      precompiledDynamic(dynamicPipelines, endPortal(new EndPortalParameters(true, false)));
      precompiledDynamic(dynamicPipelines, endPortal(new EndPortalParameters(false, false)));
      precompiledDynamic(dynamicPipelines, endPortal(new EndPortalParameters(false, true)));
      return Collections.unmodifiableList(dynamicPipelines);
   }

   private static void precompiledOitPipelines(final List<RenderPipeline> dynamicPipelines, final Function<@Nullable OitStage, RenderPipeline> pipelineSupplier, final boolean improvedTransparency) {
      if (improvedTransparency) {
         for(OitStage oitStage : OitStage.values()) {
            precompiledDynamic(dynamicPipelines, (RenderPipeline)pipelineSupplier.apply(oitStage));
         }
      }

      precompiledDynamic(dynamicPipelines, (RenderPipeline)pipelineSupplier.apply((Object)null));
   }

   public static List<RenderPipeline> staticPipelinesToPrecompile() {
      return STATIC_PIPELINES_TO_PRECOMPILE.stream().toList();
   }

   public static <P> RenderPipeline dynamicPipeline(final @Nullable P parameters, final String id, final Function<@Nullable P, RenderPipeline> pipelineSupplier) {
      return (RenderPipeline)DYNAMIC_PIPELINES_CACHE.computeIfAbsent(new DynamicPipelineKey(parameters, id), (var2) -> (RenderPipeline)pipelineSupplier.apply(parameters));
   }

   private static RenderPipeline finishDynamicPipeline(final RenderPipeline.Builder builder, final String baseName, final boolean supportsOit, final @Nullable OitStage oitStage, final @Nullable Consumer<RenderPipeline.Builder> nonOitSetup, final @Nullable Consumer<RenderPipeline.Builder> oitSetup, final @Nullable Consumer<RenderPipeline.Builder> oitAccumulateSetup, final @Nullable Consumer<RenderPipeline.Builder> colorOutputSetup, final @Nullable String locationPostfix) {
      if (oitStage != null) {
         if (!supportsOit) {
            throw new IllegalArgumentException("OIT stage must be null for pipeline permutation of \"" + baseName + "\" which doesn't support OIT.");
         }

         if (oitSetup != null) {
            oitSetup.accept(builder);
         }

         switch (oitStage) {
            case DEPTH_BOUNDS:
               builder.withSnippet(OIT_DEPTH_BOUNDS_SNIPPET);
               break;
            case TRANSMITTANCE:
               builder.withSnippet(OIT_TRANSMITTANCE_SNIPPET);
               break;
            case ACCUMULATE:
               builder.withSnippet(OIT_ACCUMULATE_SNIPPET);
               if (oitAccumulateSetup != null) {
                  oitAccumulateSetup.accept(builder);
               }

               if (colorOutputSetup != null) {
                  colorOutputSetup.accept(builder);
               }
         }
      } else {
         if (nonOitSetup != null) {
            nonOitSetup.accept(builder);
         }

         if (colorOutputSetup != null) {
            colorOutputSetup.accept(builder);
         }
      }

      String var10001 = oitStage != null ? "oit_" + oitStage.name().toLowerCase(Locale.ROOT) + "_" : "";
      builder.withLocation("pipeline/" + var10001 + baseName + (locationPostfix != null ? locationPostfix : ""));
      return builder.build();
   }

   private static RenderPipeline finishDynamicPipeline(final RenderPipeline.Builder builder, final String baseName, final boolean supportsOit, final @Nullable OitStage oitStage, final @Nullable Consumer<RenderPipeline.Builder> nonOitSetup, final @Nullable Consumer<RenderPipeline.Builder> oitSetup, final @Nullable Consumer<RenderPipeline.Builder> oitAccumulateSetup, final @Nullable String locationPostfix) {
      return finishDynamicPipeline(builder, baseName, supportsOit, oitStage, nonOitSetup, oitSetup, oitAccumulateSetup, (Consumer)null, locationPostfix);
   }

   private static RenderPipeline finishDynamicPipeline(final RenderPipeline.Builder builder, final String baseName, final boolean supportsOit, final @Nullable OitStage oitStage, final @Nullable Consumer<RenderPipeline.Builder> nonOitSetup, final @Nullable String locationPostfix) {
      return finishDynamicPipeline(builder, baseName, supportsOit, oitStage, nonOitSetup, (Consumer)null, (Consumer)null, (Consumer)null, locationPostfix);
   }

   private static RenderPipeline finishDynamicPipeline(final RenderPipeline.Builder builder, final String baseName, final boolean supportsOit, final @Nullable OitStage oitStage, final @Nullable Consumer<RenderPipeline.Builder> nonOitSetup) {
      return finishDynamicPipeline(builder, baseName, supportsOit, oitStage, nonOitSetup, (Consumer)null, (Consumer)null, (Consumer)null, (String)null);
   }

   public static RenderPipeline terrain(final TerrainParameters params) {
      return dynamicPipeline(params, "terrain", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.FOG).withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.BLOCK).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(DepthStencilState.DEFAULT).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.TERRAIN_INFO).withVertexShader("core/terrain").withFragmentShader("core/terrain");
         if (parameters.multiDraw) {
            builder.withVertexBinding(1, DefaultVertexFormat.CHUNK_DATA_INSTANCED).withShaderDefine("MULTIDRAW_TERRAIN");
         } else {
            builder.withBindGroupLayout(BindGroupLayouts.CHUNK_SECTION);
         }

         if (parameters.variant.hasAlphaCutout()) {
            builder.withShaderDefine("ALPHA_CUTOUT", parameters.variant.alphaCutoutThreshold());
         }

         String var10000 = parameters.variant.name();
         String locationPostfix = var10000 + (parameters.improvedFog ? "_improved_fog" : "") + (parameters.multiDraw ? "_multi_draw" : "") + (parameters.wireframe ? "_wireframe" : "");
         return finishDynamicPipeline(builder, "terrain", parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> {
            nonOitBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER2);
            if (!parameters.wireframe) {
               nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState());
               if (parameters.improvedFog) {
                  builder.withSnippet(IMPROVED_FOG_SNIPPET);
               }
            } else {
               nonOitBuilder.withPolygonMode(PolygonMode.WIREFRAME).withColorTargetState(ColorTargetState.DEFAULT);
            }

         }, (Consumer)null, (oitAccumulateBuilder) -> oitAccumulateBuilder.withSnippet(IMPROVED_FOG_SNIPPET).withBindGroupLayout(BindGroupLayouts.SAMPLER2), locationPostfix);
      });
   }

   public static RenderPipeline block(final BlockParameters params) {
      return dynamicPipeline(params, "block", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.BLOCK).withPrimitiveTopology(PrimitiveTopology.QUADS).withVertexShader("core/block").withFragmentShader("core/block");
         if (parameters.variant.hasAlphaCutout()) {
            builder.withShaderDefine("ALPHA_CUTOUT", parameters.variant.alphaCutoutThreshold());
         }

         return finishDynamicPipeline(builder, "block_" + parameters.variant.name(), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState()).withDepthStencilState(DepthStencilState.DEFAULT), (Consumer)null, (Consumer)null, (colorOutputBuilder) -> colorOutputBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER2), (String)null);
      });
   }

   public static RenderPipeline entity(final EntityParameters params) {
      return dynamicPipeline(params, "entity", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withVertexShader("core/entity").withFragmentShader("core/entity").withVertexBinding(0, DefaultVertexFormat.ENTITY).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayouts.SAMPLER0);
         switch (parameters.variant.lighting()) {
            case NONE -> builder.withShaderDefine("NO_CARDINAL_LIGHTING");
            case DEFAULT -> builder.withBindGroupLayout(BindGroupLayouts.LIGHTING);
            case PER_FACE -> builder.withBindGroupLayout(BindGroupLayouts.LIGHTING).withShaderDefine("PER_FACE_LIGHTING");
         }

         if (parameters.variant.hasAlphaCutout()) {
            builder.withShaderDefine("ALPHA_CUTOUT", 0.1F);
         }

         if (!parameters.variant.hasBackFaceCulling()) {
            builder.withCull(false);
         }

         if (parameters.variant.hasGlint()) {
            builder.withSnippet(GLINT_SNIPPET);
         }

         if (parameters.variant.applyTextureMatrix()) {
            builder.withShaderDefine("APPLY_TEXTURE_MATRIX");
         }

         if (parameters.variant.dissolve()) {
            builder.withShaderDefine("DISSOLVE").withBindGroupLayout(BindGroupLayouts.DISSOLVE_MASK_SAMPLER);
         }

         return finishDynamicPipeline(builder, "entity_" + parameters.variant.name(), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState()).withDepthStencilState(parameters.variant.nonOitDepthStencilState()), (oitBuilder) -> {
            if (parameters.variant.additive()) {
               oitBuilder.withShaderDefine("OIT_ADDITIVE");
            }

         }, (Consumer)null, (colorOutputBuilder) -> {
            if (parameters.variant.hasOverlay()) {
               colorOutputBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER1);
            } else {
               colorOutputBuilder.withShaderDefine("NO_OVERLAY");
            }

            if (parameters.variant.isEmissive()) {
               colorOutputBuilder.withShaderDefine("EMISSIVE");
            } else {
               colorOutputBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER2);
            }

         }, (String)null);
      });
   }

   public static RenderPipeline entityShadow(final @Nullable OitStage stage) {
      return dynamicPipeline(stage, "entity_shadow", (oitStage) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/rendertype_entity_shadow").withFragmentShader("core/rendertype_entity_shadow").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.ENTITY).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false));
         return finishDynamicPipeline(builder, "entity_shadow", true, oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)), (String)null);
      });
   }

   public static RenderPipeline item(final ItemParameters params) {
      return dynamicPipeline(params, "item", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/item").withFragmentShader("core/item").withBindGroupLayout(BindGroupLayouts.LIGHTING).withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.ENTITY).withShaderDefine("ALPHA_CUTOUT", 0.1F).withPrimitiveTopology(PrimitiveTopology.QUADS);
         switch (parameters.variant.glintType()) {
            case NORMAL -> builder.withSnippet(GLINT_SNIPPET);
            case SPECIAL -> builder.withSnippet(GLINT_SNIPPET).withShaderDefine("GLINT_SPECIAL").withVertexBinding(0, DefaultVertexFormat.ENTITY_GLINT_SPECIAL);
         }

         return finishDynamicPipeline(builder, "item_" + parameters.variant.name(), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState()).withDepthStencilState(DepthStencilState.DEFAULT), (Consumer)null, (Consumer)null, (colorOutputBuilder) -> colorOutputBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER1).withBindGroupLayout(BindGroupLayouts.SAMPLER2), (String)null);
      });
   }

   public static RenderPipeline beaconBeam(final BeaconBeamParameters params) {
      return dynamicPipeline(params, "beacon_beam", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/rendertype_beacon_beam").withFragmentShader("core/rendertype_beacon_beam").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.BLOCK).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(DepthStencilState.DEFAULT);
         return finishDynamicPipeline(builder, "beacon_beam", parameters.translucent, parameters.oitStage, (nonOitBuilder) -> {
            if (parameters.translucent) {
               nonOitBuilder.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false));
            } else {
               nonOitBuilder.withColorTargetState(ColorTargetState.DEFAULT);
            }

         }, parameters.translucent ? "_translucent" : "_opaque");
      });
   }

   public static RenderPipeline waterMask(final WaterMaskParameters params) {
      return dynamicPipeline(params, "water_mask", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/rendertype_water_mask").withFragmentShader("core/rendertype_water_mask").withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(DepthStencilState.DEFAULT);
         if (!parameters.improvedTransparency) {
            builder.withLocation("pipeline/water_mask").withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), GpuFormat.RGBA8_UNORM, 0));
         } else {
            builder.withLocation("pipeline/oit_water_mask");
         }

         return builder.build();
      });
   }

   public static RenderPipeline crumbling(final @Nullable OitStage stage) {
      return dynamicPipeline(stage, "crumbling", (oitStage) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.FOG).withVertexShader("core/rendertype_crumbling").withFragmentShader("core/rendertype_crumbling").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.BLOCK).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false, 1.0F, 10.0F));
         return finishDynamicPipeline(builder, "crumbling", true, oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.DST_COLOR, BlendFactor.SRC_COLOR, BlendFactor.ONE, BlendFactor.ZERO))));
      });
   }

   public static RenderPipeline text(final TextParameters params) {
      return dynamicPipeline(params, "text", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexShader("core/text").withFragmentShader("core/text").withPrimitiveTopology(PrimitiveTopology.QUADS);
         if (parameters.variant.affectedByFogAndLight()) {
            builder.withBindGroupLayout(BindGroupLayouts.FOG).withBindGroupLayout(BindGroupLayouts.SAMPLER2).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR).withShaderDefine("AFFECTED_BY_FOG_AND_LIGHT");
         } else {
            builder.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR);
         }

         if (parameters.variant.isGrayscale()) {
            builder.withShaderDefine("IS_GRAYSCALE");
         }

         return finishDynamicPipeline(builder, "text_" + parameters.variant.name().toLowerCase(Locale.ROOT), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> {
            nonOitBuilder.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT));
            if (parameters.variant.hasPolygonOffset()) {
               nonOitBuilder.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true, 1.0F, 10.0F));
            } else if (parameters.variant.affectedByFogAndLight()) {
               nonOitBuilder.withDepthStencilState(DepthStencilState.DEFAULT);
            } else {
               nonOitBuilder.withDepthStencilState(Optional.empty());
            }

         });
      });
   }

   public static RenderPipeline additiveVisualEffect(final AdditiveVisualEffectParameters params) {
      return dynamicPipeline(params, "additive_visual_effect", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/rendertype_lightning").withFragmentShader("core/rendertype_lightning").withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(parameters.primitiveTopology).withDepthStencilState(DepthStencilState.DEFAULT);
         OitStage var10003 = parameters.oitStage;
         Consumer var10004 = (nonOitBuilder) -> nonOitBuilder.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING));
         Consumer var10005 = (oitBuilder) -> oitBuilder.withShaderDefine("OIT_ADDITIVE");
         String var10007 = parameters.primitiveTopology.name();
         return finishDynamicPipeline(builder, "additive_visual_effect", true, var10003, var10004, var10005, (Consumer)null, "_" + var10007.toLowerCase(Locale.ROOT));
      });
   }

   public static RenderPipeline endPortal(final EndPortalParameters params) {
      return dynamicPipeline(params, "end_portal", (parameters) -> {
         RenderPipeline.Builder var10000 = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withVertexShader("core/rendertype_end_portal").withFragmentShader("core/rendertype_end_portal").withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1).withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(ColorTargetState.DEFAULT).withShaderDefine("PORTAL_LAYERS", parameters.isGateway ? 16 : 15);
         String var10001 = parameters.isGateway ? "gateway" : "portal";
         RenderPipeline.Builder builder = var10000.withLocation("pipeline/end_" + var10001 + (parameters.isInGui ? "_gui" : ""));
         if (!parameters.isInGui) {
            builder.withDepthStencilState(DepthStencilState.DEFAULT);
         }

         return builder.build();
      });
   }

   public static RenderPipeline clouds(final CloudParameters params) {
      return dynamicPipeline(params, "clouds", (parameters) -> RenderPipeline.builder(GLOBALS_SNIPPET).withVertexShader("core/clouds").withFragmentShader("core/clouds").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withBindGroupLayout(BindGroupLayouts.CLOUD_INFO).withDepthStencilState(DepthStencilState.DEFAULT).withCull(parameters.hasBackFaceCulling).withLocation("pipeline/clouds" + (!parameters.hasBackFaceCulling ? "_flat" : "")).build());
   }

   public static RenderPipeline lines(final LinesParameters params) {
      return dynamicPipeline(params, "lines", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/rendertype_lines").withFragmentShader("core/rendertype_lines").withCull(false).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH).withPrimitiveTopology(PrimitiveTopology.LINES);
         return finishDynamicPipeline(builder, "lines_" + parameters.variant.name(), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState()).withDepthStencilState(parameters.variant.nonOitDepthStencilState()));
      });
   }

   public static RenderPipeline debugPrimitive(final DebugPrimitiveParameters params) {
      return dynamicPipeline(params, "debug_primitive", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withFragmentShader("core/position_color").withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withCull(false);
         if (parameters.variant.primitiveTopology() == PrimitiveTopology.POINTS) {
            builder.withVertexShader("core/debug_point").withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH);
         } else {
            builder.withVertexShader("core/position_color").withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR);
         }

         builder.withPrimitiveTopology(parameters.variant.primitiveTopology());
         return finishDynamicPipeline(builder, "debug_primitive_" + parameters.variant.name(), true, parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)));
      });
   }

   public static RenderPipeline worldBorder(final @Nullable OitStage stage) {
      return dynamicPipeline(stage, "world_border", (oitStage) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/world_border").withFragmentShader("core/world_border").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withCull(false).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS);
         return finishDynamicPipeline(builder, "world_border", true, oitStage, (nonOitBuilder) -> nonOitBuilder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true)), (oitBuilder) -> oitBuilder.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).withShaderDefine("OIT_ADDITIVE"), (Consumer)null, (String)null);
      });
   }

   public static RenderPipeline particle(final ParticleParameters params) {
      return dynamicPipeline(params, "particle", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(MATRICES_FOG_SNIPPET).withVertexShader("core/particle").withFragmentShader("core/particle").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.PARTICLE).withPrimitiveTopology(PrimitiveTopology.QUADS).withCull(parameters.variant.cull());
         return finishDynamicPipeline(builder, "particles_" + parameters.variant.name(), parameters.variant.supportsOit(), parameters.oitStage, (nonOitBuilder) -> nonOitBuilder.withColorTargetState(parameters.variant.nonOitColorTargetState()).withDepthStencilState(parameters.variant.nonOitDepthStencilState()).withBindGroupLayout(BindGroupLayouts.SAMPLER2), (Consumer)null, (oitAccumulateBuilder) -> oitAccumulateBuilder.withBindGroupLayout(BindGroupLayouts.SAMPLER2), (String)null);
      });
   }

   public static RenderPipeline sky(final SkyParameters params) {
      return dynamicPipeline(params, "sky", (parameters) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(GLOBALS_SNIPPET);
         switch (parameters.element) {
            case SKY_DISC -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withVertexShader("core/sky").withFragmentShader("core/sky").withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.TRIANGLE_FAN).withColorTargetState(ColorTargetState.DEFAULT);
            case SKY_OCCLUDER -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withVertexShader("core/sky_occluder").withFragmentShader("core/sky_occluder").withBindGroupLayout(BindGroupLayouts.SKY_OCCLUDER_INFO).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT));
            case SUNRISE_SUNSET -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/position_color").withFragmentShader("core/position_color").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.TRIANGLE_FAN);
            case CELESTIAL -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/position_tex").withFragmentShader("core/position_tex").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS);
            case STARS -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/stars").withFragmentShader("core/stars").withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY)).withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS);
            case END_SKY -> builder.withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS);
            default -> throw new IllegalArgumentException("Unknown sky element: " + String.valueOf(parameters.element));
         }

         if (parameters.hasDepthAttachment) {
            builder.withDepthStencilState(DepthStencilState.OFF);
         }

         String var10001 = parameters.element.toString().toLowerCase(Locale.ROOT);
         builder.withLocation("pipeline/" + var10001 + (parameters.hasDepthAttachment ? "_with_depth_attachment" : ""));
         return builder.build();
      });
   }

   public static RenderPipeline blitClouds(final @Nullable OitStage stage) {
      return dynamicPipeline(stage, "blit_clouds", (oitStage) -> {
         RenderPipeline.Builder builder = RenderPipeline.builder().withVertexShader("core/screentriangle").withFragmentShader("core/blit_clouds").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER_IN_DEPTH_SAMPLER).withPrimitiveTopology(PrimitiveTopology.TRIANGLES);
         return finishDynamicPipeline(builder, "blit_clouds", true, oitStage, (Consumer)null);
      });
   }

   static {
      GLOBALS_SNIPPET = RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.GLOBALS).buildSnippet();
      MATRICES_FOG_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).buildSnippet();
      OIT_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withShaderDefine("OIT").withShaderDefine("OIT_NUMBER_OF_DEPTH_BINS", 8).withShaderDefine("OIT_TRANSMITTANCE_TARGET_COUNT", 2).buildSnippet();
      OIT_DEPTH_BOUNDS_SNIPPET = RenderPipeline.builder(OIT_SNIPPET).withBindGroupLayout(BindGroupLayouts.GLOBALS).withBindGroupLayout(BindGroupLayouts.PROJECTION).withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.MAX), GpuFormat.RGBA32_FLOAT, 15)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).withShaderDefine("OIT_ALPHA_ONLY").withShaderDefine("OIT_DEPTH_BOUNDS").buildSnippet();
      OIT_TRANSMITTANCE_SNIPPET = RenderPipeline.builder(OIT_SNIPPET).withBindGroupLayout(BindGroupLayouts.GLOBALS).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DEPTH_BOUNDS_SAMPLER).withShaderDefine("OIT_ALPHA_ONLY").withShaderDefine("OIT_TRANSMITTANCE").withColorTargetStates(0, 1, () -> new ColorTargetState(Optional.of(BlendFunction.ADDITIVE), GpuFormat.RGBA16_FLOAT, 15)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).buildSnippet();
      OIT_ACCUMULATE_SNIPPET = RenderPipeline.builder(OIT_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.OIT_COEFFS_DEPTH_BOUNDS_SAMPLER).withShaderDefine("OIT_ACCUMULATE").withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.ADDITIVE), GpuFormat.RGBA16_FLOAT, 15)).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).buildSnippet();
      GLINT_SNIPPET = RenderPipeline.builder().withShaderDefine("GLINT").withBindGroupLayout(BindGroupLayouts.GLINT_SAMPLER).buildSnippet();
      IMPROVED_FOG_SNIPPET = RenderPipeline.builder().withBindGroupLayout(BindGroupLayouts.SKY_CLOUDS_SAMPLER).withShaderDefine("IMPROVED_FOG").buildSnippet();
      GUI_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/gui").withFragmentShader("core/gui").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();
      GUI_TEXTURED_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();
      OUTLINE_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withVertexShader("core/rendertype_outline").withFragmentShader("core/rendertype_outline").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(ColorTargetState.DEFAULT).buildSnippet();
      LEASH = precompiled(RenderPipeline.builder(MATRICES_FOG_SNIPPET).withLocation("pipeline/leash").withVertexShader("core/rendertype_leash").withFragmentShader("core/rendertype_leash").withBindGroupLayout(BindGroupLayouts.SAMPLER2).withCull(false).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP).withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP).withColorTargetState(ColorTargetState.DEFAULT).withDepthStencilState(DepthStencilState.DEFAULT).build());
      GLINT = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withBindGroupLayout(BindGroupLayouts.FOG).withLocation("pipeline/glint").withVertexShader("core/glint").withFragmentShader("core/glint").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withCull(false).withColorTargetState(new ColorTargetState(BlendFunction.GLINT)).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false)).build());
      GUI = precompiled(RenderPipeline.builder(GUI_SNIPPET).withLocation("pipeline/gui").build());
      GUI_INVERT = precompiled(RenderPipeline.builder(GUI_SNIPPET).withLocation("pipeline/gui_invert").withColorTargetState(new ColorTargetState(BlendFunction.INVERT)).build());
      GUI_TEXT_HIGHLIGHT = precompiled(RenderPipeline.builder(GUI_SNIPPET).withLocation("pipeline/gui_text_highlight").withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE)).build());
      GUI_TEXTURED = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_textured").build());
      GUI_TEXTURED_PREMULTIPLIED_ALPHA = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_textured_premultiplied_alpha").withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)).build());
      BLOCK_SCREEN_EFFECT = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/block_screen_effect").withDepthStencilState(DepthStencilState.OFF).build());
      FIRE_SCREEN_EFFECT = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/fire_screen_effect").withDepthStencilState(DepthStencilState.OFF).build());
      GUI_OPAQUE_TEXTURED_BACKGROUND = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_opaque_textured_background").withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA8_UNORM, 15)).build());
      GUI_NAUSEA_OVERLAY = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/gui_nausea_overlay").withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE)).build());
      VIGNETTE = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/vignette").withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.ZERO, BlendFactor.ONE_MINUS_SRC_COLOR, BlendFactor.ZERO, BlendFactor.ONE))).build());
      CROSSHAIR = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/crosshair").withColorTargetState(new ColorTargetState(BlendFunction.INVERT)).build());
      MOJANG_LOGO = precompiled(RenderPipeline.builder(GUI_TEXTURED_SNIPPET).withLocation("pipeline/mojang_logo").withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE))).build());
      ENTITY_OUTLINE_BLIT = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/entity_outline_blit").withVertexShader("core/screentriangle").withFragmentShader("core/blit_screen").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.ENTITY_OUTLINE_BLIT), GpuFormat.RGBA8_UNORM, 7)).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).build());
      TRACY_BLIT = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/tracy_blit").withVertexShader("core/screentriangle").withFragmentShader("core/blit_screen").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withColorTargetState(ColorTargetState.DEFAULT).build());
      PANORAMA = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS).withLocation("pipeline/panorama").withVertexShader("core/panorama").withFragmentShader("core/panorama").withBindGroupLayout(BindGroupLayouts.SAMPLER0).withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(ColorTargetState.DEFAULT).build());
      OUTLINE_CULL = precompiled(RenderPipeline.builder(OUTLINE_SNIPPET).withLocation("pipeline/outline_cull").build());
      OUTLINE_NO_CULL = precompiled(RenderPipeline.builder(OUTLINE_SNIPPET).withLocation("pipeline/outline_no_cull").withCull(false).build());
      LIGHTMAP = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/lightmap").withVertexShader("core/screentriangle").withFragmentShader("core/lightmap").withBindGroupLayout(BindGroupLayouts.LIGHTMAP_INFO).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withColorTargetState(ColorTargetState.DEFAULT).build());
      ANIMATE_SPRITE_SNIPPET = RenderPipeline.builder(GLOBALS_SNIPPET).withVertexShader("core/animate_sprite").withBindGroupLayout(BindGroupLayouts.SPRITE_ANIMATION_INFO).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withColorTargetState(ColorTargetState.DEFAULT).buildSnippet();
      ANIMATE_SPRITE_BLIT = precompiled(RenderPipeline.builder(ANIMATE_SPRITE_SNIPPET).withFragmentShader("core/animate_sprite_blit").withLocation("pipeline/animate_sprite_blit").withBindGroupLayout(BindGroupLayouts.SPRITE).build());
      ANIMATE_SPRITE_INTERPOLATE = precompiled(RenderPipeline.builder(ANIMATE_SPRITE_SNIPPET).withFragmentShader("core/animate_sprite_interpolate").withLocation("pipeline/animate_sprite_interpolate").withBindGroupLayout(BindGroupLayouts.CURRENT_SPRITE_NEXT_SPRITE).build());
      BLIT_DEPTH_BOUNDS = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/blit_depth_bounds").withVertexShader("core/screentriangle").withFragmentShader("core/blit_screen").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA32_FLOAT, 15)).withDepthStencilState(DepthStencilState.OFF).build());
      OIT_DEPTH_BOUNDS_CULL = precompiled(RenderPipeline.builder(OIT_SNIPPET).withVertexShader("core/screentriangle").withFragmentShader("core/oit_depth_bounds_cull").withLocation("pipeline/oit_depth_bounds_cull").withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withBindGroupLayout(BindGroupLayouts.DEPTH_BOUNDS_SAMPLER).withBindGroupLayout(BindGroupLayouts.PROJECTION).withColorTargetState(new ColorTargetState(Optional.empty(), GpuFormat.RGBA32_FLOAT, 15)).withDepthStencilState(DepthStencilState.DEFAULT).build());
      BLIT_SKY = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/blit_sky").withVertexShader("core/screentriangle").withFragmentShader("core/blit_screen").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withDepthStencilState(DepthStencilState.DEFAULT).withColorTargetState(ColorTargetState.DEFAULT).build());
      BLIT_DEPTH = precompiled(RenderPipeline.builder(GLOBALS_SNIPPET).withLocation("pipeline/blit_depth").withVertexShader("core/screentriangle").withFragmentShader("core/blit_depth").withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true)).build());
      OIT_COMPOSITE = precompiled(RenderPipeline.builder(OIT_SNIPPET).withBindGroupLayout(BindGroupLayouts.PROJECTION).withVertexShader("core/screentriangle").withFragmentShader("core/oit_composite").withLocation("pipeline/oit_composite").withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withBindGroupLayout(BindGroupLayouts.SAMPLER0_OIT_COEFFS_DEPTH_BOUNDS_SAMPLER).withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA), GpuFormat.RGBA8_UNORM, 15)).withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true)).build());
      INTEGRATE_DEPTH = precompiled(RenderPipeline.builder().withVertexShader("core/screentriangle").withFragmentShader("core/integrate_depth").withLocation("pipeline/integrate_depth").withPrimitiveTopology(PrimitiveTopology.TRIANGLES).withBindGroupLayout(BindGroupLayouts.IN_SAMPLER).withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true)).build());
      STATIC_PIPELINES_TO_PRECOMPILE = Set.copyOf(STATIC_PIPELINES_TO_PRECOMPILE_INIT);
      STATIC_PIPELINES_TO_PRECOMPILE_INIT = null;
   }

   private static record DynamicPipelineKey<P>(@Nullable P parameters, String id) {
      private DynamicPipelineKey {
         super();
      }
   }

   public static record TerrainParameters(TerrainRenderPipelineVariant variant, boolean improvedFog, boolean multiDraw, boolean wireframe, @Nullable OitStage oitStage) {
      public TerrainParameters {
         super();
      }
   }

   public static record BlockParameters(BlockRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public BlockParameters {
         super();
      }
   }

   public static record EntityParameters(EntityRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public EntityParameters {
         super();
      }
   }

   public static record ItemParameters(ItemRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public ItemParameters {
         super();
      }
   }

   public static record BeaconBeamParameters(boolean translucent, @Nullable OitStage oitStage) {
      public BeaconBeamParameters {
         super();
      }
   }

   public static record WaterMaskParameters(boolean improvedTransparency) {
      public WaterMaskParameters {
         super();
      }
   }

   public static record TextParameters(TextRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public TextParameters {
         super();
      }
   }

   public static record AdditiveVisualEffectParameters(PrimitiveTopology primitiveTopology, @Nullable OitStage oitStage) {
      public AdditiveVisualEffectParameters {
         super();
      }
   }

   public static record EndPortalParameters(boolean isGateway, boolean isInGui) {
      public EndPortalParameters {
         super();
      }
   }

   public static record CloudParameters(boolean hasBackFaceCulling) {
      public CloudParameters {
         super();
      }
   }

   public static record LinesParameters(LineRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public LinesParameters {
         super();
      }
   }

   public static record DebugPrimitiveParameters(DebugPrimitiveRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public DebugPrimitiveParameters {
         super();
      }
   }

   public static record ParticleParameters(ParticleRenderPipelineVariant variant, @Nullable OitStage oitStage) {
      public ParticleParameters {
         super();
      }
   }

   public static record SkyParameters(SkyRenderer.SkyElement element, boolean hasDepthAttachment) {
      public SkyParameters {
         super();
      }
   }
}
