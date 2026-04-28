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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jspecify.annotations.Nullable;

public class QuadParticleFeatureRenderer implements FeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Particle");
   private final List<PreparedGroup> groups = new ArrayList();
   private @Nullable GpuBufferSlice dynamicTransforms;

   public QuadParticleFeatureRenderer() {
      super();
   }

   public void prepareGroup(final FeatureFrameContext context, final List<Submit> submits, final boolean strictlyOrdered) {
      if (!submits.isEmpty()) {
         StagedVertexBuffer stagedVertexBuffer = context.stagedVertexBuffer();
         Map<SingleQuadParticle.Layer, StagedVertexBuffer.Draw> drawByLayer = new IdentityHashMap();

         for(Submit submit : submits) {
            QuadParticleRenderState particles = submit.particles();
            if (!particles.isEmpty()) {
               for(SingleQuadParticle.Layer layer : particles.layers()) {
                  if (layer.translucent() == submit.translucent()) {
                     StagedVertexBuffer.Draw draw = (StagedVertexBuffer.Draw)drawByLayer.computeIfAbsent(layer, (var1) -> stagedVertexBuffer.appendDraw(DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS, (VertexSorting)null));
                     particles.buildLayer(layer, stagedVertexBuffer.getVertexBuilder(draw));
                  }
               }
            }
         }

         boolean translucent = ((Submit)submits.getFirst()).translucent();
         this.groups.add(new PreparedGroup(drawByLayer, translucent));
      }
   }

   public void finishPrepare(final FeatureFrameContext context) {
      this.dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
   }

   public void executeGroup(final FeatureFrameContext context, final int groupIndex, final List<Submit> submits, final boolean strictlyOrdered) {
      PreparedGroup group = (PreparedGroup)this.groups.get(groupIndex);
      GpuDevice device = RenderSystem.getDevice();
      Minecraft minecraft = Minecraft.getInstance();
      RenderTarget mainTarget = minecraft.gameRenderer.mainRenderTarget();
      RenderTarget particleTarget = minecraft.levelRenderer.particlesTarget();
      boolean useParticleTarget = particleTarget != null && group.translucent;
      GpuTextureView colorTextureView = useParticleTarget ? particleTarget.getColorTextureView() : mainTarget.getColorTextureView();
      GpuTextureView depthTextureView = useParticleTarget ? particleTarget.getDepthTextureView() : mainTarget.getDepthTextureView();

      try (RenderPass renderPass = device.createCommandEncoder().createRenderPass(() -> "Particles - " + (group.translucent ? "Translucent" : "Solid"), colorTextureView, OptionalInt.empty(), depthTextureView, OptionalDouble.empty())) {
         renderPass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
         renderPass.setUniform("Fog", RenderSystem.getShaderFog());
         renderPass.setUniform("DynamicTransforms", (GpuBufferSlice)Objects.requireNonNull(this.dynamicTransforms));
         renderPass.bindTexture("Sampler2", context.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
         drawLayers(context.stagedVertexBuffer(), group.layers, renderPass, context.textureManager());
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

   public void finishExecute(final FeatureFrameContext context) {
      this.groups.clear();
      this.dynamicTransforms = null;
   }

   private static record PreparedGroup(Map<SingleQuadParticle.Layer, StagedVertexBuffer.Draw> layers, boolean translucent) {
      private PreparedGroup {
         super();
      }
   }

   public static record Submit(QuadParticleRenderState particles, boolean translucent) implements SubmitNode {
      public Submit {
         super();
      }

      public FeatureRendererType<Submit> featureType() {
         return QuadParticleFeatureRenderer.TYPE;
      }
   }
}
