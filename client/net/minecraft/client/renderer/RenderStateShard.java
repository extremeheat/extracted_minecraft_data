package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

public abstract class RenderStateShard {
   private static final float VIEW_SCALE_Z_EPSILON = 0.99975586F;
   protected final String name;
   private final Runnable setupState;
   private final Runnable clearState;
   protected static final TransparencyStateShard NO_TRANSPARENCY = new TransparencyStateShard("no_transparency", () -> {
      RenderSystem.disableBlend();
   }, () -> {
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
   protected static final TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final ShaderStateShard NO_SHADER = new ShaderStateShard();
   protected static final ShaderStateShard BLOCK_SHADER = new ShaderStateShard(GameRenderer::getBlockShader);
   protected static final ShaderStateShard NEW_ENTITY_SHADER = new ShaderStateShard(GameRenderer::getNewEntityShader);
   protected static final ShaderStateShard POSITION_COLOR_LIGHTMAP_SHADER = new ShaderStateShard(GameRenderer::getPositionColorLightmapShader);
   protected static final ShaderStateShard POSITION_SHADER = new ShaderStateShard(GameRenderer::getPositionShader);
   protected static final ShaderStateShard POSITION_COLOR_TEX_SHADER = new ShaderStateShard(GameRenderer::getPositionColorTexShader);
   protected static final ShaderStateShard POSITION_TEX_SHADER = new ShaderStateShard(GameRenderer::getPositionTexShader);
   protected static final ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_SHADER = new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader);
   protected static final ShaderStateShard POSITION_COLOR_SHADER = new ShaderStateShard(GameRenderer::getPositionColorShader);
   protected static final ShaderStateShard RENDERTYPE_SOLID_SHADER = new ShaderStateShard(GameRenderer::getRendertypeSolidShader);
   protected static final ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCutoutMippedShader);
   protected static final ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
   protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentShader);
   protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentMovingBlockShader);
   protected static final ShaderStateShard RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTranslucentNoCrumblingShader);
   protected static final ShaderStateShard RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorCutoutNoCullShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullZOffsetShader);
   protected static final ShaderStateShard RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeItemEntityTranslucentCullShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentCullShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntitySmoothCutoutShader);
   protected static final ShaderStateShard RENDERTYPE_BEACON_BEAM_SHADER = new ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_DECAL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityDecalShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_NO_OUTLINE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityNoOutlineShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_SHADOW_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityShadowShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_ALPHA_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityAlphaShader);
   protected static final ShaderStateShard RENDERTYPE_EYES_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEyesShader);
   protected static final ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
   protected static final ShaderStateShard RENDERTYPE_LEASH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLeashShader);
   protected static final ShaderStateShard RENDERTYPE_WATER_MASK_SHADER = new ShaderStateShard(GameRenderer::getRendertypeWaterMaskShader);
   protected static final ShaderStateShard RENDERTYPE_OUTLINE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeOutlineShader);
   protected static final ShaderStateShard RENDERTYPE_ARMOR_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorGlintShader);
   protected static final ShaderStateShard RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeArmorEntityGlintShader);
   protected static final ShaderStateShard RENDERTYPE_GLINT_TRANSLUCENT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintTranslucentShader);
   protected static final ShaderStateShard RENDERTYPE_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintShader);
   protected static final ShaderStateShard RENDERTYPE_GLINT_DIRECT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeGlintDirectShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityGlintShader);
   protected static final ShaderStateShard RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityGlintDirectShader);
   protected static final ShaderStateShard RENDERTYPE_CRUMBLING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeCrumblingShader);
   protected static final ShaderStateShard RENDERTYPE_TEXT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextShader);
   protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextIntensityShader);
   protected static final ShaderStateShard RENDERTYPE_TEXT_SEE_THROUGH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextSeeThroughShader);
   protected static final ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTextIntensitySeeThroughShader);
   protected static final ShaderStateShard RENDERTYPE_LIGHTNING_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLightningShader);
   protected static final ShaderStateShard RENDERTYPE_TRIPWIRE_SHADER = new ShaderStateShard(GameRenderer::getRendertypeTripwireShader);
   protected static final ShaderStateShard RENDERTYPE_END_PORTAL_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEndPortalShader);
   protected static final ShaderStateShard RENDERTYPE_END_GATEWAY_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEndGatewayShader);
   protected static final ShaderStateShard RENDERTYPE_LINES_SHADER = new ShaderStateShard(GameRenderer::getRendertypeLinesShader);
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
   protected static final WriteMaskStateShard COLOR_DEPTH_WRITE;
   protected static final WriteMaskStateShard COLOR_WRITE;
   protected static final WriteMaskStateShard DEPTH_WRITE;
   protected static final LayeringStateShard NO_LAYERING;
   protected static final LayeringStateShard POLYGON_OFFSET_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING;
   protected static final OutputStateShard MAIN_TARGET;
   protected static final OutputStateShard OUTLINE_TARGET;
   protected static final OutputStateShard TRANSLUCENT_TARGET;
   protected static final OutputStateShard PARTICLES_TARGET;
   protected static final OutputStateShard WEATHER_TARGET;
   protected static final OutputStateShard CLOUDS_TARGET;
   protected static final OutputStateShard ITEM_ENTITY_TARGET;
   protected static final LineStateShard DEFAULT_LINE;

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
      long var1 = Util.getMillis() * 8L;
      float var3 = (float)(var1 % 110000L) / 110000.0F;
      float var4 = (float)(var1 % 30000L) / 30000.0F;
      Matrix4f var5 = Matrix4f.createTranslateMatrix(-var3, var4, 0.0F);
      var5.multiply(Vector3f.ZP.rotationDegrees(10.0F));
      var5.multiply(Matrix4f.createScaleMatrix(var0, var0, var0));
      RenderSystem.setTextureMatrix(var5);
   }

   static {
      BLOCK_SHEET_MIPPED = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
      BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
      NO_TEXTURE = new EmptyTextureStateShard();
      DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {
      }, () -> {
      });
      GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> {
         setupGlintTexturing(8.0F);
      }, () -> {
         RenderSystem.resetTextureMatrix();
      });
      ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> {
         setupGlintTexturing(0.16F);
      }, () -> {
         RenderSystem.resetTextureMatrix();
      });
      LIGHTMAP = new LightmapStateShard(true);
      NO_LIGHTMAP = new LightmapStateShard(false);
      OVERLAY = new OverlayStateShard(true);
      NO_OVERLAY = new OverlayStateShard(false);
      CULL = new CullStateShard(true);
      NO_CULL = new CullStateShard(false);
      NO_DEPTH_TEST = new DepthTestStateShard("always", 519);
      EQUAL_DEPTH_TEST = new DepthTestStateShard("==", 514);
      LEQUAL_DEPTH_TEST = new DepthTestStateShard("<=", 515);
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
         PoseStack var0 = RenderSystem.getModelViewStack();
         var0.pushPose();
         var0.scale(0.99975586F, 0.99975586F, 0.99975586F);
         RenderSystem.applyModelViewMatrix();
      }, () -> {
         PoseStack var0 = RenderSystem.getModelViewStack();
         var0.popPose();
         RenderSystem.applyModelViewMatrix();
      });
      MAIN_TARGET = new OutputStateShard("main_target", () -> {
      }, () -> {
      });
      OUTLINE_TARGET = new OutputStateShard("outline_target", () -> {
         Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
      }, () -> {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      });
      TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
         }

      }, () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      });
      PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
         }

      }, () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      });
      WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getWeatherTarget().bindWrite(false);
         }

      }, () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      });
      CLOUDS_TARGET = new OutputStateShard("clouds_target", () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getCloudsTarget().bindWrite(false);
         }

      }, () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      });
      ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
         }

      }, () -> {
         if (Minecraft.useShaderTransparency()) {
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
         }

      });
      DEFAULT_LINE = new LineStateShard(OptionalDouble.of(1.0));
   }

   protected static class TransparencyStateShard extends RenderStateShard {
      public TransparencyStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static class ShaderStateShard extends RenderStateShard {
      private final Optional<Supplier<ShaderInstance>> shader;

      public ShaderStateShard(Supplier<ShaderInstance> var1) {
         super("shader", () -> {
            RenderSystem.setShader(var1);
         }, () -> {
         });
         this.shader = Optional.of(var1);
      }

      public ShaderStateShard() {
         super("shader", () -> {
            RenderSystem.setShader(() -> {
               return null;
            });
         }, () -> {
         });
         this.shader = Optional.empty();
      }

      public String toString() {
         return this.name + "[" + this.shader + "]";
      }
   }

   protected static class TextureStateShard extends EmptyTextureStateShard {
      private final Optional<ResourceLocation> texture;
      private final boolean blur;
      private final boolean mipmap;

      public TextureStateShard(ResourceLocation var1, boolean var2, boolean var3) {
         super(() -> {
            RenderSystem.enableTexture();
            TextureManager var3x = Minecraft.getInstance().getTextureManager();
            var3x.getTexture(var1).setFilter(var2, var3);
            RenderSystem.setShaderTexture(0, var1);
         }, () -> {
         });
         this.texture = Optional.of(var1);
         this.blur = var2;
         this.mipmap = var3;
      }

      public String toString() {
         return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.texture;
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

   protected static class TexturingStateShard extends RenderStateShard {
      public TexturingStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
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
         return var10000 + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
      }
   }

   private static class BooleanStateShard extends RenderStateShard {
      private final boolean enabled;

      public BooleanStateShard(String var1, Runnable var2, Runnable var3, boolean var4) {
         super(var1, var2, var3);
         this.enabled = var4;
      }

      public String toString() {
         return this.name + "[" + this.enabled + "]";
      }
   }

   protected static final class OffsetTexturingStateShard extends TexturingStateShard {
      public OffsetTexturingStateShard(float var1, float var2) {
         super("offset_texturing", () -> {
            RenderSystem.setTextureMatrix(Matrix4f.createTranslateMatrix(var1, var2, 0.0F));
         }, () -> {
            RenderSystem.resetTextureMatrix();
         });
      }
   }

   protected static class MultiTextureStateShard extends EmptyTextureStateShard {
      private final Optional<ResourceLocation> cutoutTexture;

      MultiTextureStateShard(ImmutableList<Triple<ResourceLocation, Boolean, Boolean>> var1) {
         super(() -> {
            int var1x = 0;
            UnmodifiableIterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Triple var3 = (Triple)var2.next();
               TextureManager var4 = Minecraft.getInstance().getTextureManager();
               var4.getTexture((ResourceLocation)var3.getLeft()).setFilter((Boolean)var3.getMiddle(), (Boolean)var3.getRight());
               RenderSystem.setShaderTexture(var1x++, (ResourceLocation)var3.getLeft());
            }

         }, () -> {
         });
         this.cutoutTexture = var1.stream().findFirst().map(Triple::getLeft);
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.cutoutTexture;
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {
         private final ImmutableList.Builder<Triple<ResourceLocation, Boolean, Boolean>> builder = new ImmutableList.Builder();

         public Builder() {
            super();
         }

         public Builder add(ResourceLocation var1, boolean var2, boolean var3) {
            this.builder.add(Triple.of(var1, var2, var3));
            return this;
         }

         public MultiTextureStateShard build() {
            return new MultiTextureStateShard(this.builder.build());
         }
      }
   }
}
