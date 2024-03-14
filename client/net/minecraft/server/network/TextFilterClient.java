package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class TextFilterClient implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = var0 -> {
      Thread var1 = new Thread(var0);
      var1.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return var1;
   };
   private static final String DEFAULT_ENDPOINT = "v1/chat";
   private final URL chatEndpoint;
   private final TextFilterClient.MessageEncoder chatEncoder;
   final URL joinEndpoint;
   final TextFilterClient.JoinOrLeaveEncoder joinEncoder;
   final URL leaveEndpoint;
   final TextFilterClient.JoinOrLeaveEncoder leaveEncoder;
   private final String authKey;
   final TextFilterClient.IgnoreStrategy chatIgnoreStrategy;
   final ExecutorService workerPool;

   private TextFilterClient(
      URL var1,
      TextFilterClient.MessageEncoder var2,
      URL var3,
      TextFilterClient.JoinOrLeaveEncoder var4,
      URL var5,
      TextFilterClient.JoinOrLeaveEncoder var6,
      String var7,
      TextFilterClient.IgnoreStrategy var8,
      int var9
   ) {
      super();
      this.authKey = var7;
      this.chatIgnoreStrategy = var8;
      this.chatEndpoint = var1;
      this.chatEncoder = var2;
      this.joinEndpoint = var3;
      this.joinEncoder = var4;
      this.leaveEndpoint = var5;
      this.leaveEncoder = var6;
      this.workerPool = Executors.newFixedThreadPool(var9, THREAD_FACTORY);
   }

   private static URL getEndpoint(URI var0, @Nullable JsonObject var1, String var2, String var3) throws MalformedURLException {
      String var4 = getEndpointFromConfig(var1, var2, var3);
      return var0.resolve("/" + var4).toURL();
   }

   private static String getEndpointFromConfig(@Nullable JsonObject var0, String var1, String var2) {
      return var0 != null ? GsonHelper.getAsString(var0, var1, var2) : var2;
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
               JsonObject var9 = GsonHelper.getAsJsonObject(var1, "endpoints", null);
               String var10 = getEndpointFromConfig(var9, "chat", "v1/chat");
               boolean var11 = var10.equals("v1/chat");
               URL var12 = var2.resolve("/" + var10).toURL();
               URL var13 = getEndpoint(var2, var9, "join", "v1/join");
               URL var14 = getEndpoint(var2, var9, "leave", "v1/leave");
               TextFilterClient.JoinOrLeaveEncoder var15 = var2x -> {
                  JsonObject var3xx = new JsonObject();
                  var3xx.addProperty("server", var5);
                  var3xx.addProperty("room", var6);
                  var3xx.addProperty("user_id", var2x.getId().toString());
                  var3xx.addProperty("user_display_name", var2x.getName());
                  return var3xx;
               };
               TextFilterClient.MessageEncoder var16;
               if (var11) {
                  var16 = (var3x, var4x) -> {
                     JsonObject var5xx = new JsonObject();
                     var5xx.addProperty("rule", var4);
                     var5xx.addProperty("server", var5);
                     var5xx.addProperty("room", var6);
                     var5xx.addProperty("player", var3x.getId().toString());
                     var5xx.addProperty("player_display_name", var3x.getName());
                     var5xx.addProperty("text", var4x);
                     var5xx.addProperty("language", "*");
                     return var5xx;
                  };
               } else {
                  String var17 = String.valueOf(var4);
                  var16 = (var3x, var4x) -> {
                     JsonObject var5xx = new JsonObject();
                     var5xx.addProperty("rule_id", var17);
                     var5xx.addProperty("category", var5);
                     var5xx.addProperty("subcategory", var6);
                     var5xx.addProperty("user_id", var3x.getId().toString());
                     var5xx.addProperty("user_display_name", var3x.getName());
                     var5xx.addProperty("text", var4x);
                     var5xx.addProperty("language", "*");
                     return var5xx;
                  };
               }

               TextFilterClient.IgnoreStrategy var20 = TextFilterClient.IgnoreStrategy.select(var7);
               String var18 = Base64.getEncoder().encodeToString(var3.getBytes(StandardCharsets.US_ASCII));
               return new TextFilterClient(var12, var16, var13, var15, var14, var15, var18, var20, var8);
            }
         } catch (Exception var19) {
            LOGGER.warn("Failed to parse chat filter config {}", var0, var19);
            return null;
         }
      }
   }

   void processJoinOrLeave(GameProfile var1, URL var2, TextFilterClient.JoinOrLeaveEncoder var3, Executor var4) {
      var4.execute(() -> {
         JsonObject var4xx = var3.encode(var1);

         try {
            this.processRequest(var4xx, var2);
         } catch (Exception var6) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", new Object[]{var2, var1, var6});
         }
      });
   }

   CompletableFuture<FilteredText> requestMessageProcessing(GameProfile var1, String var2, TextFilterClient.IgnoreStrategy var3, Executor var4) {
      return var2.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
         JsonObject var4xx = this.chatEncoder.encode(var1, var2);

         try {
            JsonObject var5 = this.processRequestResponse(var4xx, this.chatEndpoint);
            boolean var6 = GsonHelper.getAsBoolean(var5, "response", false);
            if (var6) {
               return FilteredText.passThrough(var2);
            } else {
               String var7 = GsonHelper.getAsString(var5, "hashed", null);
               if (var7 == null) {
                  return FilteredText.fullyFiltered(var2);
               } else {
                  JsonArray var8 = GsonHelper.getAsJsonArray(var5, "hashes");
                  FilterMask var9 = this.parseMask(var2, var8, var3);
                  return new FilteredText(var2, var9);
               }
            }
         } catch (Exception var10) {
            LOGGER.warn("Failed to validate message '{}'", var2, var10);
            return FilteredText.fullyFiltered(var2);
         }
      }, var4);
   }

   private FilterMask parseMask(String var1, JsonArray var2, TextFilterClient.IgnoreStrategy var3) {
      if (var2.isEmpty()) {
         return FilterMask.PASS_THROUGH;
      } else if (var3.shouldIgnore(var1, var2.size())) {
         return FilterMask.FULLY_FILTERED;
      } else {
         FilterMask var4 = new FilterMask(var1.length());

         for(int var5 = 0; var5 < var2.size(); ++var5) {
            var4.setFiltered(var2.get(var5).getAsInt());
         }

         return var4;
      }
   }

   @Override
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

      JsonObject var5;
      try (InputStream var4 = var3.getInputStream()) {
         if (var3.getResponseCode() == 204) {
            return new JsonObject();
         }

         try {
            var5 = Streams.parse(new JsonReader(new InputStreamReader(var4, StandardCharsets.UTF_8))).getAsJsonObject();
         } finally {
            this.drainStream(var4);
         }
      }

      return var5;
   }

   private void processRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);

      try (InputStream var4 = var3.getInputStream()) {
         this.drainStream(var4);
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
      TextFilterClient.IgnoreStrategy NEVER_IGNORE = (var0, var1) -> false;
      TextFilterClient.IgnoreStrategy IGNORE_FULLY_FILTERED = (var0, var1) -> var0.length() == var1;

      static TextFilterClient.IgnoreStrategy ignoreOverThreshold(int var0) {
         return (var1, var2) -> var2 >= var0;
      }

      static TextFilterClient.IgnoreStrategy select(int var0) {
         return switch(var0) {
            case -1 -> NEVER_IGNORE;
            case 0 -> IGNORE_FULLY_FILTERED;
            default -> ignoreOverThreshold(var0);
         };
      }

      boolean shouldIgnore(String var1, int var2);
   }

   @FunctionalInterface
   interface JoinOrLeaveEncoder {
      JsonObject encode(GameProfile var1);
   }

   @FunctionalInterface
   interface MessageEncoder {
      JsonObject encode(GameProfile var1, String var2);
   }

   class PlayerContext implements TextFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      PlayerContext(GameProfile var2) {
         super();
         this.profile = var2;
         ProcessorMailbox var3 = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + var2.getName());
         this.streamExecutor = var3::tell;
      }

      @Override
      public void join() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, TextFilterClient.this.joinEncoder, this.streamExecutor);
      }

      @Override
      public void leave() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, TextFilterClient.this.leaveEncoder, this.streamExecutor);
      }

      @Override
      public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1) {
         List var2 = var1.stream()
            .map(var1x -> TextFilterClient.this.requestMessageProcessing(this.profile, var1x, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor))
            .collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(var2).exceptionally(var0 -> ImmutableList.of());
      }

      @Override
      public CompletableFuture<FilteredText> processStreamMessage(String var1) {
         return TextFilterClient.this.requestMessageProcessing(this.profile, var1, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }

   public static class RequestFailedException extends RuntimeException {
      RequestFailedException(String var1) {
         super(var1);
      }
   }
}
