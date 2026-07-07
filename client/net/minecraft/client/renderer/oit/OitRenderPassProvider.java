package net.minecraft.client.renderer.oit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.OutputTarget;

public class OitRenderPassProvider {
   public OitRenderPassProvider() {
      super();
   }

   public static RenderPass createRenderPass(final OitStage stage, final Supplier<String> label, final Parameters params) {
      RenderPass renderPass;
      switch (stage) {
         case DEPTH_BOUNDS -> renderPass = createDepthBoundsPass(label, params);
         case TRANSMITTANCE -> renderPass = createTransmittancePass(label, params);
         case ACCUMULATE -> renderPass = createAccumulatePass(label, params);
         default -> throw new IllegalArgumentException("Invalid OIT stage.");
      }

      return renderPass;
   }

   private static RenderPass createDepthBoundsPass(final Supplier<String> label, final Parameters params) {
      RenderPassDescriptor descriptor = RenderPassDescriptor.builder(() -> "OIT Depth Bounds for " + (String)label.get()).withColorAttachment(params.depthBoundsTargetView).withDepthAttachment(params.depthTextureView).build();
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor);
      RenderSystem.bindDefaultUniforms(renderPass);
      return renderPass;
   }

   private static RenderPass createTransmittancePass(final Supplier<String> label, final Parameters params) {
      RenderPassDescriptor.Builder descriptor = RenderPassDescriptor.builder(() -> "OIT Transmittance for " + (String)label.get()).withDepthAttachment(params.depthTextureView, OptionalDouble.empty());

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         descriptor.withColorAttachment(OutputTarget.TRANSMITTANCE_TARGETS[i].getRenderTarget().getColorTextureView(), Optional.empty());
      }

      GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor.build());
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.bindTexture("DepthBoundsSampler", params.depthBoundsTargetView, nearestSampler);
      return renderPass;
   }

   private static RenderPass createAccumulatePass(final Supplier<String> label, final Parameters params) {
      RenderPassDescriptor descriptor = RenderPassDescriptor.builder(() -> "OIT Accumulate for " + (String)label.get()).withColorAttachment(params.accumulateTargetView).withDepthAttachment(params.depthTextureView).build();
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor);
      GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      RenderSystem.bindDefaultUniforms(renderPass);

      for(int i = 0; i < LevelRenderer.OIT_TRANSMITTANCE_TARGET_COUNT; ++i) {
         renderPass.bindTexture("Coeff" + i, OutputTarget.TRANSMITTANCE_TARGETS[i].getRenderTarget().getColorTextureView(), nearestSampler);
      }

      renderPass.bindTexture("DepthBoundsSampler", params.depthBoundsTargetView, nearestSampler);
      return renderPass;
   }

   public static record Parameters(GpuTextureView depthBoundsTargetView, GpuTextureView accumulateTargetView, GpuTextureView depthTextureView) {
      public Parameters {
         super();
      }
   }
}
