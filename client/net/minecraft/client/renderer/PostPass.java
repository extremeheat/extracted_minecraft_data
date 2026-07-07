package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.MemoryStack;

public class PostPass implements AutoCloseable {
   private static final int UBO_SIZE_PER_SAMPLER = (new Std140SizeCalculator()).putVec2().get();
   private final String name;
   private final RenderPipeline pipeline;
   private final Identifier outputTargetId;
   private final Map<String, GpuBuffer> customUniforms = new HashMap();
   private final MappableRingBuffer infoUbo;
   private final List<Input> inputs;

   public PostPass(final RenderPipeline pipeline, final Identifier outputTargetId, final Map<String, List<UniformValue>> uniformGroups, final List<Input> inputs) {
      super();
      this.pipeline = pipeline;
      this.name = pipeline.getLocation().toString();
      this.outputTargetId = outputTargetId;
      this.inputs = inputs;

      for(Map.Entry<String, List<UniformValue>> uniformGroup : uniformGroups.entrySet()) {
         List<UniformValue> uniforms = (List)uniformGroup.getValue();
         if (!uniforms.isEmpty()) {
            Std140SizeCalculator calculator = new Std140SizeCalculator();

            for(UniformValue uniform : uniforms) {
               uniform.addSize(calculator);
            }

            int size = calculator.get();
            MemoryStack stack = MemoryStack.stackPush();

            try {
               Std140Builder builder = Std140Builder.onStack(stack, size);

               for(UniformValue uniform : uniforms) {
                  uniform.writeTo(builder);
               }

               this.customUniforms.put((String)uniformGroup.getKey(), RenderSystem.getDevice().createBuffer(() -> {
                  String var10000 = this.name;
                  return var10000 + " / " + (String)uniformGroup.getKey();
               }, 128, builder.get()));
            } catch (Throwable var15) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }
               }

               throw var15;
            }

            if (stack != null) {
               stack.close();
            }
         }
      }

      this.infoUbo = new MappableRingBuffer(() -> this.name + " SamplerInfo", 130, (inputs.size() + 1) * UBO_SIZE_PER_SAMPLER);
   }

   public void addToFrame(final FrameGraphBuilder frame, final Map<Identifier, ResourceHandle<RenderTarget>> targets, final GpuBufferSlice shaderOrthoMatrix) {
      FramePass pass = frame.addPass(this.name);

      for(Input input : this.inputs) {
         input.addToPass(pass, targets);
      }

      ResourceHandle<RenderTarget> outputHandle = (ResourceHandle)targets.computeIfPresent(this.outputTargetId, (id, handle) -> pass.readsAndWrites(handle));
      if (outputHandle == null) {
         throw new IllegalStateException("Missing handle for target " + String.valueOf(this.outputTargetId));
      } else {
         pass.executes(() -> {
            RenderTarget outputTarget = outputHandle.get();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(shaderOrthoMatrix, ProjectionType.ORTHOGRAPHIC);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            SamplerCache samplerCache = RenderSystem.getSamplerCache();
            List<InputTexture> inputTextures = this.inputs.stream().map((i) -> new InputTexture(i.samplerName(), i.texture(targets), samplerCache.getClampToEdge(i.bilinear() ? FilterMode.LINEAR : FilterMode.NEAREST))).toList();

            try (GpuBufferSlice.MappedView view = this.infoUbo.currentBuffer().map(false, true)) {
               Std140Builder builder = Std140Builder.intoBuffer(view.data());
               builder.putVec2((float)outputTarget.width, (float)outputTarget.height);

               for(InputTexture input : inputTextures) {
                  builder.putVec2((float)input.view.getWidth(0), (float)input.view.getHeight(0));
               }
            }

            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Post pass " + this.name, outputTarget.getColorTextureView(), Optional.empty(), outputTarget.hasDepth() ? outputTarget.getDepthTextureView() : null, OptionalDouble.empty())) {
               renderPass.setPipeline(RenderSystem.getCompiledPipeline(this.pipeline));
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("SamplerInfo", this.infoUbo.currentBuffer());

               for(Map.Entry<String, GpuBuffer> entry : this.customUniforms.entrySet()) {
                  renderPass.setUniform((String)entry.getKey(), (GpuBuffer)entry.getValue());
               }

               for(InputTexture input : inputTextures) {
                  renderPass.bindTexture(input.samplerName() + "Sampler", input.view(), input.sampler());
               }

               renderPass.draw(3, 1, 0, 0);
            }

            this.infoUbo.rotate();
            RenderSystem.restoreProjectionMatrix();

            for(Input input : this.inputs) {
               input.cleanup(targets);
            }

         });
      }
   }

   public void close() {
      for(GpuBuffer buffer : this.customUniforms.values()) {
         buffer.close();
      }

      this.infoUbo.close();
   }

   public interface Input {
      void addToPass(FramePass pass, Map<Identifier, ResourceHandle<RenderTarget>> targets);

      default void cleanup(final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
      }

      GpuTextureView texture(final Map<Identifier, ResourceHandle<RenderTarget>> targets);

      String samplerName();

      boolean bilinear();
   }

   public static record TextureInput(String samplerName, AbstractTexture texture, int width, int height, boolean bilinear) implements Input {
      public TextureInput {
         super();
      }

      public void addToPass(final FramePass pass, final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
      }

      public GpuTextureView texture(final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
         return this.texture.getTextureView();
      }
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean depthBuffer, boolean bilinear) implements Input {
      public TargetInput {
         super();
      }

      private ResourceHandle<RenderTarget> getHandle(final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
         ResourceHandle<RenderTarget> handle = (ResourceHandle)targets.get(this.targetId);
         if (handle == null) {
            throw new IllegalStateException("Missing handle for target " + String.valueOf(this.targetId));
         } else {
            return handle;
         }
      }

      public void addToPass(final FramePass pass, final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
         pass.reads(this.getHandle(targets));
      }

      public GpuTextureView texture(final Map<Identifier, ResourceHandle<RenderTarget>> targets) {
         ResourceHandle<RenderTarget> handle = this.getHandle(targets);
         RenderTarget target = handle.get();
         GpuTextureView textureView = this.depthBuffer ? target.getDepthTextureView() : target.getColorTextureView();
         if (textureView == null) {
            String var10002 = this.depthBuffer ? "depth" : "color";
            throw new IllegalStateException("Missing " + var10002 + "texture for target " + String.valueOf(this.targetId));
         } else {
            return textureView;
         }
      }
   }

   private static record InputTexture(String samplerName, GpuTextureView view, GpuSampler sampler) {
      private InputTexture {
         super();
      }
   }
}
