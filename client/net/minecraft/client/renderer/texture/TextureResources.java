package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.file.Path;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;

public record TextureResources(GpuTexture texture, GpuTextureView textureView, GpuSampler sampler, IntUnaryOperator dumpModifier) implements TextureHandle {
   public TextureResources(final GpuTexture texture, final GpuTextureView textureView, final GpuSampler sampler) {
      this(texture, textureView, sampler, (argb) -> argb);
   }

   public TextureResources {
      super();
   }

   public void dumpContents(final Identifier selfId, final Path dir) {
      String outputId = selfId.toDebugFileName();
      if ((this.texture.usage() & 2) != 0) {
         TextureUtil.writeAsPNG(dir, outputId, this.texture, 0, this.dumpModifier);
      }

   }

   public static TextureResources from2dImage(final Supplier<String> label, final NativeImage image) {
      return from2dImage(label, image, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST));
   }

   public static TextureResources from2dImage(final Supplier<String> label, final NativeImage image, final GpuSampler sampler) {
      GpuDevice device = RenderSystem.getDevice();
      GpuTexture texture = device.createTexture(label, 5, GpuFormat.RGBA8_UNORM, image.getWidth(), image.getHeight(), 1, 1);
      GpuTextureView textureView = device.createTextureView(texture);
      image.writeToGpuTexture(device.createCommandEncoder(), texture);
      return new TextureResources(texture, textureView, sampler);
   }

   public void close() {
      this.texture.close();
      this.textureView.close();
   }
}
