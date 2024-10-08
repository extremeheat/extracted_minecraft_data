package net.minecraft.client.renderer;

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
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PostPass {
   private final String name;
   private final CompiledShaderProgram shader;
   private final ResourceLocation outputTargetId;
   private final List<PostChainConfig.Uniform> uniforms;
   private final List<PostPass.Input> inputs = new ArrayList<>();

   public PostPass(String var1, CompiledShaderProgram var2, ResourceLocation var3, List<PostChainConfig.Uniform> var4) {
      super();
      this.name = var1;
      this.shader = var2;
      this.outputTargetId = var3;
      this.uniforms = var4;
   }

   public void addInput(PostPass.Input var1) {
      this.inputs.add(var1);
   }

   public void addToFrame(FrameGraphBuilder var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2, Matrix4f var3) {
      FramePass var4 = var1.addPass(this.name);

      for (PostPass.Input var6 : this.inputs) {
         var6.addToPass(var4, var2);
      }

      ResourceHandle var7 = var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> var4.readsAndWrites(var2x));
      if (var7 == null) {
         throw new IllegalStateException("Missing handle for target " + this.outputTargetId);
      } else {
         var4.executes(() -> {
            RenderTarget var4x = (RenderTarget)var7.get();
            RenderSystem.viewport(0, 0, var4x.width, var4x.height);

            for (PostPass.Input var6x : this.inputs) {
               var6x.bindTo(this.shader, var2);
            }

            this.shader.safeGetUniform("OutSize").set((float)var4x.width, (float)var4x.height);

            for (PostChainConfig.Uniform var10 : this.uniforms) {
               Uniform var7x = this.shader.getUniform(var10.name());
               if (var7x != null) {
                  var7x.setFromConfig(var10.values(), var10.values().size());
               }
            }

            var4x.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var4x.clear();
            var4x.bindWrite(false);
            RenderSystem.depthFunc(519);
            RenderSystem.setShader(this.shader);
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(var3, VertexSorting.ORTHOGRAPHIC_Z);
            BufferBuilder var9 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            var9.addVertex(0.0F, 0.0F, 500.0F);
            var9.addVertex((float)var4x.width, 0.0F, 500.0F);
            var9.addVertex((float)var4x.width, (float)var4x.height, 500.0F);
            var9.addVertex(0.0F, (float)var4x.height, 500.0F);
            BufferUploader.drawWithShader(var9.buildOrThrow());
            RenderSystem.depthFunc(515);
            RenderSystem.restoreProjectionMatrix();
            var4x.unbindWrite();

            for (PostPass.Input var12 : this.inputs) {
               var12.cleanup(var2);
            }

            this.restoreDefaultUniforms();
         });
      }
   }

   private void restoreDefaultUniforms() {
      for (PostChainConfig.Uniform var2 : this.uniforms) {
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
