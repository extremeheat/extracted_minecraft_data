package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(
         new ThreadFactoryBuilder()
            .setDaemon(true)
            .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER))
            .setNameFormat("Downloader %d")
            .build()
      )
   );

   private HttpUtil() {
      super();
   }

   public static CompletableFuture<?> downloadTo(File var0, URL var1, Map<String, String> var2, int var3, @Nullable ProgressListener var4, Proxy var5) {
      return CompletableFuture.supplyAsync(() -> {
         HttpURLConnection var6 = null;
         InputStream var7 = null;
         DataOutputStream var8 = null;
         if (var4 != null) {
            var4.progressStart(Component.translatable("resourcepack.downloading"));
            var4.progressStage(Component.translatable("resourcepack.requesting"));
         }

         try {
            byte[] var9 = new byte[4096];
            var6 = (HttpURLConnection)var1.openConnection(var5);
            var6.setInstanceFollowRedirects(true);
            float var23 = 0.0F;
            float var11 = (float)var2.entrySet().size();

            for(Entry var13 : var2.entrySet()) {
               var6.setRequestProperty((String)var13.getKey(), (String)var13.getValue());
               if (var4 != null) {
                  var4.progressStagePercentage((int)(++var23 / var11 * 100.0F));
               }
            }

            var7 = var6.getInputStream();
            var11 = (float)var6.getContentLength();
            int var25 = var6.getContentLength();
            if (var4 != null) {
               var4.progressStage(Component.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", var11 / 1000.0F / 1000.0F)));
            }

            if (var0.exists()) {
               long var26 = var0.length();
               if (var26 == (long)var25) {
                  if (var4 != null) {
                     var4.stop();
                  }

                  return null;
               }

               LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", new Object[]{var0, var25, var26});
               FileUtils.deleteQuietly(var0);
            } else if (var0.getParentFile() != null) {
               var0.getParentFile().mkdirs();
            }

            var8 = new DataOutputStream(new FileOutputStream(var0));
            if (var3 > 0 && var11 > (float)var3) {
               if (var4 != null) {
                  var4.stop();
               }

               throw new IOException("Filesize is bigger than maximum allowed (file is " + var23 + ", limit is " + var3 + ")");
            } else {
               int var27;
               while((var27 = var7.read(var9)) >= 0) {
                  var23 += (float)var27;
                  if (var4 != null) {
                     var4.progressStagePercentage((int)(var23 / var11 * 100.0F));
                  }

                  if (var3 > 0 && var23 > (float)var3) {
                     if (var4 != null) {
                        var4.stop();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + var23 + ", limit was " + var3 + ")");
                  }

                  if (Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if (var4 != null) {
                        var4.stop();
                     }

                     return null;
                  }

                  var8.write(var9, 0, var27);
               }

               if (var4 != null) {
                  var4.stop();
               }

               return null;
            }
         } catch (Throwable var21) {
            LOGGER.error("Failed to download file", var21);
            if (var6 != null) {
               InputStream var10 = var6.getErrorStream();

               try {
                  LOGGER.error("HTTP response error: {}", IOUtils.toString(var10, StandardCharsets.UTF_8));
               } catch (IOException var20) {
                  LOGGER.error("Failed to read response from server");
               }
            }

            if (var4 != null) {
               var4.stop();
            }

            return null;
         } finally {
            IOUtils.closeQuietly(var7);
            IOUtils.closeQuietly(var8);
         }
      }, DOWNLOAD_EXECUTOR);
   }

   public static int getAvailablePort() {
      try {
         int var1;
         try (ServerSocket var0 = new ServerSocket(0)) {
            var1 = var0.getLocalPort();
         }

         return var1;
      } catch (IOException var5) {
         return 25564;
      }
   }

   public static boolean isPortAvailable(int var0) {
      if (var0 >= 0 && var0 <= 65535) {
         try {
            boolean var2;
            try (ServerSocket var1 = new ServerSocket(var0)) {
               var2 = var1.getLocalPort() == var0;
            }

            return var2;
         } catch (IOException var6) {
            return false;
         }
      } else {
         return false;
      }
   }
}
