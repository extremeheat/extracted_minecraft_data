package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR;

   private HttpUtil() {
      super();
   }

   public static String buildQuery(Map<String, Object> var0) {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (var1.length() > 0) {
            var1.append('&');
         }

         try {
            var1.append(URLEncoder.encode((String)var3.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
         }

         if (var3.getValue() != null) {
            var1.append('=');

            try {
               var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException var5) {
               var5.printStackTrace();
            }
         }
      }

      return var1.toString();
   }

   public static String performPost(URL var0, Map<String, Object> var1, boolean var2, @Nullable Proxy var3) {
      return performPost(var0, buildQuery(var1), var2, var3);
   }

   private static String performPost(URL var0, String var1, boolean var2, @Nullable Proxy var3) {
      try {
         if (var3 == null) {
            var3 = Proxy.NO_PROXY;
         }

         HttpURLConnection var4 = (HttpURLConnection)var0.openConnection(var3);
         var4.setRequestMethod("POST");
         var4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var4.setRequestProperty("Content-Length", var1.getBytes().length.makeConcatWithConstants<invokedynamic>(var1.getBytes().length));
         var4.setRequestProperty("Content-Language", "en-US");
         var4.setUseCaches(false);
         var4.setDoInput(true);
         var4.setDoOutput(true);
         DataOutputStream var5 = new DataOutputStream(var4.getOutputStream());
         var5.writeBytes(var1);
         var5.flush();
         var5.close();
         BufferedReader var6 = new BufferedReader(new InputStreamReader(var4.getInputStream()));
         StringBuilder var8 = new StringBuilder();

         String var7;
         while((var7 = var6.readLine()) != null) {
            var8.append(var7);
            var8.append('\r');
         }

         var6.close();
         return var8.toString();
      } catch (Exception var9) {
         if (!var2) {
            LOGGER.error("Could not post to {}", var0, var9);
         }

         return "";
      }
   }

   public static CompletableFuture<?> downloadTo(File var0, String var1, Map<String, String> var2, int var3, @Nullable ProgressListener var4, Proxy var5) {
      return CompletableFuture.supplyAsync(() -> {
         HttpURLConnection var6 = null;
         InputStream var7 = null;
         DataOutputStream var8 = null;
         if (var4 != null) {
            var4.progressStart(new TranslatableComponent("resourcepack.downloading"));
            var4.progressStage(new TranslatableComponent("resourcepack.requesting"));
         }

         try {
            byte[] var9 = new byte[4096];
            URL var24 = new URL(var1);
            var6 = (HttpURLConnection)var24.openConnection(var5);
            var6.setInstanceFollowRedirects(true);
            float var11 = 0.0F;
            float var12 = (float)var2.entrySet().size();
            Iterator var13 = var2.entrySet().iterator();

            while(var13.hasNext()) {
               Entry var14 = (Entry)var13.next();
               var6.setRequestProperty((String)var14.getKey(), (String)var14.getValue());
               if (var4 != null) {
                  var4.progressStagePercentage((int)(++var11 / var12 * 100.0F));
               }
            }

            var7 = var6.getInputStream();
            var12 = (float)var6.getContentLength();
            int var25 = var6.getContentLength();
            if (var4 != null) {
               var4.progressStage(new TranslatableComponent("resourcepack.progress", new Object[]{String.format(Locale.ROOT, "%.2f", var12 / 1000.0F / 1000.0F)}));
            }

            if (var0.exists()) {
               long var26 = var0.length();
               if (var26 == (long)var25) {
                  if (var4 != null) {
                     var4.stop();
                  }

                  Object var16 = null;
                  return var16;
               }

               LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", var0, var25, var26);
               FileUtils.deleteQuietly(var0);
            } else if (var0.getParentFile() != null) {
               var0.getParentFile().mkdirs();
            }

            var8 = new DataOutputStream(new FileOutputStream(var0));
            if (var3 > 0 && var12 > (float)var3) {
               if (var4 != null) {
                  var4.stop();
               }

               throw new IOException("Filesize is bigger than maximum allowed (file is " + var11 + ", limit is " + var3 + ")");
            } else {
               int var27;
               while((var27 = var7.read(var9)) >= 0) {
                  var11 += (float)var27;
                  if (var4 != null) {
                     var4.progressStagePercentage((int)(var11 / var12 * 100.0F));
                  }

                  if (var3 > 0 && var11 > (float)var3) {
                     if (var4 != null) {
                        var4.stop();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + var11 + ", limit was " + var3 + ")");
                  }

                  if (Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if (var4 != null) {
                        var4.stop();
                     }

                     Object var15 = null;
                     return var15;
                  }

                  var8.write(var9, 0, var27);
               }

               if (var4 != null) {
                  var4.stop();
               }

               return null;
            }
         } catch (Throwable var22) {
            var22.printStackTrace();
            if (var6 != null) {
               InputStream var10 = var6.getErrorStream();

               try {
                  LOGGER.error(IOUtils.toString(var10));
               } catch (IOException var21) {
                  var21.printStackTrace();
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
