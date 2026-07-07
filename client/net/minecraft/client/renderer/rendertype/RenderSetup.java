package net.minecraft.client.renderer.rendertype;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public final class RenderSetup {
   final RenderPipeline pipeline;
   final @Nullable OitPipelineSet oitPipelineSet;
   final @Nullable RenderPipeline opaquePartsPipeline;
   final Map<String, TextureBinding> textures;
   final TextureTransform textureTransform;
   final OutlineProperty outlineProperty;
   final boolean useLightmap;
   final boolean useOverlay;
   final boolean affectsCrumbling;
   final boolean sortOnUpload;
   final LayeringTransform layeringTransform;
   final boolean forceSolidModelPhase;

   private RenderSetup(final RenderPipeline pipeline, final @Nullable OitPipelineSet oitPipelineSet, final @Nullable RenderPipeline opaquePartsPipeline, final Map<String, TextureBinding> textures, final boolean useLightmap, final boolean useOverlay, final LayeringTransform layeringTransform, final TextureTransform textureTransform, final OutlineProperty outlineProperty, final boolean affectsCrumbling, final boolean sortOnUpload, final boolean forceSolidModelPhase) {
      super();
      this.pipeline = pipeline;
      this.oitPipelineSet = oitPipelineSet;
      this.opaquePartsPipeline = opaquePartsPipeline;
      this.textures = textures;
      this.textureTransform = textureTransform;
      this.useLightmap = useLightmap;
      this.useOverlay = useOverlay;
      this.outlineProperty = outlineProperty;
      this.layeringTransform = layeringTransform;
      this.affectsCrumbling = affectsCrumbling;
      this.sortOnUpload = sortOnUpload;
      this.forceSolidModelPhase = forceSolidModelPhase;
   }

   public String toString() {
      String var10000 = String.valueOf(this.layeringTransform);
      return "RenderSetup[layeringTransform=" + var10000 + ", textureTransform=" + String.valueOf(this.textureTransform) + ", textures=" + String.valueOf(this.textures) + ", outlineProperty=" + String.valueOf(this.outlineProperty) + ", useLightmap=" + this.useLightmap + ", useOverlay=" + this.useOverlay + "]";
   }

   public static RenderSetupBuilder builder(final RenderPipeline pipeline) {
      return new RenderSetupBuilder(pipeline);
   }

   public List<PreparedRenderType.Texture> prepareTextures(final TextureManager textureManager, final SamplerCache samplerCache, final GpuTextureView overlayTexture, final GpuTextureView lightmapTexture) {
      if (this.textures.isEmpty() && !this.useOverlay && !this.useLightmap) {
         return List.of();
      } else {
         ImmutableList.Builder<PreparedRenderType.Texture> textures = ImmutableList.builderWithExpectedSize(this.textures.size() + 2);
         if (this.useOverlay) {
            textures.add(new PreparedRenderType.Texture("Sampler1", overlayTexture, samplerCache.getClampToEdge(FilterMode.LINEAR)));
         }

         if (this.useLightmap) {
            textures.add(new PreparedRenderType.Texture("Sampler2", lightmapTexture, samplerCache.getClampToEdge(FilterMode.LINEAR)));
         }

         for(Map.Entry<String, TextureBinding> entry : this.textures.entrySet()) {
            AbstractTexture texture = textureManager.getTexture(((TextureBinding)entry.getValue()).location);
            GpuSampler samplerOverride = (GpuSampler)((TextureBinding)entry.getValue()).sampler().get();
            textures.add(new PreparedRenderType.Texture((String)entry.getKey(), texture.getTextureView(), samplerOverride != null ? samplerOverride : texture.getSampler()));
         }

         return textures.build();
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
      private @Nullable OitPipelineSet oitPipelineSet;
      private @Nullable RenderPipeline opaquePartsPipeline;
      private boolean useLightmap = false;
      private boolean useOverlay = false;
      private LayeringTransform layeringTransform;
      private OutputTarget outputTarget;
      private @Nullable OutputTarget oitOutputTarget;
      private TextureTransform textureTransform;
      private boolean affectsCrumbling;
      private boolean sortOnUpload;
      private OutlineProperty outlineProperty;
      private final Map<String, TextureBinding> textures;
      private boolean forceSolidModelPhase;

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

      public RenderSetupBuilder setTextureTransform(final TextureTransform textureTransform) {
         this.textureTransform = textureTransform;
         return this;
      }

      public RenderSetupBuilder setOutline(final OutlineProperty outlineProperty) {
         this.outlineProperty = outlineProperty;
         return this;
      }

      public RenderSetupBuilder setOitPipelines(final OitPipelineSet oitPipelineSet) {
         this.oitPipelineSet = oitPipelineSet;
         return this;
      }

      public RenderSetupBuilder setOpaquePartsPipeline(final RenderPipeline opaquePartsPipeline) {
         this.opaquePartsPipeline = opaquePartsPipeline;
         return this;
      }

      public RenderSetupBuilder withForcedSolidModelPhase() {
         this.forceSolidModelPhase = true;
         return this;
      }

      public RenderSetup createRenderSetup() {
         return new RenderSetup(this.pipeline, this.oitPipelineSet, this.opaquePartsPipeline, this.textures, this.useLightmap, this.useOverlay, this.layeringTransform, this.textureTransform, this.outlineProperty, this.affectsCrumbling, this.sortOnUpload, this.forceSolidModelPhase);
      }
   }

   static record TextureBinding(Identifier location, Supplier<@Nullable GpuSampler> sampler) {
      TextureBinding {
         super();
      }
   }
}
