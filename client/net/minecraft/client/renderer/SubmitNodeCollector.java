package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.feature.ParticleFeatureRenderer;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jspecify.annotations.Nullable;

public interface SubmitNodeCollector extends OrderedSubmitNodeCollector {
   OrderedSubmitNodeCollector order(int order);

   public interface CustomGeometryRenderer {
      void render(PoseStack.Pose pose, VertexConsumer buffer);
   }

   public interface ParticleGroupRenderer {
      boolean isEmpty();

      QuadParticleRenderState.@Nullable PreparedBuffers prepare(ParticleFeatureRenderer.ParticleBufferCache buffer, boolean translucent);

      void render(QuadParticleRenderState.PreparedBuffers buffers, ParticleFeatureRenderer.ParticleBufferCache bufferCache, RenderPass renderPass, TextureManager textureManager);
   }
}
