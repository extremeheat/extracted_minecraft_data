package net.minecraft.client.renderer.rendertype;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class RenderSetup {
   final RenderPipeline pipeline;
   final Map<String, TextureBinding> textures;
   final TextureTransform textureTransform;
   final OutputTarget outputTarget;
   final OutlineProperty outlineProperty;
   final boolean useLightmap;
   final boolean useOverlay;
   final boolean affectsCrumbling;
   final boolean sortOnUpload;
   final LayeringTransform layeringTransform;

   private RenderSetup(final RenderPipeline pipeline, final Map<String, TextureBinding> textures, final boolean useLightmap, final boolean useOverlay, final LayeringTransform layeringTransform, final OutputTarget outputTarget, final TextureTransform textureTransform, final OutlineProperty outlineProperty, final boolean affectsCrumbling, final boolean sortOnUpload) {
      super();
      this.pipeline = pipeline;
      this.textures = textures;
      this.outputTarget = outputTarget;
      this.textureTransform = textureTransform;
      this.useLightmap = useLightmap;
      this.useOverlay = useOverlay;
      this.outlineProperty = outlineProperty;
      this.layeringTransform = layeringTransform;
      this.affectsCrumbling = affectsCrumbling;
      this.sortOnUpload = sortOnUpload;
   }

   public String toString() {
      String var10000 = String.valueOf(this.layeringTransform);
      return "RenderSetup[layeringTransform=" + var10000 + ", textureTransform=" + String.valueOf(this.textureTransform) + ", textures=" + String.valueOf(this.textures) + ", outlineProperty=" + String.valueOf(this.outlineProperty) + ", useLightmap=" + this.useLightmap + ", useOverlay=" + this.useOverlay + "]";
   }

   public static RenderSetupBuilder builder(final RenderPipeline pipeline) {
      return new RenderSetupBuilder(pipeline);
   }

   public Map<String, TextureAndSampler> getTextures() {
      if (this.textures.isEmpty() && !this.useOverlay && !this.useLightmap) {
         return Collections.emptyMap();
      } else {
         Map<String, TextureAndSampler> result = new HashMap();
         if (this.useOverlay) {
            result.put("Sampler1", new TextureAndSampler(Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)));
         }

         if (this.useLightmap) {
            result.put("Sampler2", new TextureAndSampler(Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)));
         }

         TextureManager textureManager = Minecraft.getInstance().getTextureManager();

         for(Map.Entry<String, TextureBinding> entry : this.textures.entrySet()) {
            AbstractTexture texture = textureManager.getTexture(((TextureBinding)entry.getValue()).location);
            GpuSampler samplerOverride = (GpuSampler)((TextureBinding)entry.getValue()).sampler().get();
            result.put((String)entry.getKey(), new TextureAndSampler(texture.getTextureView(), samplerOverride != null ? samplerOverride : texture.getSampler()));
         }

         return result;
      }
   }

   public static enum OutlineProperty {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineProperty(final String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static OutlineProperty[] $values() {
         return new OutlineProperty[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
      }
   }

   public static class RenderSetupBuilder {
      private final RenderPipeline pipeline;
      private boolean useLightmap = false;
      private boolean useOverlay = false;
      private LayeringTransform layeringTransform;
      private OutputTarget outputTarget;
      private TextureTransform textureTransform;
      private boolean affectsCrumbling;
      private boolean sortOnUpload;
      private OutlineProperty outlineProperty;
      private final Map<String, TextureBinding> textures;

      private RenderSetupBuilder(final RenderPipeline pipeline) {
         super();
         this.layeringTransform = LayeringTransform.NO_LAYERING;
         this.outputTarget = OutputTarget.MAIN_TARGET;
         this.textureTransform = TextureTransform.DEFAULT_TEXTURING;
         this.affectsCrumbling = false;
         this.sortOnUpload = false;
         this.outlineProperty = RenderSetup.OutlineProperty.NONE;
         this.textures = new HashMap();
         this.pipeline = pipeline;
      }

      public RenderSetupBuilder withTexture(final String name, final Identifier texture) {
         this.textures.put(name, new TextureBinding(texture, () -> null));
         return this;
      }

      public RenderSetupBuilder withTexture(final String name, final Identifier texture, final @Nullable Supplier<GpuSampler> sampler) {
         this.textures.put(name, new TextureBinding(texture, Suppliers.memoize(() -> sampler == null ? null : (GpuSampler)sampler.get())));
         return this;
      }

      public RenderSetupBuilder useLightmap() {
         this.useLightmap = true;
         return this;
      }

      public RenderSetupBuilder useOverlay() {
         this.useOverlay = true;
         return this;
      }

      public RenderSetupBuilder affectsCrumbling() {
         this.affectsCrumbling = true;
         return this;
      }

      public RenderSetupBuilder sortOnUpload() {
         this.sortOnUpload = true;
         return this;
      }

      public RenderSetupBuilder setLayeringTransform(final LayeringTransform layeringTransform) {
         this.layeringTransform = layeringTransform;
         return this;
      }

      public RenderSetupBuilder setOutputTarget(final OutputTarget outputTarget) {
         this.outputTarget = outputTarget;
         return this;
      }

      public RenderSetupBuilder setTextureTransform(final TextureTransform textureTransform) {
         this.textureTransform = textureTransform;
         return this;
      }

      public RenderSetupBuilder setOutline(final OutlineProperty outlineProperty) {
         this.outlineProperty = outlineProperty;
         return this;
      }

      public RenderSetup createRenderSetup() {
         return new RenderSetup(this.pipeline, this.textures, this.useLightmap, this.useOverlay, this.layeringTransform, this.outputTarget, this.textureTransform, this.outlineProperty, this.affectsCrumbling, this.sortOnUpload);
      }
   }

   public static record TextureAndSampler(GpuTextureView textureView, GpuSampler sampler) {
      public TextureAndSampler {
         super();
      }
   }

   static record TextureBinding(Identifier location, Supplier<@Nullable GpuSampler> sampler) {
      TextureBinding {
         super();
      }
   }
}
