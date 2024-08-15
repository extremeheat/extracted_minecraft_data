package net.minecraft.client.renderer;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;

public class PostPass implements AutoCloseable {
   private final EffectInstance effect;
   public final ResourceLocation outputTargetId;
   private final List<PostPass.Input> inputs = new ArrayList<>();

   public PostPass(ResourceProvider var1, String var2, ResourceLocation var3) throws IOException {
      super();
      this.effect = new EffectInstance(var1, var2);
      this.outputTargetId = var3;
   }

   @Override
   public void close() {
      this.effect.close();
   }

   public final String getName() {
      return this.effect.getName();
   }

   public void addInput(PostPass.Input var1) {
      this.inputs.add(var1);
   }

   public void addToFrame(FrameGraphBuilder var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2, Matrix4f var3, float var4) {
      FramePass var5 = var1.addPass(this.getName());

      for (PostPass.Input var7 : this.inputs) {
         var7.addToPass(var5, var2);
      }

      ResourceHandle var8 = var2.computeIfPresent(this.outputTargetId, (var1x, var2x) -> var5.readsAndWrites(var2x));
      if (var8 == null) {
         throw new IllegalStateException("Missing handle for target " + this.outputTargetId);
      } else {
         var5.executes(() -> {
            RenderTarget var5x = (RenderTarget)var8.get();
            RenderSystem.viewport(0, 0, var5x.width, var5x.height);

            for (PostPass.Input var7x : this.inputs) {
               var7x.bindTo(this.effect, var2);
            }

            this.effect.safeGetUniform("ProjMat").set(var3);
            this.effect.safeGetUniform("OutSize").set((float)var5x.width, (float)var5x.height);
            this.effect.safeGetUniform("Time").set(var4);
            Minecraft var10 = Minecraft.getInstance();
            this.effect.safeGetUniform("ScreenSize").set((float)var10.getWindow().getWidth(), (float)var10.getWindow().getHeight());
            this.effect.apply();
            var5x.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            var5x.clear();
            var5x.bindWrite(false);
            RenderSystem.depthFunc(519);
            BufferBuilder var11 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            var11.addVertex(0.0F, 0.0F, 500.0F);
            var11.addVertex((float)var5x.width, 0.0F, 500.0F);
            var11.addVertex((float)var5x.width, (float)var5x.height, 500.0F);
            var11.addVertex(0.0F, (float)var5x.height, 500.0F);
            BufferUploader.draw(var11.buildOrThrow());
            RenderSystem.depthFunc(515);
            this.effect.clear();
            var5x.unbindWrite();

            for (PostPass.Input var9 : this.inputs) {
               var9.cleanup(var2);
            }
         });
      }
   }

   public EffectInstance getEffect() {
      return this.effect;
   }

   public interface Input {
      void addToPass(FramePass var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

      void bindTo(EffectInstance var1, Map<ResourceLocation, ResourceHandle<RenderTarget>> var2);

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
