package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PostPass {
   private final String name;
   private final RenderPipeline pipeline;
   private final ResourceLocation outputTargetId;
   private final List<PostChainConfig.Uniform> uniforms;
   private final List<Input> inputs = new ArrayList();

   public PostPass(RenderPipeline var1, ResourceLocation var2, List<PostChainConfig.Uniform> var3) {
      super();
      this.pipeline = var1;
      this.name = var1.getLocation().toString();
      this.outputTargetId = var2;
      this.uniforms = var3;
   }

   public void addInput(Input var1) {
      this.inputs.add(var1);
   }

   public void addToFrame(FrameGraphBuilder var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2, Matrix4f var3, @Nullable Consumer<RenderPass> var4) {
      FramePass var5 = var1.addPass(this.name);

      for(Input var7 : this.inputs) {
         var7.addToPass(var5, var2);
      }

      ResourceHandle var8 = (ResourceHandle)var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> var5.readsAndWrites(var2x));
      if (var8 == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         var5.executes(() -> {
            RenderTarget var5 = (RenderTarget)var8.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(var3, ProjectionType.ORTHOGRAPHIC);
            GpuBuffer var6 = RenderSystem.getQuadVertexBuffer();
            RenderSystem.AutoStorageIndexBuffer var7 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);

            try (RenderPass var8x = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var5.getColorTexture(), OptionalInt.empty(), var5.useDepth ? var5.getDepthTexture() : null, OptionalDouble.empty())) {
               var8x.setPipeline(this.pipeline);
               var8x.setUniform("OutSize", (float)var5.width, (float)var5.height);
               var8x.setVertexBuffer(0, var6);
               var8x.setIndexBuffer(var7.getBuffer(6), var7.type());

               for(Input var10 : this.inputs) {
                  var10.bindTo(var8x, var2);
               }

               if (var4 != null) {
                  var4.accept(var8x);
               }

               for(PostChainConfig.Uniform var16 : this.uniforms) {
                  var16.setOnRenderPass(var8x);
               }

               var8x.drawIndexed(0, 6);
            }

            RenderSystem.restoreProjectionMatrix();

            for(Input var15 : this.inputs) {
               var15.cleanup(var2);
            }

         });
      }
   }

   public interface Input {
      void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

      void bindTo(RenderPass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

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

      public void bindTo(RenderPass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
         var1.bindSampler(this.samplerName + "Sampler", this.texture.getTexture());
         var1.setUniform(this.samplerName + "Size", (float)this.width, (float)this.height);
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

      public void bindTo(RenderPass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2) {
         ResourceHandle var3 = this.getHandle(var2);
         RenderTarget var4 = (RenderTarget)var3.get();
         var4.setFilterMode(this.bilinear ? FilterMode.LINEAR : FilterMode.NEAREST);
         GpuTexture var5 = this.depthBuffer ? var4.getDepthTexture() : var4.getColorTexture();
         if (var5 == null) {
            String var10002 = this.depthBuffer ? "depth" : "color";
            throw new IllegalStateException("Missing " + var10002 + "texture for target " + String.valueOf(this.targetId));
         } else {
            var1.bindSampler(this.samplerName + "Sampler", var5);
            var1.setUniform(this.samplerName + "Size", (float)var4.width, (float)var4.height);
         }
      }

      public void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1) {
         if (this.bilinear) {
            ((RenderTarget)this.getHandle(var1).get()).setFilterMode(FilterMode.NEAREST);
         }

      }
   }
}
