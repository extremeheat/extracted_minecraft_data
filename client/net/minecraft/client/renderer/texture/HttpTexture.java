package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.HttpTextureProcessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpTexture extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   @Nullable
   private final File file;
   private final String urlString;
   @Nullable
   private final HttpTextureProcessor processor;
   @Nullable
   private Thread thread;
   private volatile boolean uploaded;

   public HttpTexture(@Nullable File var1, String var2, ResourceLocation var3, @Nullable HttpTextureProcessor var4) {
      super(var3);
      this.file = var1;
      this.urlString = var2;
      this.processor = var4;
   }

   private void uploadImage(NativeImage var1) {
      TextureUtil.prepareImage(this.getId(), var1.getWidth(), var1.getHeight());
      var1.upload(0, 0, 0, false);
   }

   public void loadCallback(NativeImage var1) {
      if (this.processor != null) {
         this.processor.onTextureDownloaded();
      }

      synchronized(this) {
         this.uploadImage(var1);
         this.uploaded = true;
      }
   }

   public void load(ResourceManager var1) throws IOException {
      if (!this.uploaded) {
         synchronized(this) {
            super.load(var1);
            this.uploaded = true;
         }
      }

      if (this.thread == null) {
         if (this.file != null && this.file.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", this.file);
            NativeImage var2 = null;

            try {
               var2 = NativeImage.read((InputStream)(new FileInputStream(this.file)));
               if (this.processor != null) {
                  var2 = this.processor.process(var2);
               }

               this.loadCallback(var2);
            } catch (IOException var8) {
               LOGGER.error("Couldn't load skin {}", this.file, var8);
               this.startDownloadThread();
            } finally {
               if (var2 != null) {
                  var2.close();
               }

            }
         } else {
            this.startDownloadThread();
         }
      }

   }

   protected void startDownloadThread() {
      this.thread = new Thread("Texture Downloader #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            HttpURLConnection var1 = null;
            HttpTexture.LOGGER.debug("Downloading http texture from {} to {}", HttpTexture.this.urlString, HttpTexture.this.file);

            try {
               var1 = (HttpURLConnection)(new URL(HttpTexture.this.urlString)).openConnection(Minecraft.getInstance().getProxy());
               var1.setDoInput(true);
               var1.setDoOutput(false);
               var1.connect();
               if (var1.getResponseCode() / 100 != 2) {
                  return;
               }

               Object var2;
               if (HttpTexture.this.file != null) {
                  FileUtils.copyInputStreamToFile(var1.getInputStream(), HttpTexture.this.file);
                  var2 = new FileInputStream(HttpTexture.this.file);
               } else {
                  var2 = var1.getInputStream();
               }

               Minecraft.getInstance().execute(() -> {
                  NativeImage var2x = null;

                  try {
                     var2x = NativeImage.read(var2);
                     if (HttpTexture.this.processor != null) {
                        var2x = HttpTexture.this.processor.process(var2x);
                     }

                     HttpTexture.this.loadCallback(var2x);
                  } catch (IOException var7) {
                     HttpTexture.LOGGER.warn("Error while loading the skin texture", var7);
                  } finally {
                     if (var2x != null) {
                        var2x.close();
                     }

                     IOUtils.closeQuietly(var2);
                  }

               });
            } catch (Exception var6) {
               HttpTexture.LOGGER.error("Couldn't download http texture", var6);
            } finally {
               if (var1 != null) {
                  var1.disconnect();
               }

            }

         }
      };
      this.thread.setDaemon(true);
      this.thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.thread.start();
   }
}
