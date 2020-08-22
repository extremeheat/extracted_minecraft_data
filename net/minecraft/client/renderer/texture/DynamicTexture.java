package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.server.packs.resources.ResourceManager;

public class DynamicTexture extends AbstractTexture implements AutoCloseable {
   private NativeImage pixels;

   public DynamicTexture(NativeImage var1) {
      this.pixels = var1;
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
            this.upload();
         });
      } else {
         TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
         this.upload();
      }

   }

   public DynamicTexture(int var1, int var2, boolean var3) {
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      this.pixels = new NativeImage(var1, var2, var3);
      TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
   }

   public void load(ResourceManager var1) throws IOException {
   }

   public void upload() {
      this.bind();
      this.pixels.upload(0, 0, 0, false);
   }

   @Nullable
   public NativeImage getPixels() {
      return this.pixels;
   }

   public void setPixels(NativeImage var1) throws Exception {
      this.pixels.close();
      this.pixels = var1;
   }

   public void close() {
      this.pixels.close();
      this.releaseId();
      this.pixels = null;
   }
}
