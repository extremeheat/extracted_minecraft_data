package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class DynamicTexture extends AbstractTexture implements Dumpable {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public void dumpContents(ResourceLocation var1, Path var2) throws IOException {
      if (this.pixels != null) {
         String var3 = var1.toDebugFileName() + ".png";
         Path var4 = var2.resolve(var3);
         this.pixels.writeToFile(var4);
      }

   }
}
