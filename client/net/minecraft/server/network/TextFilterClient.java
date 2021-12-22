package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextFilterClient implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (var0) -> {
      Thread var1 = new Thread(var0);
      var1.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return var1;
   };
   private final URL chatEndpoint;
   final URL joinEndpoint;
   final URL leaveEndpoint;
   private final String authKey;
   private final int ruleId;
   private final String serverId;
   private final String roomId;
   final TextFilterClient.IgnoreStrategy chatIgnoreStrategy;
   final ExecutorService workerPool;

   private TextFilterClient(URI var1, String var2, int var3, String var4, String var5, TextFilterClient.IgnoreStrategy var6, int var7) throws MalformedURLException {
      super();
      this.authKey = var2;
      this.ruleId = var3;
      this.serverId = var4;
      this.roomId = var5;
      this.chatIgnoreStrategy = var6;
      this.chatEndpoint = var1.resolve("/v1/chat").toURL();
      this.joinEndpoint = var1.resolve("/v1/join").toURL();
      this.leaveEndpoint = var1.resolve("/v1/leave").toURL();
      this.workerPool = Executors.newFixedThreadPool(var7, THREAD_FACTORY);
   }

   @Nullable
   public static TextFilterClient createFromConfig(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return null;
      } else {
         try {
            JsonObject var1 = GsonHelper.parse(var0);
            URI var2 = new URI(GsonHelper.getAsString(var1, "apiServer"));
            String var3 = GsonHelper.getAsString(var1, "apiKey");
            if (var3.isEmpty()) {
               throw new IllegalArgumentException("Missing API key");
            } else {
               int var4 = GsonHelper.getAsInt(var1, "ruleId", 1);
               String var5 = GsonHelper.getAsString(var1, "serverId", "");
               String var6 = GsonHelper.getAsString(var1, "roomId", "Java:Chat");
               int var7 = GsonHelper.getAsInt(var1, "hashesToDrop", -1);
               int var8 = GsonHelper.getAsInt(var1, "maxConcurrentRequests", 7);
               TextFilterClient.IgnoreStrategy var9 = TextFilterClient.IgnoreStrategy.select(var7);
               return new TextFilterClient(var2, Base64.getEncoder().encodeToString(var3.getBytes(StandardCharsets.US_ASCII)), var4, var5, var6, var9, var8);
            }
         } catch (Exception var10) {
            LOGGER.warn("Failed to parse chat filter config {}", var0, var10);
            return null;
         }
      }
   }

   void processJoinOrLeave(GameProfile var1, URL var2, Executor var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("server", this.serverId);
      var4.addProperty("room", this.roomId);
      var4.addProperty("user_id", var1.getId().toString());
      var4.addProperty("user_display_name", var1.getName());
      var3.execute(() -> {
         try {
            this.processRequest(var4, var2);
         } catch (Exception var5) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", var2, var1, var5);
         }

      });
   }

   CompletableFuture<TextFilter.FilteredText> requestMessageProcessing(GameProfile var1, String var2, TextFilterClient.IgnoreStrategy var3, Executor var4) {
      if (var2.isEmpty()) {
         return CompletableFuture.completedFuture(TextFilter.FilteredText.EMPTY);
      } else {
         JsonObject var5 = new JsonObject();
         var5.addProperty("rule", this.ruleId);
         var5.addProperty("server", this.serverId);
         var5.addProperty("room", this.roomId);
         var5.addProperty("player", var1.getId().toString());
         var5.addProperty("player_display_name", var1.getName());
         var5.addProperty("text", var2);
         return CompletableFuture.supplyAsync(() -> {
            try {
               JsonObject var4 = this.processRequestResponse(var5, this.chatEndpoint);
               boolean var5x = GsonHelper.getAsBoolean(var4, "response", false);
               if (var5x) {
                  return TextFilter.FilteredText.passThrough(var2);
               } else {
                  String var6 = GsonHelper.getAsString(var4, "hashed", (String)null);
                  if (var6 == null) {
                     return TextFilter.FilteredText.fullyFiltered(var2);
                  } else {
                     int var7 = GsonHelper.getAsJsonArray(var4, "hashes").size();
                     return var3.shouldIgnore(var6, var7) ? TextFilter.FilteredText.fullyFiltered(var2) : new TextFilter.FilteredText(var2, var6);
                  }
               }
            } catch (Exception var8) {
               LOGGER.warn("Failed to validate message '{}'", var2, var8);
               return TextFilter.FilteredText.fullyFiltered(var2);
            }
         }, var4);
      }
   }

   public void close() {
      this.workerPool.shutdownNow();
   }

   private void drainStream(InputStream var1) throws IOException {
      byte[] var2 = new byte[1024];

      while(var1.read(var2) != -1) {
      }

   }

   private JsonObject processRequestResponse(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);
      InputStream var4 = var3.getInputStream();

      JsonObject var5;
      label90: {
         try {
            if (var3.getResponseCode() == 204) {
               var5 = new JsonObject();
               break label90;
            }

            try {
               var5 = Streams.parse(new JsonReader(new InputStreamReader(var4))).getAsJsonObject();
            } finally {
               this.drainStream(var4);
            }
         } catch (Throwable var12) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (var4 != null) {
            var4.close();
         }

         return var5;
      }

      if (var4 != null) {
         var4.close();
      }

      return var5;
   }

   private void processRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);
      InputStream var4 = var3.getInputStream();

      try {
         this.drainStream(var4);
      } catch (Throwable var8) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var4 != null) {
         var4.close();
      }

   }

   private HttpURLConnection makeRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = (HttpURLConnection)var2.openConnection();
      var3.setConnectTimeout(15000);
      var3.setReadTimeout(2000);
      var3.setUseCaches(false);
      var3.setDoOutput(true);
      var3.setDoInput(true);
      var3.setRequestMethod("POST");
      var3.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      var3.setRequestProperty("Accept", "application/json");
      var3.setRequestProperty("Authorization", "Basic " + this.authKey);
      var3.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
      OutputStreamWriter var4 = new OutputStreamWriter(var3.getOutputStream(), StandardCharsets.UTF_8);

      try {
         JsonWriter var5 = new JsonWriter(var4);

         try {
            Streams.write(var1, var5);
         } catch (Throwable var10) {
            try {
               var5.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         var5.close();
      } catch (Throwable var11) {
         try {
            var4.close();
         } catch (Throwable var8) {
            var11.addSuppressed(var8);
         }

         throw var11;
      }

      var4.close();
      int var12 = var3.getResponseCode();
      if (var12 >= 200 && var12 < 300) {
         return var3;
      } else {
         throw new TextFilterClient.RequestFailedException(var12 + " " + var3.getResponseMessage());
      }
   }

   public TextFilter createContext(GameProfile var1) {
      return new TextFilterClient.PlayerContext(var1);
   }

   @FunctionalInterface
   public interface IgnoreStrategy {
      TextFilterClient.IgnoreStrategy NEVER_IGNORE = (var0, var1) -> {
         return false;
      };
      TextFilterClient.IgnoreStrategy IGNORE_FULLY_FILTERED = (var0, var1) -> {
         return var0.length() == var1;
      };

      static TextFilterClient.IgnoreStrategy ignoreOverThreshold(int var0) {
         return (var1, var2) -> {
            return var2 >= var0;
         };
      }

      static TextFilterClient.IgnoreStrategy select(int var0) {
         switch(var0) {
         case -1:
            return NEVER_IGNORE;
         case 0:
            return IGNORE_FULLY_FILTERED;
         default:
            return ignoreOverThreshold(var0);
         }
      }

      boolean shouldIgnore(String var1, int var2);
   }

   public static class RequestFailedException extends RuntimeException {
      RequestFailedException(String var1) {
         super(var1);
      }
   }

   class PlayerContext implements TextFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      PlayerContext(GameProfile var2) {
         super();
         this.profile = var2;
         ProcessorMailbox var3 = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + var2.getName());
         Objects.requireNonNull(var3);
         this.streamExecutor = var3::tell;
      }

      public void join() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, this.streamExecutor);
      }

      public void leave() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, this.streamExecutor);
      }

      public CompletableFuture<List<TextFilter.FilteredText>> processMessageBundle(List<String> var1) {
         List var2 = (List)var1.stream().map((var1x) -> {
            return TextFilterClient.this.requestMessageProcessing(this.profile, var1x, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
         }).collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(var2).exceptionally((var0) -> {
            return ImmutableList.of();
         });
      }

      public CompletableFuture<TextFilter.FilteredText> processStreamMessage(String var1) {
         return TextFilterClient.this.requestMessageProcessing(this.profile, var1, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }
}
