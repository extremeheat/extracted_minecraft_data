package net.minecraft.server.network;

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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
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
   private final URL joinEndpoint;
   private final URL leaveEndpoint;
   private final String authKey;
   private final int ruleId;
   private final String serverId;
   private final TextFilterClient.IgnoreStrategy chatIgnoreStrategy;
   private final ExecutorService workerPool;

   private void processJoinOrLeave(GameProfile var1, URL var2, Executor var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("server", this.serverId);
      var4.addProperty("room", "Chat");
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

   private CompletableFuture<Optional<String>> requestMessageProcessing(GameProfile var1, String var2, TextFilterClient.IgnoreStrategy var3, Executor var4) {
      if (var2.isEmpty()) {
         return CompletableFuture.completedFuture(Optional.of(""));
      } else {
         JsonObject var5 = new JsonObject();
         var5.addProperty("rule", this.ruleId);
         var5.addProperty("server", this.serverId);
         var5.addProperty("room", "Chat");
         var5.addProperty("player", var1.getId().toString());
         var5.addProperty("player_display_name", var1.getName());
         var5.addProperty("text", var2);
         return CompletableFuture.supplyAsync(() -> {
            try {
               JsonObject var4 = this.processRequestResponse(var5, this.chatEndpoint);
               boolean var5x = GsonHelper.getAsBoolean(var4, "response", false);
               if (var5x) {
                  return Optional.of(var2);
               } else {
                  String var6 = GsonHelper.getAsString(var4, "hashed", (String)null);
                  if (var6 == null) {
                     return Optional.empty();
                  } else {
                     int var7 = GsonHelper.getAsJsonArray(var4, "hashes").size();
                     return var3.shouldIgnore(var6, var7) ? Optional.empty() : Optional.of(var6);
                  }
               }
            } catch (Exception var8) {
               LOGGER.warn("Failed to validate message '{}'", var2, var8);
               return Optional.empty();
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
      Throwable var5 = null;

      JsonObject var6;
      try {
         if (var3.getResponseCode() == 204) {
            var6 = new JsonObject();
            return var6;
         }

         try {
            var6 = Streams.parse(new JsonReader(new InputStreamReader(var4))).getAsJsonObject();
         } finally {
            this.drainStream(var4);
         }
      } catch (Throwable var23) {
         var5 = var23;
         throw var23;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var21) {
                  var5.addSuppressed(var21);
               }
            } else {
               var4.close();
            }
         }

      }

      return var6;
   }

   private void processRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);
      InputStream var4 = var3.getInputStream();
      Throwable var5 = null;

      try {
         this.drainStream(var4);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               var4.close();
            }
         }

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
      Throwable var5 = null;

      try {
         JsonWriter var6 = new JsonWriter(var4);
         Throwable var7 = null;

         try {
            Streams.write(var1, var6);
         } catch (Throwable var30) {
            var7 = var30;
            throw var30;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var29) {
                     var7.addSuppressed(var29);
                  }
               } else {
                  var6.close();
               }
            }

         }
      } catch (Throwable var32) {
         var5 = var32;
         throw var32;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var28) {
                  var5.addSuppressed(var28);
               }
            } else {
               var4.close();
            }
         }

      }

      int var34 = var3.getResponseCode();
      if (var34 >= 200 && var34 < 300) {
         return var3;
      } else {
         throw new TextFilterClient.RequestFailedException(var34 + " " + var3.getResponseMessage());
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

      boolean shouldIgnore(String var1, int var2);
   }

   class PlayerContext implements TextFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      private PlayerContext(GameProfile var2) {
         super();
         this.profile = var2;
         ProcessorMailbox var3 = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + var2.getName());
         this.streamExecutor = var3::tell;
      }

      public void join() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, this.streamExecutor);
      }

      public void leave() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, this.streamExecutor);
      }

      public CompletableFuture<Optional<List<String>>> processMessageBundle(List<String> var1) {
         List var2 = (List)var1.stream().map((var1x) -> {
            return TextFilterClient.this.requestMessageProcessing(this.profile, var1x, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
         }).collect(ImmutableList.toImmutableList());
         return Util.sequence(var2).thenApply((var0) -> {
            return Optional.of(var0.stream().map((var0x) -> {
               return (String)var0x.orElse("");
            }).collect(ImmutableList.toImmutableList()));
         }).exceptionally((var0) -> {
            return Optional.empty();
         });
      }

      public CompletableFuture<Optional<String>> processStreamMessage(String var1) {
         return TextFilterClient.this.requestMessageProcessing(this.profile, var1, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }

      // $FF: synthetic method
      PlayerContext(GameProfile var2, Object var3) {
         this(var2);
      }
   }

   public static class RequestFailedException extends RuntimeException {
      private RequestFailedException(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      RequestFailedException(String var1, Object var2) {
         this(var1);
      }
   }
}
