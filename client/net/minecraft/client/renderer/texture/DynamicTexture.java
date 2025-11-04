package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DynamicTexture extends AbstractTexture implements Dumpable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private @Nullable NativeImage pixels;

   public DynamicTexture(Supplier<String> var1, NativeImage var2) {
      super();
      this.pixels = var2;
      this.createTexture(var1);
      this.upload();
   }

   public DynamicTexture(String var1, int var2, int var3, boolean var4) {
      super();
      this.pixels = new NativeImage(var2, var3, var4);
      this.createTexture(var1);
   }

   public DynamicTexture(Supplier<String> var1, int var2, int var3, boolean var4) {
      super();
      this.pixels = new NativeImage(var2, var3, var4);
      this.createTexture(var1);
   }

   private void createTexture(Supplier<String> var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      this.texture = var2.createTexture(var1, 5, TextureFormat.RGBA8, this.pixels.getWidth(), this.pixels.getHeight(), 1, 1);
      this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      this.textureView = var2.createTextureView(this.texture);
   }

   private void createTexture(String var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      this.texture = var2.createTexture(var1, 5, TextureFormat.RGBA8, this.pixels.getWidth(), this.pixels.getHeight(), 1, 1);
      this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
      this.textureView = var2.createTextureView(this.texture);
   }

   public void upload() {
      if (this.pixels != null && this.texture != null) {
         RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.texture, this.pixels);
      } else {
         LOGGER.warn("Trying to upload disposed texture {}", this.getTexture().getLabel());
      }

   }

   public @Nullable NativeImage getPixels() {
      return this.pixels;
   }

   public void setPixels(NativeImage var1) {
      if (this.pixels != null) {
         this.pixels.close();
      }

      this.pixels = var1;
   }

   public void close() {
      if (this.pixels != null) {
         this.pixels.close();
         this.pixels = null;
      }

      super.close();
   }

   public void dumpContents(Identifier var1, Path var2) throws IOException {
      if (this.pixels != null) {
         String var3 = var1.toDebugFileName() + ".png";
         Path var4 = var2.resolve(var3);
         this.pixels.writeToFile(var4);
      }

   }
}
