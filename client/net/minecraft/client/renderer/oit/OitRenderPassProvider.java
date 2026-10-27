package net.minecraft.client.renderer.oit;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.function.Supplier;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderPassLayouts;

public class OitRenderPassProvider {
   public OitRenderPassProvider() {
      super();
   }

   public static RenderPass createRenderPass(final OitStage stage, final Supplier<String> label, final Parameters params, final boolean shouldClearTargets) {
      RenderPass renderPass;
      switch (stage) {
         case DEPTH_BOUNDS -> renderPass = createDepthBoundsPass(label, params, shouldClearTargets);
         case TRANSMITTANCE -> renderPass = createTransmittancePass(label, params, shouldClearTargets);
         case ACCUMULATE -> renderPass = createAccumulatePass(label, params, shouldClearTargets);
         default -> throw new IllegalArgumentException("Invalid OIT stage.");
      }

      return renderPass;
   }

   private static RenderPass createDepthBoundsPass(final Supplier<String> label, final Parameters params, final boolean shouldClearTargets) {
      RenderPassDescriptor.Builder descriptor = RenderPassDescriptor.builder(() -> "OIT Depth Bounds for " + (String)label.get(), RenderPassLayouts.OIT_DEPTH_BOUNDS);
      if (shouldClearTargets) {
         descriptor.withAttachment(params.depthBoundsTargetView, LevelRenderer.DEPTH_BOUNDS_CLEAR_COLOR);
      } else {
         descriptor.withAttachment(params.depthBoundsTargetView);
      }

      descriptor.withAttachment(params.depthTextureView);
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor.build());
      RenderSystem.bindDefaultUniforms(renderPass);
      return renderPass;
   }

   private static RenderPass createTransmittancePass(final Supplier<String> label, final Parameters params, final boolean shouldClearTargets) {
      RenderPassDescriptor.Builder descriptor = RenderPassDescriptor.builder(() -> "OIT Transmittance for " + (String)label.get(), RenderPassLayouts.OIT_TRANSMITTANCE);

      for(int i = 0; i < 2; ++i) {
         if (shouldClearTargets) {
            descriptor.withAttachment(params.transmittanceTargetViews[i], LevelRenderer.ZERO_CLEAR_COLOR);
         } else {
            descriptor.withAttachment(params.transmittanceTargetViews[i]);
         }
      }

      descriptor.withAttachment(params.depthTextureView);
      GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor.build());
      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.setUniform("DepthBoundsSampler", params.depthBoundsTargetView, nearestSampler);
      return renderPass;
   }

   private static RenderPass createAccumulatePass(final Supplier<String> label, final Parameters params, final boolean shouldClearTargets) {
      RenderPassDescriptor.Builder descriptor = RenderPassDescriptor.builder(() -> "OIT Accumulate for " + (String)label.get(), RenderPassLayouts.OIT_ACCUMULATE);
      if (shouldClearTargets) {
         descriptor.withAttachment(params.accumulateTargetView, LevelRenderer.ZERO_CLEAR_COLOR);
      } else {
         descriptor.withAttachment(params.accumulateTargetView);
      }

      descriptor.withAttachment(params.depthTextureView);
      RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(descriptor.build());
      GpuSampler nearestSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
      RenderSystem.bindDefaultUniforms(renderPass);

      for(int i = 0; i < 2; ++i) {
         renderPass.setUniform("Bins" + i, params.transmittanceTargetViews[i], nearestSampler);
      }

      renderPass.setUniform("DepthBoundsSampler", params.depthBoundsTargetView, nearestSampler);
      return renderPass;
   }

   public static final class Parameters {
      private GpuTextureView depthBoundsTargetView;
      private final GpuTextureView[] transmittanceTargetViews;
      private final GpuTextureView accumulateTargetView;
      private final GpuTextureView depthTextureView;

      public Parameters(final GpuTextureView depthBoundsTargetView, final GpuTextureView[] transmittanceTargetViews, final GpuTextureView accumulateTargetView, final GpuTextureView depthTextureView) {
         super();
         this.depthBoundsTargetView = depthBoundsTargetView;
         this.transmittanceTargetViews = transmittanceTargetViews;
         this.accumulateTargetView = accumulateTargetView;
         this.depthTextureView = depthTextureView;
      }

      public void setDepthBoundsTargetView(final GpuTextureView depthBoundsTargetView) {
         this.depthBoundsTargetView = depthBoundsTargetView;
      }
   }
}
