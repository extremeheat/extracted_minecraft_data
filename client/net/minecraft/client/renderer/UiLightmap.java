package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.function.Supplier;

public class UiLightmap implements AutoCloseable {
   private final GpuTextureView textureView;

   public UiLightmap() {
      super();
      GpuDevice device = RenderSystem.getDevice();
      GpuTexture texture = device.createTexture((Supplier)(() -> "UI Lightmap"), 5, GpuFormat.RGBA8_UNORM, 1, 1, 1, 1);
      this.textureView = device.createTextureView(texture);

      try (NativeImage pixels = new NativeImage(1, 1, false)) {
         pixels.setPixel(0, 0, -1);
         pixels.writeToGpuTexture(device.createCommandEncoder(), texture);
      }

      texture.close();
   }

   public GpuTextureView getTextureView() {
      return this.textureView;
   }

   public void close() {
      this.textureView.close();
   }
}
