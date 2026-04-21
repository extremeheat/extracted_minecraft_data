package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

public class QuadParticleFeatureRenderer implements AutoCloseable {
   private final StagedVertexBuffer stagedVertexBuffer = new StagedVertexBuffer(() -> "Quad Particles", 1048576);

   public QuadParticleFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.render(nodeCollection, context, false);
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      this.render(nodeCollection, context, true);
   }

   private void render(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context, final boolean translucent) {
      List<QuadParticleRenderState> groups = nodeCollection.getQuadParticleGroups();
      if (!groups.isEmpty()) {
         Map<SingleQuadParticle.Layer, StagedVertexBuffer.Draw> drawByLayer = new IdentityHashMap();

         for(QuadParticleRenderState particles : groups) {
            if (!particles.isEmpty()) {
               for(SingleQuadParticle.Layer layer : particles.layers()) {
                  if (layer.translucent() == translucent) {
                     StagedVertexBuffer.Draw draw = (StagedVertexBuffer.Draw)drawByLayer.computeIfAbsent(layer, (var1) -> this.stagedVertexBuffer.appendDraw(DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, (VertexSorting)null));
                     particles.buildLayer(layer, this.stagedVertexBuffer.getVertexBuilder(draw));
                  }
               }
            }
         }

         if (!drawByLayer.isEmpty()) {
            this.stagedVertexBuffer.upload();
            GpuDevice device = RenderSystem.getDevice();
            Minecraft minecraft = Minecraft.getInstance();
            RenderTarget mainTarget = minecraft.gameRenderer.mainRenderTarget();
            RenderTarget particleTarget = minecraft.levelRenderer.particlesTarget();
            boolean useParticleTarget = particleTarget != null && translucent;
            GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
            GpuTextureView colorTextureView = useParticleTarget ? particleTarget.getColorTextureView() : mainTarget.getColorTextureView();
            GpuTextureView depthTextureView = useParticleTarget ? particleTarget.getDepthTextureView() : mainTarget.getDepthTextureView();

            try (RenderPass renderPass = device.createCommandEncoder().createRenderPass(() -> "Particles - " + (translucent ? "Translucent" : "Solid"), colorTextureView, OptionalInt.empty(), depthTextureView, OptionalDouble.empty())) {
               renderPass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
               renderPass.setUniform("Fog", RenderSystem.getShaderFog());
               renderPass.setUniform("DynamicTransforms", dynamicTransforms);
               renderPass.bindTexture("Sampler2", context.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
               drawLayers(this.stagedVertexBuffer, drawByLayer, renderPass, context.textureManager());
            }

            this.stagedVertexBuffer.endDraw();
         }
      }
   }

   private static void drawLayers(final StagedVertexBuffer stagedBuffer, final Map<SingleQuadParticle.Layer, StagedVertexBuffer.Draw> layers, final RenderPass renderPass, final TextureManager textureManager) {
      for(Map.Entry<SingleQuadParticle.Layer, StagedVertexBuffer.Draw> entry : layers.entrySet()) {
         StagedVertexBuffer.ExecuteInfo executeInfo = stagedBuffer.getExecuteInfo((StagedVertexBuffer.Draw)entry.getValue());
         if (executeInfo != null) {
            renderPass.setPipeline(((SingleQuadParticle.Layer)entry.getKey()).pipeline());
            renderPass.setVertexBuffer(0, executeInfo.vertexBuffer());
            renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
            AbstractTexture texture = textureManager.getTexture(((SingleQuadParticle.Layer)entry.getKey()).textureAtlasLocation());
            renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
            renderPass.drawIndexed(executeInfo.baseVertex(), executeInfo.firstIndex(), executeInfo.indexCount(), 1);
         }
      }

   }

   public void endFrame() {
      this.stagedVertexBuffer.endFrame();
   }

   public void close() {
      this.stagedVertexBuffer.close();
   }
}
