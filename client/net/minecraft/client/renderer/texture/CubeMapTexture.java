package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.TextureFormat;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class CubeMapTexture extends ReloadableTexture {
   private static final String[] SUFFIXES = new String[]{"_1.png", "_3.png", "_5.png", "_4.png", "_0.png", "_2.png"};

   public CubeMapTexture(final Identifier resourceId) {
      super(resourceId);
   }

   public TextureContents loadContents(final ResourceManager resourceManager) throws IOException {
      Identifier location = this.resourceId();
      TextureContents first = TextureContents.load(resourceManager, location.withSuffix(SUFFIXES[0]));

      TextureContents var15;
      try {
         int width = first.image().getWidth();
         int height = first.image().getHeight();
         NativeImage stackedImage = new NativeImage(width, height * 6, false);
         first.image().copyRect(stackedImage, 0, 0, 0, 0, width, height, false, true);

         for(int i = 1; i < 6; ++i) {
            TextureContents part = TextureContents.load(resourceManager, location.withSuffix(SUFFIXES[i]));

            try {
               if (part.image().getWidth() != width || part.image().getHeight() != height) {
                  String var10002 = String.valueOf(location);
                  throw new IOException("Image dimensions of cubemap '" + var10002 + "' sides do not match: part 0 is " + width + "x" + height + ", but part " + i + " is " + part.image().getWidth() + "x" + part.image().getHeight());
               }

               part.image().copyRect(stackedImage, 0, 0, 0, i * height, width, height, false, true);
            } catch (Throwable var13) {
               if (part != null) {
                  try {
                     part.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (part != null) {
               part.close();
            }
         }

         var15 = new TextureContents(stackedImage, new TextureMetadataSection(true, false, MipmapStrategy.MEAN, 0.0F));
      } catch (Throwable var14) {
         if (first != null) {
            try {
               first.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }
         }

         throw var14;
      }

      if (first != null) {
         first.close();
      }

      return var15;
   }

   protected void doLoad(final NativeImage image) {
      GpuDevice device = RenderSystem.getDevice();
      int width = image.getWidth();
      int height = image.getHeight() / 6;
      this.close();
      Identifier var10002 = this.resourceId();
      Objects.requireNonNull(var10002);
      this.texture = device.createTexture(var10002::toString, 21, TextureFormat.RGBA8, width, height, 6, 1);
      this.textureView = device.createTextureView(this.texture);

      for(int i = 0; i < 6; ++i) {
         device.createCommandEncoder().writeToTexture(this.texture, image, 0, i, 0, 0, width, height, 0, height * i);
      }

   }
}
