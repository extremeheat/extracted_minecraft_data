package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Downloader %d").build()));
   private static final AtomicInteger field_151228_a = new AtomicInteger(0);
   private static final Logger field_151227_b = LogManager.getLogger();

   public static String func_76179_a(Map<String, Object> var0) {
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

   public static String func_151226_a(URL var0, Map<String, Object> var1, boolean var2) {
      return func_151225_a(var0, func_76179_a(var1), var2);
   }

   private static String func_151225_a(URL var0, String var1, boolean var2) {
      try {
         Proxy var3 = MinecraftServer.func_71276_C() == null ? null : MinecraftServer.func_71276_C().func_110454_ao();
         if (var3 == null) {
            var3 = Proxy.NO_PROXY;
         }

         HttpURLConnection var4 = (HttpURLConnection)var0.openConnection(var3);
         var4.setRequestMethod("POST");
         var4.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         var4.setRequestProperty("Content-Length", "" + var1.getBytes().length);
         var4.setRequestProperty("Content-Language", "en-US");
         var4.setUseCaches(false);
         var4.setDoInput(true);
         var4.setDoOutput(true);
         DataOutputStream var5 = new DataOutputStream(var4.getOutputStream());
         var5.writeBytes(var1);
         var5.flush();
         var5.close();
         BufferedReader var6 = new BufferedReader(new InputStreamReader(var4.getInputStream()));
         StringBuffer var8 = new StringBuffer();

         String var7;
         while((var7 = var6.readLine()) != null) {
            var8.append(var7);
            var8.append('\r');
         }

         var6.close();
         return var8.toString();
      } catch (Exception var9) {
         if (!var2) {
            field_151227_b.error("Could not post to " + var0, var9);
         }

         return "";
      }
   }

   public static ListenableFuture<Object> func_180192_a(final File var0, final String var1, final Map<String, String> var2, final int var3, final IProgressUpdate var4, final Proxy var5) {
      ListenableFuture var6 = field_180193_a.submit(new Runnable() {
         public void run() {
            HttpURLConnection var1x = null;
            InputStream var2x = null;
            DataOutputStream var3x = null;
            if (var4 != null) {
               var4.func_73721_b("Downloading Resource Pack");
               var4.func_73719_c("Making Request...");
            }

            try {
               try {
                  byte[] var4x = new byte[4096];
                  URL var18 = new URL(var1);
                  var1x = (HttpURLConnection)var18.openConnection(var5);
                  float var6 = 0.0F;
                  float var7 = (float)var2.entrySet().size();
                  Iterator var8 = var2.entrySet().iterator();

                  while(var8.hasNext()) {
                     Entry var9 = (Entry)var8.next();
                     var1x.setRequestProperty((String)var9.getKey(), (String)var9.getValue());
                     if (var4 != null) {
                        var4.func_73718_a((int)(++var6 / var7 * 100.0F));
                     }
                  }

                  var2x = var1x.getInputStream();
                  var7 = (float)var1x.getContentLength();
                  int var19 = var1x.getContentLength();
                  if (var4 != null) {
                     var4.func_73719_c(String.format("Downloading file (%.2f MB)...", var7 / 1000.0F / 1000.0F));
                  }

                  if (var0.exists()) {
                     long var20 = var0.length();
                     if (var20 == (long)var19) {
                        if (var4 != null) {
                           var4.func_146586_a();
                        }

                        return;
                     }

                     HttpUtil.field_151227_b.warn("Deleting " + var0 + " as it does not match what we currently have (" + var19 + " vs our " + var20 + ").");
                     FileUtils.deleteQuietly(var0);
                  } else if (var0.getParentFile() != null) {
                     var0.getParentFile().mkdirs();
                  }

                  var3x = new DataOutputStream(new FileOutputStream(var0));
                  if (var3 > 0 && var7 > (float)var3) {
                     if (var4 != null) {
                        var4.func_146586_a();
                     }

                     throw new IOException("Filesize is bigger than maximum allowed (file is " + var6 + ", limit is " + var3 + ")");
                  }

                  boolean var21 = false;

                  int var22;
                  while((var22 = var2x.read(var4x)) >= 0) {
                     var6 += (float)var22;
                     if (var4 != null) {
                        var4.func_73718_a((int)(var6 / var7 * 100.0F));
                     }

                     if (var3 > 0 && var6 > (float)var3) {
                        if (var4 != null) {
                           var4.func_146586_a();
                        }

                        throw new IOException("Filesize was bigger than maximum allowed (got >= " + var6 + ", limit was " + var3 + ")");
                     }

                     if (Thread.interrupted()) {
                        HttpUtil.field_151227_b.error("INTERRUPTED");
                        if (var4 != null) {
                           var4.func_146586_a();
                        }

                        return;
                     }

                     var3x.write(var4x, 0, var22);
                  }

                  if (var4 != null) {
                     var4.func_146586_a();
                     return;
                  }
               } catch (Throwable var16) {
                  var16.printStackTrace();
                  if (var1x != null) {
                     InputStream var5x = var1x.getErrorStream();

                     try {
                        HttpUtil.field_151227_b.error(IOUtils.toString(var5x));
                     } catch (IOException var15) {
                        var15.printStackTrace();
                     }
                  }

                  if (var4 != null) {
                     var4.func_146586_a();
                     return;
                  }
               }

            } finally {
               IOUtils.closeQuietly(var2x);
               IOUtils.closeQuietly(var3x);
            }
         }
      });
      return var6;
   }

   public static int func_76181_a() throws IOException {
      ServerSocket var0 = null;
      boolean var1 = true;

      int var10;
      try {
         var0 = new ServerSocket(0);
         var10 = var0.getLocalPort();
      } finally {
         try {
            if (var0 != null) {
               var0.close();
            }
         } catch (IOException var8) {
         }

      }

      return var10;
   }

   public static String func_152755_a(URL var0) throws IOException {
      HttpURLConnection var1 = (HttpURLConnection)var0.openConnection();
      var1.setRequestMethod("GET");
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream()));
      StringBuilder var4 = new StringBuilder();

      String var3;
      while((var3 = var2.readLine()) != null) {
         var4.append(var3);
         var4.append('\r');
      }

      var2.close();
      return var4.toString();
   }
}
