package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public abstract class RenderStateShard {
   public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
   protected final String name;
   private final Runnable setupState;
   private final Runnable clearState;
   protected static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("no_transparency", () -> RenderSystem.disableBlend(), () -> {
   });
   protected static final TransparencyStateShard ADDITIVE_TRANSPARENCY = new TransparencyStateShard("additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard LIGHTNING_TRANSPARENCY = new TransparencyStateShard("lightning_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard GLINT_TRANSPARENCY = new TransparencyStateShard("glint_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard CRUMBLING_TRANSPARENCY = new TransparencyStateShard("crumbling_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard OVERLAY_TRANSPARENCY = new TransparencyStateShard("overlay_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard VIGNETTE_TRANSPARENCY = new TransparencyStateShard("vignette_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard CROSSHAIR_TRANSPARENCY = new TransparencyStateShard("crosshair_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard MOJANG_LOGO_TRANSPARENCY = new TransparencyStateShard("mojang_logo_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(770, 1);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final TransparencyStateShard NAUSEA_OVERLAY_TRANSPARENCY = new TransparencyStateShard("nausea_overlay_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final ShaderStateShard NO_SHADER = new ShaderStateShard();
   protected static final ShaderStateShard POSITION_COLOR_LIGHTMAP_SHADER;
   protected static final ShaderStateShard POSITION_SHADER;
   protected static final ShaderStateShard POSITION_TEX_SHADER;
   protected static final ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_SHADER;
   protected static final ShaderStateShard POSITION_COLOR_SHADER;
   protected static final ShaderStateShard POSITION_TEXTURE_COLOR_SHADER;
   protected static final ShaderStateShard PARTICLE_SHADER;
   protected static final ShaderStateShard RENDERTYPE_SOLID_SHADER;
   protected static final ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER;
   protected static final ShaderStateShard RENDERTYPE_CUTOUT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ARMOR_TRANSLUCENT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_BEACON_BEAM_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_DECAL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_NO_OUTLINE_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SHADOW_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_ALPHA_SHADER;
   protected static final ShaderStateShard RENDERTYPE_EYES_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_LEASH_SHADER;
   protected static final ShaderStateShard RENDERTYPE_WATER_MASK_SHADER;
   protected static final ShaderStateShard RENDERTYPE_OUTLINE_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GLINT_TRANSLUCENT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GLINT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_CRUMBLING_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_BACKGROUND_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_SEE_THROUGH_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER;
   protected static final ShaderStateShard RENDERTYPE_LIGHTNING_SHADER;
   protected static final ShaderStateShard RENDERTYPE_TRIPWIRE_SHADER;
   protected static final ShaderStateShard RENDERTYPE_END_PORTAL_SHADER;
   protected static final ShaderStateShard RENDERTYPE_END_GATEWAY_SHADER;
   protected static final ShaderStateShard RENDERTYPE_CLOUDS_SHADER;
   protected static final ShaderStateShard RENDERTYPE_LINES_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GUI_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GUI_OVERLAY_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GUI_TEXT_HIGHLIGHT_SHADER;
   protected static final ShaderStateShard RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY_SHADER;
   protected static final ShaderStateShard RENDERTYPE_BREEZE_WIND_SHADER;
   protected static final TextureStateShard BLOCK_SHEET_MIPPED;
   protected static final TextureStateShard BLOCK_SHEET;
   protected static final EmptyTextureStateShard NO_TEXTURE;
   protected static final TexturingStateShard DEFAULT_TEXTURING;
   protected static final TexturingStateShard GLINT_TEXTURING;
   protected static final TexturingStateShard ENTITY_GLINT_TEXTURING;
   protected static final LightmapStateShard LIGHTMAP;
   protected static final LightmapStateShard NO_LIGHTMAP;
   protected static final OverlayStateShard OVERLAY;
   protected static final OverlayStateShard NO_OVERLAY;
   protected static final CullStateShard CULL;
   protected static final CullStateShard NO_CULL;
   protected static final DepthTestStateShard NO_DEPTH_TEST;
   protected static final DepthTestStateShard EQUAL_DEPTH_TEST;
   protected static final DepthTestStateShard LEQUAL_DEPTH_TEST;
   protected static final DepthTestStateShard GREATER_DEPTH_TEST;
   protected static final WriteMaskStateShard COLOR_DEPTH_WRITE;
   protected static final WriteMaskStateShard COLOR_WRITE;
   protected static final WriteMaskStateShard DEPTH_WRITE;
   protected static final LayeringStateShard NO_LAYERING;
   protected static final LayeringStateShard POLYGON_OFFSET_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING_FORWARD;
   protected static final LayeringStateShard WORLD_BORDER_LAYERING;
   protected static final OutputStateShard MAIN_TARGET;
   protected static final OutputStateShard OUTLINE_TARGET;
   protected static final OutputStateShard TRANSLUCENT_TARGET;
   protected static final OutputStateShard PARTICLES_TARGET;
   protected static final OutputStateShard WEATHER_TARGET;
   protected static final OutputStateShard CLOUDS_TARGET;
   protected static final OutputStateShard ITEM_ENTITY_TARGET;
   protected static final LineStateShard DEFAULT_LINE;
   protected static final ColorLogicStateShard NO_COLOR_LOGIC;
   protected static final ColorLogicStateShard OR_REVERSE_COLOR_LOGIC;

   public RenderStateShard(String var1, Runnable var2, Runnable var3) {
      super();
      this.name = var1;
      this.setupState = var2;
      this.clearState = var3;
   }

   public void setupRenderState() {
      this.setupState.run();
   }

   public void clearRenderState() {
      this.clearState.run();
   }

   public String toString() {
      return this.name;
   }

   private static void setupGlintTexturing(float var0) {
      long var1 = (long)((double)Util.getMillis() * (Double)Minecraft.getInstance().options.glintSpeed().get() * 8.0);
      float var3 = (float)(var1 % 110000L) / 110000.0F;
      float var4 = (float)(var1 % 30000L) / 30000.0F;
      Matrix4f var5 = (new Matrix4f()).translation(-var3, var4, 0.0F);
      var5.rotateZ(0.17453292F).scale(var0);
      RenderSystem.setTextureMatrix(var5);
   }

   static {
      POSITION_COLOR_LIGHTMAP_SHADER = new ShaderStateShard(CoreShaders.POSITION_COLOR_LIGHTMAP);
      POSITION_SHADER = new ShaderStateShard(CoreShaders.POSITION);
      POSITION_TEX_SHADER = new ShaderStateShard(CoreShaders.POSITION_TEX);
      POSITION_COLOR_TEX_LIGHTMAP_SHADER = new ShaderStateShard(CoreShaders.POSITION_COLOR_TEX_LIGHTMAP);
      POSITION_COLOR_SHADER = new ShaderStateShard(CoreShaders.POSITION_COLOR);
      POSITION_TEXTURE_COLOR_SHADER = new ShaderStateShard(CoreShaders.POSITION_TEX_COLOR);
      PARTICLE_SHADER = new ShaderStateShard(CoreShaders.PARTICLE);
      RENDERTYPE_SOLID_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_SOLID);
      RENDERTYPE_CUTOUT_MIPPED_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_CUTOUT_MIPPED);
      RENDERTYPE_CUTOUT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_CUTOUT);
      RENDERTYPE_TRANSLUCENT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TRANSLUCENT);
      RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TRANSLUCENT_MOVING_BLOCK);
      RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ARMOR_CUTOUT_NO_CULL);
      RENDERTYPE_ARMOR_TRANSLUCENT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ARMOR_TRANSLUCENT);
      RENDERTYPE_ENTITY_SOLID_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_SOLID);
      RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_CUTOUT);
      RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_CUTOUT_NO_CULL);
      RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET);
      RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL);
      RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_TRANSLUCENT);
      RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE);
      RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_SMOOTH_CUTOUT);
      RENDERTYPE_BEACON_BEAM_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_BEACON_BEAM);
      RENDERTYPE_ENTITY_DECAL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_DECAL);
      RENDERTYPE_ENTITY_NO_OUTLINE_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_NO_OUTLINE);
      RENDERTYPE_ENTITY_SHADOW_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_SHADOW);
      RENDERTYPE_ENTITY_ALPHA_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_ALPHA);
      RENDERTYPE_EYES_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_EYES);
      RENDERTYPE_ENERGY_SWIRL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENERGY_SWIRL);
      RENDERTYPE_LEASH_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_LEASH);
      RENDERTYPE_WATER_MASK_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_WATER_MASK);
      RENDERTYPE_OUTLINE_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_OUTLINE);
      RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ARMOR_ENTITY_GLINT);
      RENDERTYPE_GLINT_TRANSLUCENT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GLINT_TRANSLUCENT);
      RENDERTYPE_GLINT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GLINT);
      RENDERTYPE_ENTITY_GLINT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_ENTITY_GLINT);
      RENDERTYPE_CRUMBLING_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_CRUMBLING);
      RENDERTYPE_TEXT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT);
      RENDERTYPE_TEXT_BACKGROUND_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT_BACKGROUND);
      RENDERTYPE_TEXT_INTENSITY_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT_INTENSITY);
      RENDERTYPE_TEXT_SEE_THROUGH_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT_SEE_THROUGH);
      RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH);
      RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH);
      RENDERTYPE_LIGHTNING_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_LIGHTNING);
      RENDERTYPE_TRIPWIRE_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_TRIPWIRE);
      RENDERTYPE_END_PORTAL_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_END_PORTAL);
      RENDERTYPE_END_GATEWAY_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_END_GATEWAY);
      RENDERTYPE_CLOUDS_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_CLOUDS);
      RENDERTYPE_LINES_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_LINES);
      RENDERTYPE_GUI_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GUI);
      RENDERTYPE_GUI_OVERLAY_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GUI_OVERLAY);
      RENDERTYPE_GUI_TEXT_HIGHLIGHT_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GUI_TEXT_HIGHLIGHT);
      RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY);
      RENDERTYPE_BREEZE_WIND_SHADER = new ShaderStateShard(CoreShaders.RENDERTYPE_BREEZE_WIND);
      BLOCK_SHEET_MIPPED = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, TriState.FALSE, true);
      BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, TriState.FALSE, false);
      NO_TEXTURE = new EmptyTextureStateShard();
      DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {
      }, () -> {
      });
      GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> setupGlintTexturing(8.0F), () -> RenderSystem.resetTextureMatrix());
      ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> setupGlintTexturing(0.16F), () -> RenderSystem.resetTextureMatrix());
      LIGHTMAP = new LightmapStateShard(true);
      NO_LIGHTMAP = new LightmapStateShard(false);
      OVERLAY = new OverlayStateShard(true);
      NO_OVERLAY = new OverlayStateShard(false);
      CULL = new CullStateShard(true);
      NO_CULL = new CullStateShard(false);
      NO_DEPTH_TEST = new DepthTestStateShard("always", 519);
      EQUAL_DEPTH_TEST = new DepthTestStateShard("==", 514);
      LEQUAL_DEPTH_TEST = new DepthTestStateShard("<=", 515);
      GREATER_DEPTH_TEST = new DepthTestStateShard(">", 516);
      COLOR_DEPTH_WRITE = new WriteMaskStateShard(true, true);
      COLOR_WRITE = new WriteMaskStateShard(true, false);
      DEPTH_WRITE = new WriteMaskStateShard(false, true);
      NO_LAYERING = new LayeringStateShard("no_layering", () -> {
      }, () -> {
      });
      POLYGON_OFFSET_LAYERING = new LayeringStateShard("polygon_offset_layering", () -> {
         RenderSystem.polygonOffset(-1.0F, -10.0F);
         RenderSystem.enablePolygonOffset();
      }, () -> {
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
      });
      VIEW_OFFSET_Z_LAYERING = new LayeringStateShard("view_offset_z_layering", () -> {
         Matrix4fStack var0 = RenderSystem.getModelViewStack();
         var0.pushMatrix();
         RenderSystem.getProjectionType().applyLayeringTransform(var0, 1.0F);
      }, () -> {
         Matrix4fStack var0 = RenderSystem.getModelViewStack();
         var0.popMatrix();
      });
      VIEW_OFFSET_Z_LAYERING_FORWARD = new LayeringStateShard("view_offset_z_layering_forward", () -> {
         Matrix4fStack var0 = RenderSystem.getModelViewStack();
         var0.pushMatrix();
         RenderSystem.getProjectionType().applyLayeringTransform(var0, -1.0F);
      }, () -> {
         Matrix4fStack var0 = RenderSystem.getModelViewStack();
         var0.popMatrix();
      });
      WORLD_BORDER_LAYERING = new LayeringStateShard("world_border_layering", () -> {
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
      }, () -> {
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
      });
      MAIN_TARGET = new OutputStateShard("main_target", () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false), () -> {
      });
      OUTLINE_TARGET = new OutputStateShard("outline_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.entityOutlineTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getTranslucentTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getParticlesTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      CLOUDS_TARGET = new OutputStateShard("clouds_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getItemEntityTarget();
         if (var0 != null) {
            var0.bindWrite(false);
         } else {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      }, () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(false));
      DEFAULT_LINE = new LineStateShard(OptionalDouble.of(1.0));
      NO_COLOR_LOGIC = new ColorLogicStateShard("no_color_logic", () -> RenderSystem.disableColorLogicOp(), () -> {
      });
      OR_REVERSE_COLOR_LOGIC = new ColorLogicStateShard("or_reverse", () -> {
         RenderSystem.enableColorLogicOp();
         RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      }, () -> RenderSystem.disableColorLogicOp());
   }

   protected static class TransparencyStateShard extends RenderStateShard {
      public TransparencyStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static class ShaderStateShard extends RenderStateShard {
      private final Optional<ShaderProgram> shader;

      public ShaderStateShard(ShaderProgram var1) {
         super("shader", () -> RenderSystem.setShader(var1), () -> {
         });
         this.shader = Optional.of(var1);
      }

      public ShaderStateShard() {
         super("shader", RenderSystem::clearShader, () -> {
         });
         this.shader = Optional.empty();
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.shader) + "]";
      }
   }

   protected static class EmptyTextureStateShard extends RenderStateShard {
      public EmptyTextureStateShard(Runnable var1, Runnable var2) {
         super("texture", var1, var2);
      }

      EmptyTextureStateShard() {
         super("texture", () -> {
         }, () -> {
         });
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return Optional.empty();
      }
   }

   protected static class MultiTextureStateShard extends EmptyTextureStateShard {
      private final Optional<ResourceLocation> cutoutTexture;

      MultiTextureStateShard(List<Entry> var1) {
         super(() -> {
            for(int var1x = 0; var1x < var1.size(); ++var1x) {
               Entry var2 = (Entry)var1.get(var1x);
               TextureManager var3 = Minecraft.getInstance().getTextureManager();
               AbstractTexture var4 = var3.getTexture(var2.id);
               var4.setFilter(var2.blur, var2.mipmap);
               RenderSystem.setShaderTexture(var1x, var4.getId());
            }

         }, () -> {
         });
         this.cutoutTexture = var1.isEmpty() ? Optional.empty() : Optional.of(((Entry)var1.getFirst()).id);
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.cutoutTexture;
      }

      public static Builder builder() {
         return new Builder();
      }

      static record Entry(ResourceLocation id, boolean blur, boolean mipmap) {
         final ResourceLocation id;
         final boolean blur;
         final boolean mipmap;

         Entry(ResourceLocation var1, boolean var2, boolean var3) {
            super();
            this.id = var1;
            this.blur = var2;
            this.mipmap = var3;
         }
      }

      public static final class Builder {
         private final ImmutableList.Builder<Entry> builder = new ImmutableList.Builder();

         public Builder() {
            super();
         }

         public Builder add(ResourceLocation var1, boolean var2, boolean var3) {
            this.builder.add(new Entry(var1, var2, var3));
            return this;
         }

         public MultiTextureStateShard build() {
            return new MultiTextureStateShard(this.builder.build());
         }
      }
   }

   protected static class TextureStateShard extends EmptyTextureStateShard {
      private final Optional<ResourceLocation> texture;
      private final TriState blur;
      private final boolean mipmap;

      public TextureStateShard(ResourceLocation var1, TriState var2, boolean var3) {
         super(() -> {
            TextureManager var3x = Minecraft.getInstance().getTextureManager();
            AbstractTexture var4 = var3x.getTexture(var1);
            var4.setFilter(var2, var3);
            RenderSystem.setShaderTexture(0, var4.getId());
         }, () -> {
         });
         this.texture = Optional.of(var1);
         this.blur = var2;
         this.mipmap = var3;
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.texture) + "(blur=" + String.valueOf(this.blur) + ", mipmap=" + this.mipmap + ")]";
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.texture;
      }
   }

   protected static class TexturingStateShard extends RenderStateShard {
      public TexturingStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static final class OffsetTexturingStateShard extends TexturingStateShard {
      public OffsetTexturingStateShard(float var1, float var2) {
         super("offset_texturing", () -> RenderSystem.setTextureMatrix((new Matrix4f()).translation(var1, var2, 0.0F)), () -> RenderSystem.resetTextureMatrix());
      }
   }

   static class BooleanStateShard extends RenderStateShard {
      private final boolean enabled;

      public BooleanStateShard(String var1, Runnable var2, Runnable var3, boolean var4) {
         super(var1, var2, var3);
         this.enabled = var4;
      }

      public String toString() {
         return this.name + "[" + this.enabled + "]";
      }
   }

   protected static class LightmapStateShard extends BooleanStateShard {
      public LightmapStateShard(boolean var1) {
         super("lightmap", () -> {
            if (var1) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            }

         }, () -> {
            if (var1) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
            }

         }, var1);
      }
   }

   protected static class OverlayStateShard extends BooleanStateShard {
      public OverlayStateShard(boolean var1) {
         super("overlay", () -> {
            if (var1) {
               Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
            }

         }, () -> {
            if (var1) {
               Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
            }

         }, var1);
      }
   }

   protected static class CullStateShard extends BooleanStateShard {
      public CullStateShard(boolean var1) {
         super("cull", () -> {
            if (!var1) {
               RenderSystem.disableCull();
            }

         }, () -> {
            if (!var1) {
               RenderSystem.enableCull();
            }

         }, var1);
      }
   }

   protected static class DepthTestStateShard extends RenderStateShard {
      private final String functionName;

      public DepthTestStateShard(String var1, int var2) {
         super("depth_test", () -> {
            if (var2 != 519) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(var2);
            }

         }, () -> {
            if (var2 != 519) {
               RenderSystem.disableDepthTest();
               RenderSystem.depthFunc(515);
            }

         });
         this.functionName = var1;
      }

      public String toString() {
         return this.name + "[" + this.functionName + "]";
      }
   }

   protected static class WriteMaskStateShard extends RenderStateShard {
      private final boolean writeColor;
      private final boolean writeDepth;

      public WriteMaskStateShard(boolean var1, boolean var2) {
         super("write_mask_state", () -> {
            if (!var2) {
               RenderSystem.depthMask(var2);
            }

            if (!var1) {
               RenderSystem.colorMask(var1, var1, var1, var1);
            }

         }, () -> {
            if (!var2) {
               RenderSystem.depthMask(true);
            }

            if (!var1) {
               RenderSystem.colorMask(true, true, true, true);
            }

         });
         this.writeColor = var1;
         this.writeDepth = var2;
      }

      public String toString() {
         return this.name + "[writeColor=" + this.writeColor + ", writeDepth=" + this.writeDepth + "]";
      }
   }

   protected static class LayeringStateShard extends RenderStateShard {
      public LayeringStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static class OutputStateShard extends RenderStateShard {
      public OutputStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static class LineStateShard extends RenderStateShard {
      private final OptionalDouble width;

      public LineStateShard(OptionalDouble var1) {
         super("line_width", () -> {
            if (!Objects.equals(var1, OptionalDouble.of(1.0))) {
               if (var1.isPresent()) {
                  RenderSystem.lineWidth((float)var1.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(var1, OptionalDouble.of(1.0))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.width = var1;
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
      }
   }

   protected static class ColorLogicStateShard extends RenderStateShard {
      public ColorLogicStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }
}
