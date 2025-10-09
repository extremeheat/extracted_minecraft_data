package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public abstract class RenderStateShard {
   public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
   protected final String name;
   private final Runnable setupState;
   private final Runnable clearState;
   protected static final TextureStateShard BLOCK_SHEET;
   protected static final EmptyTextureStateShard NO_TEXTURE;
   protected static final TexturingStateShard DEFAULT_TEXTURING;
   protected static final TexturingStateShard GLINT_TEXTURING;
   protected static final TexturingStateShard ENTITY_GLINT_TEXTURING;
   protected static final TexturingStateShard ARMOR_ENTITY_GLINT_TEXTURING;
   protected static final LightmapStateShard LIGHTMAP;
   protected static final LightmapStateShard NO_LIGHTMAP;
   protected static final OverlayStateShard OVERLAY;
   protected static final OverlayStateShard NO_OVERLAY;
   protected static final LayeringStateShard NO_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING_FORWARD;
   protected static final OutputStateShard MAIN_TARGET;
   protected static final OutputStateShard OUTLINE_TARGET;
   protected static final OutputStateShard WEATHER_TARGET;
   protected static final OutputStateShard ITEM_ENTITY_TARGET;

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

   public String getName() {
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
      BLOCK_SHEET = new TextureStateShard(TextureAtlas.LOCATION_BLOCKS);
      NO_TEXTURE = new EmptyTextureStateShard();
      DEFAULT_TEXTURING = new TexturingStateShard("default_texturing", () -> {
      }, () -> {
      });
      GLINT_TEXTURING = new TexturingStateShard("glint_texturing", () -> setupGlintTexturing(8.0F), RenderSystem::resetTextureMatrix);
      ENTITY_GLINT_TEXTURING = new TexturingStateShard("entity_glint_texturing", () -> setupGlintTexturing(0.5F), RenderSystem::resetTextureMatrix);
      ARMOR_ENTITY_GLINT_TEXTURING = new TexturingStateShard("armor_entity_glint_texturing", () -> setupGlintTexturing(0.16F), RenderSystem::resetTextureMatrix);
      LIGHTMAP = new LightmapStateShard(true);
      NO_LIGHTMAP = new LightmapStateShard(false);
      OVERLAY = new OverlayStateShard(true);
      NO_OVERLAY = new OverlayStateShard(false);
      NO_LAYERING = new LayeringStateShard("no_layering", () -> {
      }, () -> {
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
      MAIN_TARGET = new OutputStateShard("main_target", () -> Minecraft.getInstance().getMainRenderTarget());
      OUTLINE_TARGET = new OutputStateShard("outline_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.entityOutlineTarget();
         return var0 != null ? var0 : Minecraft.getInstance().getMainRenderTarget();
      });
      WEATHER_TARGET = new OutputStateShard("weather_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getWeatherTarget();
         return var0 != null ? var0 : Minecraft.getInstance().getMainRenderTarget();
      });
      ITEM_ENTITY_TARGET = new OutputStateShard("item_entity_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getItemEntityTarget();
         return var0 != null ? var0 : Minecraft.getInstance().getMainRenderTarget();
      });
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

      MultiTextureStateShard(List<ResourceLocation> var1) {
         super(() -> {
            for(int var1x = 0; var1x < var1.size(); ++var1x) {
               ResourceLocation var2 = (ResourceLocation)var1.get(var1x);
               TextureManager var3 = Minecraft.getInstance().getTextureManager();
               AbstractTexture var4 = var3.getTexture(var2);
               RenderSystem.setShaderTexture(var1x, var4.getTextureView(), var4.getSampler());
            }

         }, () -> {
         });
         this.cutoutTexture = var1.isEmpty() ? Optional.empty() : Optional.of((ResourceLocation)var1.getFirst());
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.cutoutTexture;
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {
         private final ImmutableList.Builder<ResourceLocation> builder = new ImmutableList.Builder();

         public Builder() {
            super();
         }

         public Builder add(ResourceLocation var1) {
            this.builder.add(var1);
            return this;
         }

         public MultiTextureStateShard build() {
            return new MultiTextureStateShard(this.builder.build());
         }
      }
   }

   protected static class TextureStateShard extends EmptyTextureStateShard {
      private final Optional<ResourceLocation> texture;

      public TextureStateShard(ResourceLocation var1) {
         super(() -> {
            TextureManager var1x = Minecraft.getInstance().getTextureManager();
            AbstractTexture var2 = var1x.getTexture(var1);
            RenderSystem.setShaderTexture(0, var2.getTextureView(), var2.getSampler());
         }, () -> {
         });
         this.texture = Optional.of(var1);
      }

      public String toString() {
         String var10000 = this.name;
         return var10000 + "[" + String.valueOf(this.texture) + "]";
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

   protected static class LayeringStateShard extends RenderStateShard {
      public LayeringStateShard(String var1, Runnable var2, Runnable var3) {
         super(var1, var2, var3);
      }
   }

   protected static class OutputStateShard extends RenderStateShard {
      private final Supplier<RenderTarget> renderTargetSupplier;

      public OutputStateShard(String var1, Supplier<RenderTarget> var2) {
         super(var1, () -> {
         }, () -> {
         });
         this.renderTargetSupplier = var2;
      }

      public RenderTarget getRenderTarget() {
         return (RenderTarget)this.renderTargetSupplier.get();
      }
   }
}
