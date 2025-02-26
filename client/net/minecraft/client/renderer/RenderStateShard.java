package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
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
   protected static final LayeringStateShard NO_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING;
   protected static final LayeringStateShard VIEW_OFFSET_Z_LAYERING_FORWARD;
   protected static final OutputStateShard MAIN_TARGET;
   protected static final OutputStateShard OUTLINE_TARGET;
   protected static final OutputStateShard TRANSLUCENT_TARGET;
   protected static final OutputStateShard PARTICLES_TARGET;
   protected static final OutputStateShard WEATHER_TARGET;
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
      TRANSLUCENT_TARGET = new OutputStateShard("translucent_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getTranslucentTarget();
         return var0 != null ? var0 : Minecraft.getInstance().getMainRenderTarget();
      });
      PARTICLES_TARGET = new OutputStateShard("particles_target", () -> {
         RenderTarget var0 = Minecraft.getInstance().levelRenderer.getParticlesTarget();
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
      DEFAULT_LINE = new LineStateShard(OptionalDouble.of(1.0));
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
               RenderSystem.setShaderTexture(var1x, var4.getTexture());
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
            RenderSystem.setShaderTexture(0, var4.getTexture());
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
}
