package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PostPass {
   private final String name;
   private final RenderPipeline pipeline;
   private final CompiledShaderProgram shader;
   private final ResourceLocation outputTargetId;
   private final List<PostChainConfig.Uniform> uniforms;
   private final List<Input> inputs = new ArrayList();

   public PostPass(RenderPipeline var1, CompiledShaderProgram var2, ResourceLocation var3, List<PostChainConfig.Uniform> var4) {
      super();
      this.pipeline = var1;
      this.name = var1.getLocation().toString();
      this.shader = var2;
      this.outputTargetId = var3;
      this.uniforms = var4;
   }

   public void addInput(Input var1) {
      this.inputs.add(var1);
   }

   public void addToFrame(FrameGraphBuilder var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2, Matrix4f var3) {
      FramePass var4 = var1.addPass(this.name);

      for(Input var6 : this.inputs) {
         var6.addToPass(var4, var2);
      }

      ResourceHandle var7 = (ResourceHandle)var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> var4.readsAndWrites(var2x));
      if (var7 == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         var4.executes(() -> {
            RenderTarget var4 = (RenderTarget)var7.get();
            RenderSystem.viewport(0, 0, var4.width, var4.height);
            var4.bindWrite(false);
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(var3, ProjectionType.ORTHOGRAPHIC);
            VertexBuffer var5 = RenderSystem.getQuadVertices();
            var5.drawWithRenderPipeline(this.pipeline, (var3x) -> {
               for(Input var5 : this.inputs) {
                  var5.bindTo(var3x, var2);
               }

               var3x.safeGetUniform("OutSize").set((float)var4.width, (float)var4.height);

               for(PostChainConfig.Uniform var9 : this.uniforms) {
                  if (var9.values().isPresent()) {
                     Uniform var6 = var3x.getUniform(var9.name());
                     if (var6 != null) {
                        List var7 = (List)var9.values().get();
                        var6.setFromConfig(var7, var7.size());
                     }
                  }
               }

            });
            RenderSystem.restoreProjectionMatrix();
            var4.unbindWrite();

            for(Input var7x : this.inputs) {
               var7x.cleanup(var2);
            }

         });
      }
   }

   public CompiledShaderProgram getShader() {
      return this.shader;
   }

   public interface Input {
      void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

      void bindTo(CompiledShaderProgram var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

      default void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1) {
      }
   }

   public static record TextureInput(String samplerName, AbstractTexture texture, int width, int height) implements Input {
      public TextureInput(String var1, AbstractTexture var2, int var3, int var4) {
         super();
         this.samplerName = var1;
         this.texture = var2;
         this.width = var3;
         this.height = var4;
      }

      public void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
      }

      public void bindTo(CompiledShaderProgram var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
         var1.bindSampler(this.samplerName + "Sampler", this.texture.getTexture());
         var1.safeGetUniform(this.samplerName + "Size").set((float)this.width, (float)this.height);
      }
   }

   public static record TargetInput(String samplerName, ResourceLocation targetId, boolean depthBuffer, boolean bilinear) implements Input {
      public TargetInput(String var1, ResourceLocation var2, boolean var3, boolean var4) {
         super();
         this.samplerName = var1;
         this.targetId = var2;
         this.depthBuffer = var3;
         this.bilinear = var4;
      }

      private ResourceHandle<RenderTarget> getHandle(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1) {
         ResourceHandle var2 = (ResourceHandle)var1.get(this.targetId);
         if (var2 == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
         } else {
            return var2;
         }
      }

      public void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
         var1.reads(this.getHandle(var2));
      }

      public void bindTo(CompiledShaderProgram var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
         ResourceHandle var3 = this.getHandle(var2);
         RenderTarget var4 = (RenderTarget)var3.get();
         var4.setFilterMode(this.bilinear ? FilterMode.LINEAR : FilterMode.NEAREST);
         var1.bindSampler(this.samplerName + "Sampler", this.depthBuffer ? var4.getDepthTexture() : var4.getColorTexture());
         var1.safeGetUniform(this.samplerName + "Size").set((float)var4.width, (float)var4.height);
      }

      public void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1) {
         if (this.bilinear) {
            ((RenderTarget)this.getHandle(var1).get()).setFilterMode(FilterMode.NEAREST);
         }

      }
   }
}
