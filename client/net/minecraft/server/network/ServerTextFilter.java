package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class ServerTextFilter implements AutoCloseable {
   protected static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (runnable) -> {
      Thread thread = new Thread(runnable);
      thread.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return thread;
   };
   private final URL chatEndpoint;
   private final MessageEncoder chatEncoder;
   private final IgnoreStrategy chatIgnoreStrategy;
   private final ExecutorService workerPool;

   protected static ExecutorService createWorkerPool(final int maxConcurrentRequests) {
      return Executors.newFixedThreadPool(maxConcurrentRequests, THREAD_FACTORY);
   }

   protected ServerTextFilter(final URL chatEndpoint, final MessageEncoder chatEncoder, final IgnoreStrategy chatIgnoreStrategy, final ExecutorService workerPool) {
      super();
      this.chatIgnoreStrategy = chatIgnoreStrategy;
      this.workerPool = workerPool;
      this.chatEndpoint = chatEndpoint;
      this.chatEncoder = chatEncoder;
   }

   protected static URL getEndpoint(final URI host, final @Nullable JsonObject source, final String id, final String def) throws MalformedURLException {
      String endpointConfig = getEndpointFromConfig(source, id, def);
      return host.resolve("/" + endpointConfig).toURL();
   }

   protected static String getEndpointFromConfig(final @Nullable JsonObject source, final String id, final String def) {
      return source != null ? GsonHelper.getAsString(source, id, def) : def;
   }

   public static @Nullable ServerTextFilter createFromConfig(final DedicatedServerProperties config) {
      String textFilteringConfig = config.textFilteringConfig;
      if (StringUtil.isBlank(textFilteringConfig)) {
         return null;
      } else {
         ServerTextFilter var10000;
         switch (config.textFilteringVersion) {
            case 0:
               var10000 = LegacyTextFilter.createTextFilterFromConfig(textFilteringConfig);
               break;
            case 1:
               var10000 = PlayerSafetyServiceTextFilter.createTextFilterFromConfig(textFilteringConfig);
               break;
            default:
               LOGGER.warn("Could not create text filter - unsupported text filtering version used");
               var10000 = null;
         }

         return var10000;
      }
   }

   protected CompletableFuture<FilteredText> requestMessageProcessing(final GameProfile sender, final String message, final IgnoreStrategy ignoreStrategy, final Executor executor) {
      return message.isEmpty() ? CompletableFuture.completedFuture(FilteredText.EMPTY) : CompletableFuture.supplyAsync(() -> {
         JsonObject object = this.chatEncoder.encode(sender, message);

         try {
            JsonObject result = this.processRequestResponse(object, this.chatEndpoint);
            return this.filterText(message, ignoreStrategy, result);
         } catch (Exception e) {
            LOGGER.warn("Failed to validate message '{}'", message, e);
            return FilteredText.fullyFiltered(message);
         }
      }, executor);
   }

   protected abstract FilteredText filterText(final String message, final IgnoreStrategy ignoreStrategy, final JsonObject result);

   protected FilterMask parseMask(final String message, final JsonArray removedChars, final IgnoreStrategy ignoreStrategy) {
      if (removedChars.isEmpty()) {
         return FilterMask.PASS_THROUGH;
      } else if (ignoreStrategy.shouldIgnore(message, removedChars.size())) {
         return FilterMask.FULLY_FILTERED;
      } else {
         FilterMask mask = new FilterMask(message.length());

         for(int i = 0; i < removedChars.size(); ++i) {
            mask.setFiltered(removedChars.get(i).getAsInt());
         }

         return mask;
      }
   }

   public void close() {
      this.workerPool.shutdownNow();
   }

   protected void drainStream(final InputStream input) throws IOException {
      byte[] trashcan = new byte[1024];

      while(input.read(trashcan) != -1) {
      }

   }

   private JsonObject processRequestResponse(final JsonObject payload, final URL url) throws IOException {
      HttpURLConnection connection = this.makeRequest(payload, url);
      InputStream is = connection.getInputStream();

      JsonObject var13;
      label89: {
         try {
            if (connection.getResponseCode() == 204) {
               var13 = new JsonObject();
               break label89;
            }

            try {
               var13 = LenientJsonParser.parse((Reader)(new InputStreamReader(is, StandardCharsets.UTF_8))).getAsJsonObject();
            } finally {
               this.drainStream(is);
            }
         } catch (Throwable var12) {
            if (is != null) {
               try {
                  is.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (is != null) {
            is.close();
         }

         return var13;
      }

      if (is != null) {
         is.close();
      }

      return var13;
   }

   protected HttpURLConnection makeRequest(final JsonObject payload, final URL url) throws IOException {
      HttpURLConnection connection = this.getURLConnection(url);
      this.setAuthorizationProperty(connection);
      OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);

      try {
         JsonWriter jsonWriter = new JsonWriter(writer);

         try {
            Streams.write(payload, jsonWriter);
         } catch (Throwable var10) {
            try {
               jsonWriter.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         jsonWriter.close();
      } catch (Throwable var11) {
         try {
            writer.close();
         } catch (Throwable var8) {
            var11.addSuppressed(var8);
         }

         throw var11;
      }

      writer.close();
      int responseCode = connection.getResponseCode();
      if (responseCode >= 200 && responseCode < 300) {
         return connection;
      } else {
         throw new RequestFailedException(responseCode + " " + connection.getResponseMessage());
      }
   }

   protected abstract void setAuthorizationProperty(final HttpURLConnection connection);

   protected int connectionReadTimeout() {
      return 2000;
   }

   protected HttpURLConnection getURLConnection(final URL url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setConnectTimeout(15000);
      connection.setReadTimeout(this.connectionReadTimeout());
      connection.setUseCaches(false);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().name());
      return connection;
   }

   public TextFilter createContext(final GameProfile gameProfile) {
      return new PlayerContext(gameProfile);
   }

   protected static class RequestFailedException extends RuntimeException {
      protected RequestFailedException(final String message) {
         super(message);
      }
   }

   protected class PlayerContext implements TextFilter {
      protected final GameProfile profile;
      protected final Executor streamExecutor;

      protected PlayerContext(final GameProfile profile) {
         Objects.requireNonNull(ServerTextFilter.this);
         super();
         this.profile = profile;
         ConsecutiveExecutor streamProcessor = new ConsecutiveExecutor(ServerTextFilter.this.workerPool, "chat stream for " + profile.name());
         Objects.requireNonNull(streamProcessor);
         this.streamExecutor = streamProcessor::schedule;
      }

      public CompletableFuture<List<FilteredText>> processMessageBundle(final List<String> messages) {
         List<CompletableFuture<FilteredText>> requests = (List)messages.stream().map((message) -> ServerTextFilter.this.requestMessageProcessing(this.profile, message, ServerTextFilter.this.chatIgnoreStrategy, this.streamExecutor)).collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(requests).exceptionally((e) -> ImmutableList.of());
      }

      public CompletableFuture<FilteredText> processStreamMessage(final String message) {
         return ServerTextFilter.this.requestMessageProcessing(this.profile, message, ServerTextFilter.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }

   @FunctionalInterface
   public interface IgnoreStrategy {
      IgnoreStrategy NEVER_IGNORE = (message, removedCharCount) -> false;
      IgnoreStrategy IGNORE_FULLY_FILTERED = (message, removedCharCount) -> message.length() == removedCharCount;

      static IgnoreStrategy ignoreOverThreshold(final int threshold) {
         return (message, removedCharCount) -> removedCharCount >= threshold;
      }

      static IgnoreStrategy select(final int hashesToDrop) {
         IgnoreStrategy var10000;
         switch (hashesToDrop) {
            case -1 -> var10000 = NEVER_IGNORE;
            case 0 -> var10000 = IGNORE_FULLY_FILTERED;
            default -> var10000 = ignoreOverThreshold(hashesToDrop);
         }

         return var10000;
      }

      boolean shouldIgnore(final String message, final int removedCharCount);
   }

   @FunctionalInterface
   protected interface MessageEncoder {
      JsonObject encode(GameProfile profile, String message);
   }
}
