package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PostPass {
   private final String name;
   private final CompiledShaderProgram shader;
   private final ResourceLocation outputTargetId;
   private final List<PostChainConfig.Uniform> uniforms;
   private final List<Input> inputs = new ArrayList();

   public PostPass(String var1, CompiledShaderProgram var2, ResourceLocation var3, List<PostChainConfig.Uniform> var4) {
      super();
      this.name = var1;
      this.shader = var2;
      this.outputTargetId = var3;
      this.uniforms = var4;
   }

   public void addInput(Input var1) {
      this.inputs.add(var1);
   }

   public void addToFrame(FrameGraphBuilder var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2, Matrix4f var3) {
      FramePass var4 = var1.addPass(this.name);
      Iterator var5 = this.inputs.iterator();

      while(var5.hasNext()) {
         Input var6 = (Input)var5.next();
         var6.addToPass(var4, var2);
      }

      ResourceHandle var7 = (ResourceHandle)var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> {
         return var4.readsAndWrites(var2x);
      });
      if (var7 == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         var4.executes(() -> {
            RenderTarget var4 = (RenderTarget)var7.get();
            RenderSystem.viewport(0, 0, var4.width, var4.height);
            Iterator var5 = this.inputs.iterator();

            while(var5.hasNext()) {
               Input var6 = (Input)var5.next();
               var6.bindTo(this.shader, var2);
            }

            this.shader.safeGetUniform("OutSize").set((float)var4.width, (float)var4.height);
            var5 = this.uniforms.iterator();

            while(var5.hasNext()) {
               PostChainConfig.Uniform var9 = (PostChainConfig.Uniform)var5.next();
               Uniform var7x = this.shader.getUniform(var9.name());
               if (var7x != null) {
                  var7x.setFromConfig(var9.values(), var9.values().size());
               }
            }

            var4.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var4.clear();
            var4.bindWrite(false);
            RenderSystem.depthFunc(519);
            RenderSystem.setShader(this.shader);
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(var3, ProjectionType.ORTHOGRAPHIC);
            BufferBuilder var8 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            var8.addVertex(0.0F, 0.0F, 500.0F);
            var8.addVertex((float)var4.width, 0.0F, 500.0F);
            var8.addVertex((float)var4.width, (float)var4.height, 500.0F);
            var8.addVertex(0.0F, (float)var4.height, 500.0F);
            BufferUploader.drawWithShader(var8.buildOrThrow());
            RenderSystem.depthFunc(515);
            RenderSystem.restoreProjectionMatrix();
            var4.unbindWrite();
            Iterator var10 = this.inputs.iterator();

            while(var10.hasNext()) {
               Input var11 = (Input)var10.next();
               var11.cleanup(var2);
            }

            this.restoreDefaultUniforms();
         });
      }
   }

   private void restoreDefaultUniforms() {
      Iterator var1 = this.uniforms.iterator();

      while(var1.hasNext()) {
         PostChainConfig.Uniform var2 = (PostChainConfig.Uniform)var1.next();
         String var3 = var2.name();
         Uniform var4 = this.shader.getUniform(var3);
         ShaderProgramConfig.Uniform var5 = this.shader.getUniformConfig(var3);
         if (var4 != null && var5 != null && !var2.values().equals(var5.values())) {
            var4.setFromConfig(var5);
         }
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
         var4.setFilterMode(this.bilinear ? 9729 : 9728);
         var1.bindSampler(this.samplerName + "Sampler", this.depthBuffer ? var4.getDepthTextureId() : var4.getColorTextureId());
         var1.safeGetUniform(this.samplerName + "Size").set((float)var4.width, (float)var4.height);
      }

      public void cleanup(Map<ResourceLocation, ResourceHandle<RenderTarget>> var1) {
         if (this.bilinear) {
            ((RenderTarget)this.getHandle(var1).get()).setFilterMode(9728);
         }

      }

      public String samplerName() {
         return this.samplerName;
      }

      public ResourceLocation targetId() {
         return this.targetId;
      }

      public boolean depthBuffer() {
         return this.depthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
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
         var1.bindSampler(this.samplerName + "Sampler", this.texture.getId());
         var1.safeGetUniform(this.samplerName + "Size").set((float)this.width, (float)this.height);
      }

      public String samplerName() {
         return this.samplerName;
      }

      public AbstractTexture texture() {
         return this.texture;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }
}
