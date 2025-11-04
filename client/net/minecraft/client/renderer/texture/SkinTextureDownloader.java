package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class SkinTextureDownloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SKIN_WIDTH = 64;
   private static final int SKIN_HEIGHT = 64;
   private static final int LEGACY_SKIN_HEIGHT = 32;
   private final Proxy proxy;
   private final TextureManager textureManager;
   private final Executor mainThreadExecutor;

   public SkinTextureDownloader(Proxy var1, TextureManager var2, Executor var3) {
      super();
      this.proxy = var1;
      this.textureManager = var2;
      this.mainThreadExecutor = var3;
   }

   public CompletableFuture<ClientAsset.Texture> downloadAndRegisterSkin(Identifier var1, Path var2, String var3, boolean var4) {
      ClientAsset.DownloadedTexture var5 = new ClientAsset.DownloadedTexture(var1, var3);
      return CompletableFuture.supplyAsync(() -> {
         NativeImage var4x;
         try {
            var4x = this.downloadSkin(var2, var5.url());
         } catch (IOException var6) {
            throw new UncheckedIOException(var6);
         }

         return var4 ? processLegacySkin(var4x, var5.url()) : var4x;
      }, Util.nonCriticalIoPool().forName("downloadTexture")).thenCompose((var2x) -> this.registerTextureInManager(var5, var2x));
   }

   private NativeImage downloadSkin(Path var1, String var2) throws IOException {
      if (Files.isRegularFile(var1, new LinkOption[0])) {
         LOGGER.debug("Loading HTTP texture from local cache ({})", var1);
         InputStream var17 = Files.newInputStream(var1);

         NativeImage var18;
         try {
            var18 = NativeImage.read(var17);
         } catch (Throwable var15) {
            if (var17 != null) {
               try {
                  var17.close();
               } catch (Throwable var13) {
                  var15.addSuppressed(var13);
               }
            }

            throw var15;
         }

         if (var17 != null) {
            var17.close();
         }

         return var18;
      } else {
         HttpURLConnection var3 = null;
         LOGGER.debug("Downloading HTTP texture from {} to {}", var2, var1);
         URI var4 = URI.create(var2);

         NativeImage var7;
         try {
            var3 = (HttpURLConnection)var4.toURL().openConnection(this.proxy);
            var3.setDoInput(true);
            var3.setDoOutput(false);
            var3.connect();
            int var5 = var3.getResponseCode();
            if (var5 / 100 != 2) {
               String var10002 = String.valueOf(var4);
               throw new IOException("Failed to open " + var10002 + ", HTTP error code: " + var5);
            }

            byte[] var6 = var3.getInputStream().readAllBytes();

            try {
               FileUtil.createDirectoriesSafe(var1.getParent());
               Files.write(var1, var6, new OpenOption[0]);
            } catch (IOException var14) {
               LOGGER.warn("Failed to cache texture {} in {}", var2, var1);
            }

            var7 = NativeImage.read(var6);
         } finally {
            if (var3 != null) {
               var3.disconnect();
            }

         }

         return var7;
      }
   }

   private CompletableFuture<ClientAsset.Texture> registerTextureInManager(ClientAsset.Texture var1, NativeImage var2) {
      return CompletableFuture.supplyAsync(() -> {
         Identifier var10002 = var1.texturePath();
         Objects.requireNonNull(var10002);
         DynamicTexture var3 = new DynamicTexture(var10002::toString, var2);
         this.textureManager.register(var1.texturePath(), var3);
         return var1;
      }, this.mainThreadExecutor);
   }

   private static NativeImage processLegacySkin(NativeImage var0, String var1) {
      int var2 = var0.getHeight();
      int var3 = var0.getWidth();
      if (var3 == 64 && (var2 == 32 || var2 == 64)) {
         boolean var4 = var2 == 32;
         if (var4) {
            NativeImage var5 = new NativeImage(64, 64, true);
            var5.copyFrom(var0);
            var0.close();
            var0 = var5;
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

         setNoAlpha(var0, 0, 0, 32, 16);
         if (var4) {
            doNotchTransparencyHack(var0, 32, 0, 64, 32);
         }

         setNoAlpha(var0, 0, 16, 64, 32);
         setNoAlpha(var0, 16, 48, 48, 64);
         return var0;
      } else {
         var0.close();
         throw new IllegalStateException("Discarding incorrectly sized (" + var3 + "x" + var2 + ") skin texture from " + var1);
      }
   }

   private static void doNotchTransparencyHack(NativeImage var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            int var7 = var0.getPixel(var5, var6);
            if (ARGB.alpha(var7) < 128) {
               return;
            }
         }
      }

      for(int var8 = var1; var8 < var3; ++var8) {
         for(int var9 = var2; var9 < var4; ++var9) {
            var0.setPixel(var8, var9, var0.getPixel(var8, var9) & 16777215);
         }
      }

   }

   private static void setNoAlpha(NativeImage var0, int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var3; ++var5) {
         for(int var6 = var2; var6 < var4; ++var6) {
            var0.setPixel(var5, var6, ARGB.opaque(var0.getPixel(var5, var6)));
         }
      }

   }
}
