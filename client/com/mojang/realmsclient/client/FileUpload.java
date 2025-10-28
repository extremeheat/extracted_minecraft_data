package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.User;
import net.minecraft.util.LenientJsonParser;
import org.apache.commons.io.input.CountingInputStream;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class FileUpload implements AutoCloseable {
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
   private final HttpClient client;

   public FileUpload(File var1, long var2, int var4, UploadInfo var5, User var6, String var7, String var8, UploadStatus var9) {
      super();
      this.file = var1;
      this.realmId = var2;
      this.slotId = var4;
      this.uploadInfo = var5;
      this.sessionId = var6.getSessionId();
      this.username = var6.getName();
      this.clientVersion = var7;
      this.worldVersion = var8;
      this.uploadStatus = var9;
      this.client = HttpClient.newBuilder().executor(Util.nonCriticalIoPool()).connectTimeout(Duration.ofSeconds(15L)).build();
   }

   public void close() {
      this.client.close();
   }

   public CompletableFuture<UploadResult> startUpload() {
      long var1 = this.file.length();
      this.uploadStatus.setTotalBytes(var1);
      return this.requestUpload(0, var1);
   }

   private CompletableFuture<UploadResult> requestUpload(int var1, long var2) {
      HttpRequest.BodyPublisher var4 = inputStreamPublisherWithSize(() -> {
         try {
            return new UploadCountingInputStream(new FileInputStream(this.file), this.uploadStatus);
         } catch (IOException var2) {
            LOGGER.warn("Failed to open file {}", this.file, var2);
            return null;
         }
      }, var2);
      HttpRequest var5 = HttpRequest.newBuilder(this.uploadInfo.uploadEndpoint().resolve("/upload/" + this.realmId + "/" + this.slotId)).timeout(Duration.ofMinutes(10L)).setHeader("Cookie", this.uploadCookie()).setHeader("Content-Type", "application/octet-stream").POST(var4).build();
      return this.client.sendAsync(var5, BodyHandlers.ofString(StandardCharsets.UTF_8)).thenCompose((var4x) -> {
         long var5 = this.getRetryDelaySeconds(var4x);
         if (this.shouldRetry(var5, var1)) {
            this.uploadStatus.restart();

            try {
               Thread.sleep(Duration.ofSeconds(var5));
            } catch (InterruptedException var8) {
            }

            return this.requestUpload(var1 + 1, var2);
         } else {
            return CompletableFuture.completedFuture(this.handleResponse(var4x));
         }
      });
   }

   private static HttpRequest.BodyPublisher inputStreamPublisherWithSize(Supplier<@Nullable InputStream> var0, long var1) {
      return BodyPublishers.fromPublisher(BodyPublishers.ofInputStream(var0), var1);
   }

   private String uploadCookie() {
      String var10000 = this.sessionId;
      return "sid=" + var10000 + ";token=" + this.uploadInfo.token() + ";user=" + this.username + ";version=" + this.clientVersion + ";worldVersion=" + this.worldVersion;
   }

   private UploadResult handleResponse(HttpResponse<String> var1) {
      int var2 = var1.statusCode();
      if (var2 == 401) {
         LOGGER.debug("Realms server returned 401: {}", var1.headers().firstValue("WWW-Authenticate"));
      }

      String var3 = null;
      String var4 = (String)var1.body();
      if (var4 != null && !var4.isBlank()) {
         try {
            JsonElement var5 = LenientJsonParser.parse(var4).getAsJsonObject().get("errorMsg");
            if (var5 != null) {
               var3 = var5.getAsString();
            }
         } catch (Exception var6) {
            LOGGER.warn("Failed to parse response {}", var4, var6);
         }
      }

      return new UploadResult(var2, var3);
   }

   private boolean shouldRetry(long var1, int var3) {
      return var1 > 0L && var3 + 1 < 5;
   }

   private long getRetryDelaySeconds(HttpResponse<?> var1) {
      return var1.headers().firstValueAsLong("Retry-After").orElse(0L);
   }

   static class UploadCountingInputStream extends CountingInputStream {
      private final UploadStatus uploadStatus;

      UploadCountingInputStream(InputStream var1, UploadStatus var2) {
         super(var1);
         this.uploadStatus = var2;
      }

      protected void afterRead(int var1) throws IOException {
         super.afterRead(var1);
         this.uploadStatus.onWrite(this.getByteCount());
      }
   }
}
