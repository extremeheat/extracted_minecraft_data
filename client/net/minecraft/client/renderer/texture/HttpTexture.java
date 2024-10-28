package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class HttpTexture extends SimpleTexture {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SKIN_WIDTH = 64;
   private static final int SKIN_HEIGHT = 64;
   private static final int LEGACY_SKIN_HEIGHT = 32;
   @Nullable
   private final File file;
   private final String urlString;
   private final boolean processLegacySkin;
   @Nullable
   private final Runnable onDownloaded;
   @Nullable
   private CompletableFuture<?> future;
   private boolean uploaded;

   public HttpTexture(@Nullable File var1, String var2, ResourceLocation var3, boolean var4, @Nullable Runnable var5) {
      super(var3);
      this.file = var1;
      this.urlString = var2;
      this.processLegacySkin = var4;
      this.onDownloaded = var5;
   }

   private void loadCallback(NativeImage var1) {
      if (this.onDownloaded != null) {
         this.onDownloaded.run();
      }

      Minecraft.getInstance().execute(() -> {
         this.uploaded = true;
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               this.upload(var1);
            });
         } else {
            this.upload(var1);
         }

      });
   }

   private void upload(NativeImage var1) {
      TextureUtil.prepareImage(this.getId(), var1.getWidth(), var1.getHeight());
      var1.upload(0, 0, 0, true);
   }

   public void load(ResourceManager var1) throws IOException {
      Minecraft.getInstance().execute(() -> {
         if (!this.uploaded) {
            try {
               super.load(var1);
            } catch (IOException var3) {
               LOGGER.warn("Failed to load texture: {}", this.location, var3);
            }

            this.uploaded = true;
         }

      });
      if (this.future == null) {
         NativeImage var2;
         if (this.file != null && this.file.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", this.file);
            FileInputStream var3 = new FileInputStream(this.file);
            var2 = this.load((InputStream)var3);
         } else {
            var2 = null;
         }

         if (var2 != null) {
            this.loadCallback(var2);
         } else {
            this.future = CompletableFuture.runAsync(() -> {
               HttpURLConnection var1 = null;
               LOGGER.debug("Downloading http texture from {} to {}", this.urlString, this.file);

               try {
                  var1 = (HttpURLConnection)(new URL(this.urlString)).openConnection(Minecraft.getInstance().getProxy());
                  var1.setDoInput(true);
                  var1.setDoOutput(false);
                  var1.connect();
                  if (var1.getResponseCode() / 100 == 2) {
                     Object var2;
                     if (this.file != null) {
                        FileUtils.copyInputStreamToFile(var1.getInputStream(), this.file);
                        var2 = new FileInputStream(this.file);
                     } else {
                        var2 = var1.getInputStream();
                     }

                     Minecraft.getInstance().execute(() -> {
                        NativeImage var2x = this.load(var2);
                        if (var2x != null) {
                           this.loadCallback(var2x);
                        }

                     });
                     return;
                  }
               } catch (Exception var6) {
                  LOGGER.error("Couldn't download http texture", var6);
                  return;
               } finally {
                  if (var1 != null) {
                     var1.disconnect();
                  }

               }

            }, Util.backgroundExecutor());
         }
      }
   }

   @Nullable
   private NativeImage load(InputStream var1) {
      NativeImage var2 = null;

      try {
         var2 = NativeImage.read(var1);
         if (this.processLegacySkin) {
            var2 = this.processLegacySkin(var2);
         }
      } catch (Exception var4) {
         LOGGER.warn("Error while loading the skin texture", var4);
      }

      return var2;
   }

   @Nullable
   private NativeImage processLegacySkin(NativeImage var1) {
      int var2 = var1.getHeight();
      int var3 = var1.getWidth();
      if (var3 == 64 && (var2 == 32 || var2 == 64)) {
         boolean var4 = var2 == 32;
         if (var4) {
            NativeImage var5 = new NativeImage(64, 64, true);
            var5.copyFrom(var1);
            var1.close();
            var1 = var5;
            var5.fillRect(0, 32, 64, 32, 0);
            var5.copyRect(4, 16, 16, 32, 4, 4, true, false);
            var5.copyRect(8, 16, 16, 32, 4, 4, true, false);
            var5.copyRect(0, 20, 24, 32, 4, 12, true, false);
            var5.copyRect(4, 20, 16, 32, 4, 12, true, false);
            var5.copyRect(8, 20, 8, 32, 4, 12, true, false);
            var5.copyRect(12, 20, 16, 32, 4, 12, true, false);
            var5.copyRect(44, 16, -8, 32, 4, 4, true, false);
            var5.copyRect(48, 16, -8, 32, 4, 4, true, false);
            var5.copyRect(40, 20, 0, 32, 4, 12, true, false);
            var5.copyRect(44, 20, -8, 32, 4, 12, true, false);
            var5.copyRect(48, 20, -16, 32, 4, 12, true, false);
            var5.copyRect(52, 20, -8, 32, 4, 12, true, false);
         }

         setNoAlpha(var1, 0, 0, 32, 16);
         if (var4) {
            doNotchTransparencyHack(var1, 32, 0, 64, 32);
         }

         setNoAlpha(var1, 0, 16, 64, 32);
         setNoAlpha(var1, 16, 48, 48, 64);
         return var1;
      } else {
         var1.close();
         LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", new Object[]{var3, var2, this.urlString});
         return null;
      }
   }

   private static void doNotchTransparencyHack(NativeImage var0, int var1, int var2, int var3, int var4) {
      int var5;
      int var6;
      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            int var7 = var0.getPixelRGBA(var5, var6);
            if ((var7 >> 24 & 255) < 128) {
               return;
            }
         }
      }

      for(var5 = var1; var5 < var3; ++var5) {
         for(var6 = var2; var6 < var4; ++var6) {
            var0.setPixelRGBA(var5, var6, var0.getPixelRGBA(var5, var6) & 16777215);
         }
      }

   }

   private static void setNoAlpha(NativeImage var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            var0.setPixelRGBA(var5, var6, var0.getPixelRGBA(var5, var6) | -16777216);
         }
      }

   }
}
