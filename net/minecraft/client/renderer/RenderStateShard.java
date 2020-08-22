package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderStateShard {
   protected final String name;
   private final Runnable setupState;
   private final Runnable clearState;
   protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
      RenderSystem.disableBlend();
   }, () -> {
   });
   protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard LIGHTNING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard GLINT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("glint_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard CRUMBLING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("crumbling_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
   }, () -> {
      RenderSystem.disableBlend();
   });
   protected static final RenderStateShard.AlphaStateShard NO_ALPHA = new RenderStateShard.AlphaStateShard(0.0F);
   protected static final RenderStateShard.AlphaStateShard DEFAULT_ALPHA = new RenderStateShard.AlphaStateShard(0.003921569F);
   protected static final RenderStateShard.AlphaStateShard MIDWAY_ALPHA = new RenderStateShard.AlphaStateShard(0.5F);
   protected static final RenderStateShard.ShadeModelStateShard FLAT_SHADE = new RenderStateShard.ShadeModelStateShard(false);
   protected static final RenderStateShard.ShadeModelStateShard SMOOTH_SHADE = new RenderStateShard.ShadeModelStateShard(true);
   protected static final RenderStateShard.TextureStateShard BLOCK_SHEET_MIPPED;
   protected static final RenderStateShard.TextureStateShard BLOCK_SHEET;
   protected static final RenderStateShard.TextureStateShard NO_TEXTURE;
   protected static final RenderStateShard.TexturingStateShard DEFAULT_TEXTURING;
   protected static final RenderStateShard.TexturingStateShard OUTLINE_TEXTURING;
   protected static final RenderStateShard.TexturingStateShard GLINT_TEXTURING;
   protected static final RenderStateShard.TexturingStateShard ENTITY_GLINT_TEXTURING;
   protected static final RenderStateShard.LightmapStateShard LIGHTMAP;
   protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP;
   protected static final RenderStateShard.OverlayStateShard OVERLAY;
   protected static final RenderStateShard.OverlayStateShard NO_OVERLAY;
   protected static final RenderStateShard.DiffuseLightingStateShard DIFFUSE_LIGHTING;
   protected static final RenderStateShard.DiffuseLightingStateShard NO_DIFFUSE_LIGHTING;
   protected static final RenderStateShard.CullStateShard CULL;
   protected static final RenderStateShard.CullStateShard NO_CULL;
   protected static final RenderStateShard.DepthTestStateShard NO_DEPTH_TEST;
   protected static final RenderStateShard.DepthTestStateShard EQUAL_DEPTH_TEST;
   protected static final RenderStateShard.DepthTestStateShard LEQUAL_DEPTH_TEST;
   protected static final RenderStateShard.WriteMaskStateShard COLOR_DEPTH_WRITE;
   protected static final RenderStateShard.WriteMaskStateShard COLOR_WRITE;
   protected static final RenderStateShard.WriteMaskStateShard DEPTH_WRITE;
   protected static final RenderStateShard.LayeringStateShard NO_LAYERING;
   protected static final RenderStateShard.LayeringStateShard POLYGON_OFFSET_LAYERING;
   protected static final RenderStateShard.LayeringStateShard PROJECTION_LAYERING;
   protected static final RenderStateShard.FogStateShard NO_FOG;
   protected static final RenderStateShard.FogStateShard FOG;
   protected static final RenderStateShard.FogStateShard BLACK_FOG;
   protected static final RenderStateShard.OutputStateShard MAIN_TARGET;
   protected static final RenderStateShard.OutputStateShard OUTLINE_TARGET;
   protected static final RenderStateShard.LineStateShard DEFAULT_LINE;

   public RenderStateShard(String var1, Runnable var2, Runnable var3) {
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

   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RenderStateShard var2 = (RenderStateShard)var1;
         return this.name.equals(var2.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   private static void setupGlintTexturing(float var0) {
      RenderSystem.matrixMode(5890);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      long var1 = Util.getMillis() * 8L;
      float var3 = (float)(var1 % 110000L) / 110000.0F;
      float var4 = (float)(var1 % 30000L) / 30000.0F;
      RenderSystem.translatef(-var3, var4, 0.0F);
      RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      RenderSystem.scalef(var0, var0, var0);
      RenderSystem.matrixMode(5888);
   }

   static {
      BLOCK_SHEET_MIPPED = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
      BLOCK_SHEET = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
      NO_TEXTURE = new RenderStateShard.TextureStateShard();
      DEFAULT_TEXTURING = new RenderStateShard.TexturingStateShard("default_texturing", () -> {
      }, () -> {
      });
      OUTLINE_TEXTURING = new RenderStateShard.TexturingStateShard("outline_texturing", () -> {
         RenderSystem.setupOutline();
      }, () -> {
         RenderSystem.teardownOutline();
      });
      GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("glint_texturing", () -> {
         setupGlintTexturing(8.0F);
      }, () -> {
         RenderSystem.matrixMode(5890);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
         setupGlintTexturing(0.16F);
      }, () -> {
         RenderSystem.matrixMode(5890);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
      NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
      OVERLAY = new RenderStateShard.OverlayStateShard(true);
      NO_OVERLAY = new RenderStateShard.OverlayStateShard(false);
      DIFFUSE_LIGHTING = new RenderStateShard.DiffuseLightingStateShard(true);
      NO_DIFFUSE_LIGHTING = new RenderStateShard.DiffuseLightingStateShard(false);
      CULL = new RenderStateShard.CullStateShard(true);
      NO_CULL = new RenderStateShard.CullStateShard(false);
      NO_DEPTH_TEST = new RenderStateShard.DepthTestStateShard(519);
      EQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard(514);
      LEQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard(515);
      COLOR_DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(true, true);
      COLOR_WRITE = new RenderStateShard.WriteMaskStateShard(true, false);
      DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(false, true);
      NO_LAYERING = new RenderStateShard.LayeringStateShard("no_layering", () -> {
      }, () -> {
      });
      POLYGON_OFFSET_LAYERING = new RenderStateShard.LayeringStateShard("polygon_offset_layering", () -> {
         RenderSystem.polygonOffset(-1.0F, -10.0F);
         RenderSystem.enablePolygonOffset();
      }, () -> {
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
      });
      PROJECTION_LAYERING = new RenderStateShard.LayeringStateShard("projection_layering", () -> {
         RenderSystem.matrixMode(5889);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(1.0F, 1.0F, 0.999F);
         RenderSystem.matrixMode(5888);
      }, () -> {
         RenderSystem.matrixMode(5889);
         RenderSystem.popMatrix();
         RenderSystem.matrixMode(5888);
      });
      NO_FOG = new RenderStateShard.FogStateShard("no_fog", () -> {
      }, () -> {
      });
      FOG = new RenderStateShard.FogStateShard("fog", () -> {
         FogRenderer.levelFogColor();
         RenderSystem.enableFog();
      }, () -> {
         RenderSystem.disableFog();
      });
      BLACK_FOG = new RenderStateShard.FogStateShard("black_fog", () -> {
         RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
         RenderSystem.enableFog();
      }, () -> {
         FogRenderer.levelFogColor();
         RenderSystem.disableFog();
      });
      MAIN_TARGET = new RenderStateShard.OutputStateShard("main_target", () -> {
      }, () -> {
      });
      OUTLINE_TARGET = new RenderStateShard.OutputStateShard("outline_target", () -> {
         Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
      }, () -> {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      });
      DEFAULT_LINE = new RenderStateShard.LineStateShard(OptionalDouble.of(1.0D));
   }

   public static class LineStateShard extends RenderStateShard {
      private final OptionalDouble width;

      public LineStateShard(OptionalDouble var1) {
         super("alpha", () -> {
            if (!Objects.equals(var1, OptionalDouble.of(1.0D))) {
               if (var1.isPresent()) {
                  RenderSystem.lineWidth((float)var1.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(var1, OptionalDouble.of(1.0D))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.width = var1;
      }

      public boolean equals(@Nullable Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            return !super.equals(var1) ? false : Objects.equals(this.width, ((RenderStateShard.LineStateShard)var1).width);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.width});
      }
   }

   public static class OutputStateShard extends RenderStateShard {
      public OutputStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   public static class FogStateShard extends RenderStateShard {
      public FogStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   public static class LayeringStateShard extends RenderStateShard {
      public LayeringStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   public static class WriteMaskStateShard extends RenderStateShard {
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

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.WriteMaskStateShard var2 = (RenderStateShard.WriteMaskStateShard)var1;
            return this.writeColor == var2.writeColor && this.writeDepth == var2.writeDepth;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.writeColor, this.writeDepth});
      }
   }

   public static class DepthTestStateShard extends RenderStateShard {
      private final int function;

      public DepthTestStateShard(int var1) {
         super("depth_test", () -> {
            if (var1 != 519) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(var1);
            }

         }, () -> {
            if (var1 != 519) {
               RenderSystem.disableDepthTest();
               RenderSystem.depthFunc(515);
            }

         });
         this.function = var1;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.DepthTestStateShard var2 = (RenderStateShard.DepthTestStateShard)var1;
            return this.function == var2.function;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.function);
      }
   }

   public static class CullStateShard extends RenderStateShard.BooleanStateShard {
      public CullStateShard(boolean var1) {
         super("cull", () -> {
            if (var1) {
               RenderSystem.enableCull();
            }

         }, () -> {
            if (var1) {
               RenderSystem.disableCull();
            }

         }, var1);
      }
   }

   public static class DiffuseLightingStateShard extends RenderStateShard.BooleanStateShard {
      public DiffuseLightingStateShard(boolean var1) {
         super("diffuse_lighting", () -> {
            if (var1) {
               Lighting.turnBackOn();
            }

         }, () -> {
            if (var1) {
               Lighting.turnOff();
            }

         }, var1);
      }
   }

   public static class OverlayStateShard extends RenderStateShard.BooleanStateShard {
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

   public static class LightmapStateShard extends RenderStateShard.BooleanStateShard {
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

   static class BooleanStateShard extends RenderStateShard {
      private final boolean enabled;

      public BooleanStateShard(String var1, Runnable var2, Runnable var3, boolean var4) {
         super(var1, var2, var3);
         this.enabled = var4;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.BooleanStateShard var2 = (RenderStateShard.BooleanStateShard)var1;
            return this.enabled == var2.enabled;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.enabled);
      }
   }

   public static final class PortalTexturingStateShard extends RenderStateShard.TexturingStateShard {
      private final int iteration;

      public PortalTexturingStateShard(int var1) {
         super("portal_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.scalef(0.5F, 0.5F, 1.0F);
            RenderSystem.translatef(17.0F / (float)var1, (2.0F + (float)var1 / 1.5F) * ((float)(Util.getMillis() % 800000L) / 800000.0F), 0.0F);
            RenderSystem.rotatef(((float)(var1 * var1) * 4321.0F + (float)var1 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.scalef(4.5F - (float)var1 / 4.0F, 4.5F - (float)var1 / 4.0F, 1.0F);
            RenderSystem.mulTextureByProjModelView();
            RenderSystem.matrixMode(5888);
            RenderSystem.setupEndPortalTexGen();
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            RenderSystem.clearTexGen();
         });
         this.iteration = var1;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.PortalTexturingStateShard var2 = (RenderStateShard.PortalTexturingStateShard)var1;
            return this.iteration == var2.iteration;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Integer.hashCode(this.iteration);
      }
   }

   public static final class OffsetTexturingStateShard extends RenderStateShard.TexturingStateShard {
      private final float uOffset;
      private final float vOffset;

      public OffsetTexturingStateShard(float var1, float var2) {
         super("offset_texturing", () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(var1, var2, 0.0F);
            RenderSystem.matrixMode(5888);
         }, () -> {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
         });
         this.uOffset = var1;
         this.vOffset = var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.OffsetTexturingStateShard var2 = (RenderStateShard.OffsetTexturingStateShard)var1;
            return Float.compare(var2.uOffset, this.uOffset) == 0 && Float.compare(var2.vOffset, this.vOffset) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.uOffset, this.vOffset});
      }
   }

   public static class TexturingStateShard extends RenderStateShard {
      public TexturingStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   public static class TextureStateShard extends RenderStateShard {
      private final Optional texture;
      private final boolean blur;
      private final boolean mipmap;

      public TextureStateShard(ResourceLocation var1, boolean var2, boolean var3) {
         super("texture", () -> {
            RenderSystem.enableTexture();
            TextureManager var3x = Minecraft.getInstance().getTextureManager();
            var3x.bind(var1);
            var3x.getTexture(var1).setFilter(var2, var3);
         }, () -> {
         });
         this.texture = Optional.of(var1);
         this.blur = var2;
         this.mipmap = var3;
      }

      public TextureStateShard() {
         super("texture", () -> {
            RenderSystem.disableTexture();
         }, () -> {
            RenderSystem.enableTexture();
         });
         this.texture = Optional.empty();
         this.blur = false;
         this.mipmap = false;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.TextureStateShard var2 = (RenderStateShard.TextureStateShard)var1;
            return this.texture.equals(var2.texture) && this.blur == var2.blur && this.mipmap == var2.mipmap;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.texture.hashCode();
      }

      protected Optional texture() {
         return this.texture;
      }
   }

   public static class ShadeModelStateShard extends RenderStateShard {
      private final boolean smooth;

      public ShadeModelStateShard(boolean var1) {
         super("shade_model", () -> {
            RenderSystem.shadeModel(var1 ? 7425 : 7424);
         }, () -> {
            RenderSystem.shadeModel(7424);
         });
         this.smooth = var1;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderStateShard.ShadeModelStateShard var2 = (RenderStateShard.ShadeModelStateShard)var1;
            return this.smooth == var2.smooth;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Boolean.hashCode(this.smooth);
      }
   }

   public static class AlphaStateShard extends RenderStateShard {
      private final float cutoff;

      public AlphaStateShard(float var1) {
         super("alpha", () -> {
            if (var1 > 0.0F) {
               RenderSystem.enableAlphaTest();
               RenderSystem.alphaFunc(516, var1);
            } else {
               RenderSystem.disableAlphaTest();
            }

         }, () -> {
            RenderSystem.disableAlphaTest();
            RenderSystem.defaultAlphaFunc();
         });
         this.cutoff = var1;
      }

      public boolean equals(@Nullable Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            if (!super.equals(var1)) {
               return false;
            } else {
               return this.cutoff == ((RenderStateShard.AlphaStateShard)var1).cutoff;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.cutoff});
      }
   }

   public static class TransparencyStateShard extends RenderStateShard {
      public TransparencyStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }
}
