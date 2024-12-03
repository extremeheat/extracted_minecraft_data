package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.slf4j.Logger;

public class SkinTextureDownloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SKIN_WIDTH = 64;
   private static final int SKIN_HEIGHT = 64;
   private static final int LEGACY_SKIN_HEIGHT = 32;

   public SkinTextureDownloader() {
      super();
   }

   public static CompletableFuture<ResourceLocation> downloadAndRegisterSkin(ResourceLocation var0, Path var1, String var2, boolean var3) {
      return CompletableFuture.supplyAsync(() -> {
         NativeImage var3x;
         try {
            var3x = downloadSkin(var1, var2);
         } catch (IOException var5) {
            throw new UncheckedIOException(var5);
         }

         return var3 ? processLegacySkin(var3x, var2) : var3x;
      }, Util.nonCriticalIoPool().forName("downloadTexture")).thenCompose((var1x) -> registerTextureInManager(var0, var1x));
   }

   private static NativeImage downloadSkin(Path var0, String var1) throws IOException {
      if (Files.isRegularFile(var0, new LinkOption[0])) {
         LOGGER.debug("Loading HTTP texture from local cache ({})", var0);
         InputStream var16 = Files.newInputStream(var0);

         NativeImage var17;
         try {
            var17 = NativeImage.read(var16);
         } catch (Throwable var14) {
            if (var16 != null) {
               try {
                  var16.close();
               } catch (Throwable var12) {
                  var14.addSuppressed(var12);
               }
            }

            throw var14;
         }

         if (var16 != null) {
            var16.close();
         }

         return var17;
      } else {
         HttpURLConnection var2 = null;
         LOGGER.debug("Downloading HTTP texture from {} to {}", var1, var0);
         URI var3 = URI.create(var1);

         NativeImage var6;
         try {
            var2 = (HttpURLConnection)var3.toURL().openConnection(Minecraft.getInstance().getProxy());
            var2.setDoInput(true);
            var2.setDoOutput(false);
            var2.connect();
            int var4 = var2.getResponseCode();
            if (var4 / 100 != 2) {
               String var10002 = String.valueOf(var3);
               throw new IOException("Failed to open " + var10002 + ", HTTP error code: " + var4);
            }

            byte[] var5 = var2.getInputStream().readAllBytes();

            try {
               FileUtil.createDirectoriesSafe(var0.getParent());
               Files.write(var0, var5, new OpenOption[0]);
            } catch (IOException var13) {
               LOGGER.warn("Failed to cache texture {} in {}", var1, var0);
            }

            var6 = NativeImage.read(var5);
         } finally {
            if (var2 != null) {
               var2.disconnect();
            }

         }

         return var6;
      }
   }

   private static CompletableFuture<ResourceLocation> registerTextureInManager(ResourceLocation var0, NativeImage var1) {
      Minecraft var2 = Minecraft.getInstance();
      return CompletableFuture.supplyAsync(() -> {
         var2.getTextureManager().register(var0, new DynamicTexture(var1));
         return var0;
      }, var2);
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
