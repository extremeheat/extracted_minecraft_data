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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR;

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
            try {
               byte[] var9 = new byte[4096];
               var6 = (HttpURLConnection)var1.openConnection(var5);
               var6.setInstanceFollowRedirects(true);
               float var23 = 0.0F;
               float var11 = (float)var2.entrySet().size();
               Iterator var12 = var2.entrySet().iterator();

               while(var12.hasNext()) {
                  Map.Entry var13 = (Map.Entry)var12.next();
                  var6.setRequestProperty((String)var13.getKey(), (String)var13.getValue());
                  if (var4 != null) {
                     var4.progressStagePercentage((int)(++var23 / var11 * 100.0F));
                  }
               }

               var7 = var6.getInputStream();
               var11 = (float)var6.getContentLength();
               int var24 = var6.getContentLength();
               if (var4 != null) {
                  var4.progressStage(Component.translatable("resourcepack.progress", String.format(Locale.ROOT, "%.2f", var11 / 1000.0F / 1000.0F)));
               }

               if (var0.exists()) {
                  long var25 = var0.length();
                  if (var25 == (long)var24) {
                     if (var4 != null) {
                        var4.stop();
                     }

                     Object var15 = null;
                     return var15;
                  }

                  LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", new Object[]{var0, var24, var25});
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
               }

               int var26;
               while((var26 = var7.read(var9)) >= 0) {
                  var23 += (float)var26;
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

                     Object var14 = null;
                     return var14;
                  }

                  var8.write(var9, 0, var26);
               }

               if (var4 != null) {
                  var4.stop();
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
                  return null;
               }
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
         ServerSocket var0 = new ServerSocket(0);

         int var1;
         try {
            var1 = var0.getLocalPort();
         } catch (Throwable var4) {
            try {
               var0.close();
            } catch (Throwable var3) {
               var4.addSuppressed(var3);
            }

            throw var4;
         }

         var0.close();
         return var1;
      } catch (IOException var5) {
         return 25564;
      }
   }

   static {
      DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));
   }
}
