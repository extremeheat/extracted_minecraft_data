package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

public class FileUpload {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_RETRIES = 5;
   private static final String UPLOAD_PATH = "/upload";
   private final File file;
   private final long realmId;
   private final int slotId;
   private final UploadInfo uploadInfo;
   private final String sessionId;
   private final String username;
   private final String clientVersion;
   private final String worldVersion;
   private final UploadStatus uploadStatus;
   private final AtomicBoolean cancelled = new AtomicBoolean(false);
   @Nullable
   private CompletableFuture<UploadResult> uploadTask;
   private final RequestConfig requestConfig;

   public FileUpload(File var1, long var2, int var4, UploadInfo var5, User var6, String var7, String var8, UploadStatus var9) {
      super();
      this.requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();
      this.file = var1;
      this.realmId = var2;
      this.slotId = var4;
      this.uploadInfo = var5;
      this.sessionId = var6.getSessionId();
      this.username = var6.getName();
      this.clientVersion = var7;
      this.worldVersion = var8;
      this.uploadStatus = var9;
   }

   public void upload(Consumer<UploadResult> var1) {
      if (this.uploadTask == null) {
         this.uploadTask = CompletableFuture.supplyAsync(() -> {
            return this.requestUpload(0);
         });
         this.uploadTask.thenAccept(var1);
      }
   }

   public void cancel() {
      this.cancelled.set(true);
      if (this.uploadTask != null) {
         this.uploadTask.cancel(false);
         this.uploadTask = null;
      }

   }

   private UploadResult requestUpload(int var1) {
      UploadResult.Builder var2 = new UploadResult.Builder();
      if (this.cancelled.get()) {
         return var2.build();
      } else {
         this.uploadStatus.totalBytes = this.file.length();
         HttpPost var3 = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.realmId + "/" + this.slotId));
         CloseableHttpClient var4 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();

         try {
            this.setupRequest(var3);
            CloseableHttpResponse var5 = var4.execute(var3);
            long var6 = this.getRetryDelaySeconds(var5);
            if (this.shouldRetry(var6, var1)) {
               UploadResult var8 = this.retryUploadAfter(var6, var1);
               return var8;
            }

            this.handleResponse(var5, var2);
         } catch (Exception var12) {
            if (!this.cancelled.get()) {
               LOGGER.error("Caught exception while uploading: ", var12);
            }
         } finally {
            this.cleanup(var3, var4);
         }

         return var2.build();
      }
   }

   private void cleanup(HttpPost var1, @Nullable CloseableHttpClient var2) {
      var1.releaseConnection();
      if (var2 != null) {
         try {
            var2.close();
         } catch (IOException var4) {
            LOGGER.error("Failed to close Realms upload client");
         }
      }

   }

   private void setupRequest(HttpPost var1) throws FileNotFoundException {
      String var10002 = this.sessionId;
      var1.setHeader("Cookie", "sid=" + var10002 + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion + ";worldVersion=" + this.worldVersion);
      CustomInputStreamEntity var2 = new CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
      var2.setContentType("application/octet-stream");
      var1.setEntity(var2);
   }

   private void handleResponse(HttpResponse var1, UploadResult.Builder var2) throws IOException {
      int var3 = var1.getStatusLine().getStatusCode();
      if (var3 == 401) {
         LOGGER.debug("Realms server returned 401: {}", var1.getFirstHeader("WWW-Authenticate"));
      }

      var2.withStatusCode(var3);
      if (var1.getEntity() != null) {
         String var4 = EntityUtils.toString(var1.getEntity(), "UTF-8");
         if (var4 != null) {
            try {
               JsonParser var5 = new JsonParser();
               JsonElement var6 = var5.parse(var4).getAsJsonObject().get("errorMsg");
               Optional var7 = Optional.ofNullable(var6).map(JsonElement::getAsString);
               var2.withErrorMessage((String)var7.orElse((Object)null));
            } catch (Exception var8) {
            }
         }
      }

   }

   private boolean shouldRetry(long var1, int var3) {
      return var1 > 0L && var3 + 1 < 5;
   }

   private UploadResult retryUploadAfter(long var1, int var3) throws InterruptedException {
      Thread.sleep(Duration.ofSeconds(var1).toMillis());
      return this.requestUpload(var3 + 1);
   }

   private long getRetryDelaySeconds(HttpResponse var1) {
      return (Long)Optional.ofNullable(var1.getFirstHeader("Retry-After")).map(NameValuePair::getValue).map(Long::valueOf).orElse(0L);
   }

   public boolean isFinished() {
      return this.uploadTask.isDone() || this.uploadTask.isCancelled();
   }

   static class CustomInputStreamEntity extends InputStreamEntity {
      private final long length;
      private final InputStream content;
      private final UploadStatus uploadStatus;

      public CustomInputStreamEntity(InputStream var1, long var2, UploadStatus var4) {
         super(var1);
         this.content = var1;
         this.length = var2;
         this.uploadStatus = var4;
      }

      public void writeTo(OutputStream var1) throws IOException {
         Args.notNull(var1, "Output stream");
         InputStream var2 = this.content;

         try {
            byte[] var3 = new byte[4096];
            UploadStatus var10000;
            int var4;
            if (this.length < 0L) {
               while((var4 = var2.read(var3)) != -1) {
                  var1.write(var3, 0, var4);
                  var10000 = this.uploadStatus;
                  var10000.bytesWritten += (long)var4;
               }
            } else {
               long var5 = this.length;

               while(var5 > 0L) {
                  var4 = var2.read(var3, 0, (int)Math.min(4096L, var5));
                  if (var4 == -1) {
                     break;
                  }

                  var1.write(var3, 0, var4);
                  var10000 = this.uploadStatus;
                  var10000.bytesWritten += (long)var4;
                  var5 -= (long)var4;
                  var1.flush();
               }
            }
         } finally {
            var2.close();
         }

      }
   }
}
