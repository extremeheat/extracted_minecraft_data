package net.minecraft.server.network;

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
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringUtil;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.slf4j.Logger;

public abstract class ServerTextFilter implements AutoCloseable {
   protected static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (var0) -> {
      Thread var1 = new Thread(var0);
      var1.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return var1;
   };
   private final URL chatEndpoint;
   private final MessageEncoder chatEncoder;
   final IgnoreStrategy chatIgnoreStrategy;
   final ExecutorService workerPool;

   protected static ExecutorService createWorkerPool(int var0) {
      return Executors.newFixedThreadPool(var0, THREAD_FACTORY);
   }

   protected ServerTextFilter(URL var1, MessageEncoder var2, IgnoreStrategy var3, ExecutorService var4) {
      super();
      this.chatIgnoreStrategy = var3;
      this.workerPool = var4;
      this.chatEndpoint = var1;
      this.chatEncoder = var2;
   }

   protected static URL getEndpoint(URI var0, @Nullable JsonObject var1, String var2, String var3) throws MalformedURLException {
      String var4 = getEndpointFromConfig(var1, var2, var3);
      return var0.resolve("/" + var4).toURL();
   }

   protected static String getEndpointFromConfig(@Nullable JsonObject var0, String var1, String var2) {
      return var0 != null ? GsonHelper.getAsString(var0, var1, var2) : var2;
   }

   @Nullable
   public static ServerTextFilter createFromConfig(DedicatedServerProperties var0) {
      String var1 = var0.textFilteringConfig;
      if (StringUtil.isBlank(var1)) {
         return null;
      } else {
         ServerTextFilter var10000;
         switch (var0.textFilteringVersion) {
            case 0:
               var10000 = LegacyTextFilter.createTextFilterFromConfig(var1);
               break;
            case 1:
               var10000 = PlayerSafetyServiceTextFilter.createTextFilterFromConfig(var1);
               break;
            default:
               LOGGER.warn("Could not create text filter - unsupported text filtering version used");
               var10000 = null;
         }

         return var10000;
      }
   }

   protected CompletableFuture<FilteredText> requestMessageProcessing(GameProfile var1, String var2, IgnoreStrategy var3, Executor var4) {
      return var2.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
         JsonObject var4 = this.chatEncoder.encode(var1, var2);

         try {
            JsonObject var5 = this.processRequestResponse(var4, this.chatEndpoint);
            return this.filterText(var2, var3, var5);
         } catch (Exception var6) {
            LOGGER.warn("Failed to validate message '{}'", var2, var6);
            return FilteredText.fullyFiltered(var2);
         }
      }, var4);
   }

   protected abstract FilteredText filterText(String var1, IgnoreStrategy var2, JsonObject var3);

   protected FilterMask parseMask(String var1, JsonArray var2, IgnoreStrategy var3) {
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

   public void close() {
      this.workerPool.shutdownNow();
   }

   protected void drainStream(InputStream var1) throws IOException {
      byte[] var2 = new byte[1024];

      while(var1.read(var2) != -1) {
      }

   }

   private JsonObject processRequestResponse(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.makeRequest(var1, var2);
      InputStream var4 = var3.getInputStream();

      JsonObject var13;
      label89: {
         try {
            if (var3.getResponseCode() == 204) {
               var13 = new JsonObject();
               break label89;
            }

            try {
               var13 = Streams.parse(new JsonReader(new InputStreamReader(var4, StandardCharsets.UTF_8))).getAsJsonObject();
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

         return var13;
      }

      if (var4 != null) {
         var4.close();
      }

      return var13;
   }

   protected HttpURLConnection makeRequest(JsonObject var1, URL var2) throws IOException {
      HttpURLConnection var3 = this.getURLConnection(var2);
      this.setAuthorizationProperty(var3);
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
         throw new RequestFailedException(var12 + " " + var3.getResponseMessage());
      }
   }

   protected abstract void setAuthorizationProperty(HttpURLConnection var1);

   protected int connectionReadTimeout() {
      return 2000;
   }

   protected HttpURLConnection getURLConnection(URL var1) throws IOException {
      HttpURLConnection var2 = (HttpURLConnection)var1.openConnection();
      var2.setConnectTimeout(15000);
      var2.setReadTimeout(this.connectionReadTimeout());
      var2.setUseCaches(false);
      var2.setDoOutput(true);
      var2.setDoInput(true);
      var2.setRequestMethod("POST");
      var2.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      var2.setRequestProperty("Accept", "application/json");
      var2.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
      return var2;
   }

   public TextFilter createContext(GameProfile var1) {
      return new PlayerContext(var1);
   }

   protected static class RequestFailedException extends RuntimeException {
      protected RequestFailedException(String var1) {
         super(var1);
      }
   }

   protected class PlayerContext implements TextFilter {
      protected final GameProfile profile;
      protected final Executor streamExecutor;

      protected PlayerContext(final GameProfile var2) {
         super();
         this.profile = var2;
         ConsecutiveExecutor var3 = new ConsecutiveExecutor(ServerTextFilter.this.workerPool, "chat stream for " + var2.getName());
         Objects.requireNonNull(var3);
         this.streamExecutor = var3::schedule;
      }

      public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1) {
         List var2 = (List)var1.stream().map((var1x) -> ServerTextFilter.this.requestMessageProcessing(this.profile, var1x, ServerTextFilter.this.chatIgnoreStrategy, this.streamExecutor)).collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(var2).exceptionally((var0) -> ImmutableList.of());
      }

      public CompletableFuture<FilteredText> processStreamMessage(String var1) {
         return ServerTextFilter.this.requestMessageProcessing(this.profile, var1, ServerTextFilter.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }

   @FunctionalInterface
   public interface IgnoreStrategy {
      IgnoreStrategy NEVER_IGNORE = (var0, var1) -> false;
      IgnoreStrategy IGNORE_FULLY_FILTERED = (var0, var1) -> var0.length() == var1;

      static IgnoreStrategy ignoreOverThreshold(int var0) {
         return (var1, var2) -> var2 >= var0;
      }

      static IgnoreStrategy select(int var0) {
         IgnoreStrategy var10000;
         switch (var0) {
            case -1 -> var10000 = NEVER_IGNORE;
            case 0 -> var10000 = IGNORE_FULLY_FILTERED;
            default -> var10000 = ignoreOverThreshold(var0);
         }

         return var10000;
      }

      boolean shouldIgnore(String var1, int var2);
   }

   @FunctionalInterface
   protected interface MessageEncoder {
      JsonObject encode(GameProfile var1, String var2);
   }
}
