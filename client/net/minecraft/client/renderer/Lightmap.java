package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.OptionalInt;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.dimension.DimensionType;

public class Lightmap implements AutoCloseable {
   public static final int TEXTURE_SIZE = 16;
   private static final int LIGHTMAP_UBO_SIZE = (new Std140SizeCalculator()).putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putVec3().putVec3().putVec3().putVec3().get();
   private final GpuTexture texture;
   private final GpuTextureView textureView;
   private final MappableRingBuffer ubo;

   public Lightmap() {
      super();
      GpuDevice device = RenderSystem.getDevice();
      this.texture = device.createTexture("Lightmap", 13, TextureFormat.RGBA8, 16, 16, 1, 1);
      this.textureView = device.createTextureView(this.texture);
      device.createCommandEncoder().clearColorTexture(this.texture, -1);
      this.ubo = new MappableRingBuffer(() -> "Lightmap UBO", 130, LIGHTMAP_UBO_SIZE);
   }

   public GpuTextureView getTextureView() {
      return this.textureView;
   }

   public void close() {
      this.texture.close();
      this.textureView.close();
      this.ubo.close();
   }

   public void update(final LightmapRenderState renderState) {
      if (renderState.needsUpdate) {
         ProfilerFiller profiler = Profiler.get();
         profiler.push("lightmapUpdate");
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

         try (GpuBuffer.MappedView view = commandEncoder.mapBuffer(this.ubo.currentBuffer(), false, true)) {
            Std140Builder.intoBuffer(view.data()).putFloat(renderState.skyFactor).putFloat(renderState.blockFactor).putFloat(renderState.nightVisionEffectIntensity).putFloat(renderState.darknessEffectScale).putFloat(renderState.bossOverlayWorldDarkening).putFloat(renderState.brightness).putVec3(renderState.blockLightTint).putVec3(renderState.skyLightColor).putVec3(renderState.ambientColor).putVec3(renderState.nightVisionColor);
         }

         try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Update light", this.textureView, OptionalInt.empty())) {
            renderPass.setPipeline(RenderPipelines.LIGHTMAP);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("LightmapInfo", this.ubo.currentBuffer());
            renderPass.draw(0, 3);
         }

         this.ubo.rotate();
         profiler.pop();
      }
   }

   public static float getBrightness(final DimensionType dimensionType, final int level) {
      float v = (float)level / 15.0F;
      float curvedV = v / (4.0F - 3.0F * v);
      return Mth.lerp(dimensionType.ambientLight(), curvedV, 1.0F);
   }
}
