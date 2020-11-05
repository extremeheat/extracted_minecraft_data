package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DynamicTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   private NativeImage pixels;

   public DynamicTexture(NativeImage var1) {
      super();
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
      super();
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      this.pixels = new NativeImage(var1, var2, var3);
      TextureUtil.prepareImage(this.getId(), this.pixels.getWidth(), this.pixels.getHeight());
   }

   public void load(ResourceManager var1) {
   }

   public void upload() {
      if (this.pixels != null) {
         this.bind();
         this.pixels.upload(0, 0, 0, false);
      } else {
         LOGGER.warn("Trying to upload disposed texture {}", this.getId());
      }

   }

   @Nullable
   public NativeImage getPixels() {
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
         this.releaseId();
         this.pixels = null;
      }

   }
}
