package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.oit.OitStage;
import org.jspecify.annotations.Nullable;

public record PreparedRenderType(String name, Function<@Nullable OitStage, @Nullable RenderPipeline> pipelineProvider, GpuBufferSlice dynamicTransforms, List<Texture> textures) {
   public PreparedRenderType {
      super();
   }

   public void drawFromBuffer(final StagedVertexBuffer.ExecuteInfo info, final RenderPass renderPass) {
      RenderPipeline pipeline = (RenderPipeline)this.pipelineProvider.apply((Object)null);
      if (pipeline == null) {
         throw new IllegalStateException("Render type " + this.name + " does not have a non-OIT pipeline set up.");
      } else {
         this.draw(info, renderPass, pipeline);
      }
   }

   public void drawFromBufferOit(final StagedVertexBuffer.ExecuteInfo info, final OitStage stage, final RenderPass renderPass) {
      RenderPipeline pipeline = (RenderPipeline)this.pipelineProvider.apply(stage);
      if (pipeline == null) {
         throw new IllegalStateException("Render type " + this.name + " does not have OIT pipelines set up.");
      } else {
         this.draw(info, renderPass, pipeline);
      }
   }

   private void draw(final StagedVertexBuffer.ExecuteInfo info, final RenderPass renderPass, final RenderPipeline renderPipeline) {
      renderPass.pushDebugGroup(() -> "Render Type " + this.name);
      GpuBuffer indexBuffer = info.indexBuffer();
      renderPass.setPipeline(RenderSystem.getCompiledPipeline(renderPipeline));
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.setUniform("DynamicTransforms", this.dynamicTransforms);
      renderPass.setVertexBuffer(0, info.vertexBuffer().slice());

      for(Texture texture : this.textures) {
         renderPass.setUniform(texture.name, texture.textureView, texture.sampler);
      }

      renderPass.setIndexBuffer(indexBuffer, info.indexType());
      renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
      renderPass.popDebugGroup();
   }

   public static record Texture(String name, GpuTextureView textureView, GpuSampler sampler) {
      public Texture {
         super();
      }
   }
}
