package net.minecraft.client.multiplayer.signaling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.services.MinecraftServicesDiscoveryService;
import com.mojang.authlib.services.MinecraftServicesSignalingService;
import com.mojang.authlib.services.response.PresenceStatus;
import com.mojang.authlib.services.response.PresenceStatusDto;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.client.User;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.jsonrpc.JsonRPCErrors;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class SignalingClient implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration TIMEOUT = Duration.ofSeconds(15L);
   private static final long INITIAL_RECONNECT_DELAY_MS = 1000L;
   private static final long MAX_RECONNECT_DELAY_MS = 30000L;
   private static final String PING = "System_Ping_v1_0";
   private static final String PONG = "System_Pong_v1_0";
   private static final String PRESENCE_UPDATED = "Java_PresenceUpdated_v1_0";
   private static final Codec<PresenceStatus> PRESENCE_STATUS_CODEC;
   private static final Codec<PresenceStatusDto> PRESENCE_CODEC;
   private final MinecraftServicesSignalingService signalingService;
   private final CopyOnWriteArrayList<Consumer<PresenceStatusDto>> presenceListeners = new CopyOnWriteArrayList();
   private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, Thread.ofPlatform().daemon().name("Signaling").factory());
   private boolean started;
   private volatile boolean closed;
   private Duration pingInterval;
   private long reconnectDelayMs;
   private @Nullable ScheduledFuture<?> pingTask;
   private @Nullable ScheduledFuture<?> pongTimeout;
   private @Nullable ScheduledFuture<?> reconnectTask;
   private JsonRpcClient rpc;

   public SignalingClient(final User user, final MinecraftServicesDiscoveryService discoveryService) {
      super();
      this.pingInterval = Duration.ZERO;
      this.reconnectDelayMs = 1000L;
      this.executor.setRemoveOnCancelPolicy(true);
      this.signalingService = discoveryService.createSignalingService(user.getAccessToken());
      this.rpc = this.createConnection();
   }

   public void connect() {
      this.execute(() -> {
         if (!this.started) {
            this.started = true;
            this.connect(this.rpc);
         }
      });
   }

   private synchronized void execute(final Runnable task) {
      if (!this.closed) {
         this.executor.execute(task);
      }

   }

   private JsonRpcClient createConnection() {
      return new JsonRpcClient(this.executor, this::handleMethod, this::onConnected, this::onDisconnected);
   }

   private void connect(final JsonRpcClient connection) {
      this.signalingService.prepareConnection().thenComposeAsync((prepared) -> {
         this.pingInterval = prepared.pingInterval();
         return prepared.connect(connection);
      }, this.executor).whenComplete((var2, error) -> {
         if (error != null) {
            this.execute(() -> {
               LOGGER.warn("Signaling connection failed", error);
               connection.close();
            });
         }

      });
   }

   private void reconnect() {
      this.reconnectTask = null;
      this.rpc = this.createConnection();
      this.connect(this.rpc);
   }

   private void onDisconnected() {
      if (!this.closed) {
         this.stopHeartbeat();
         long delayMs = ThreadLocalRandom.current().nextLong(this.reconnectDelayMs / 2L, this.reconnectDelayMs + 1L);
         this.reconnectDelayMs = Math.min(this.reconnectDelayMs * 2L, 30000L);
         LOGGER.info("Signaling reconnecting in {} ms", delayMs);
         this.reconnectTask = this.executor.schedule(this::reconnect, delayMs, TimeUnit.MILLISECONDS);
      }
   }

   private void stopHeartbeat() {
      if (this.pingTask != null) {
         this.pingTask.cancel(false);
         this.pingTask = null;
      }

      this.cancelPongTimeout();
   }

   private void onConnected() {
      if (!this.closed) {
         LOGGER.info("Signaling connected");
         this.reconnectDelayMs = 1000L;
         this.pingTask = this.executor.scheduleWithFixedDelay(this::sendPing, 0L, this.pingInterval.toSeconds(), TimeUnit.SECONDS);
      }
   }

   private void sendPing() {
      if (this.pongTimeout == null) {
         this.pongTimeout = this.executor.schedule(this::onPongTimeout, TIMEOUT.toSeconds(), TimeUnit.SECONDS);
         this.rpc.sendNotification("System_Ping_v1_0", List.of()).thenRun(() -> LOGGER.debug("Signaling ping sent"));
      }
   }

   private void onPongTimeout() {
      this.stopHeartbeat();
      LOGGER.warn("Signaling pong timed out after {} seconds", TIMEOUT.toSeconds());
      this.rpc.close();
   }

   private void cancelPongTimeout() {
      if (this.pongTimeout != null) {
         this.pongTimeout.cancel(false);
         this.pongTimeout = null;
      }

   }

   private void handleMethod(final @Nullable JsonElement id, final String method, final @Nullable JsonElement params) {
      if (!this.closed) {
         switch (method) {
            case "System_Pong_v1_0":
               this.cancelPongTimeout();
               LOGGER.debug("Signaling pong received: id={}", id);
               this.acknowledge(id);
               break;
            case "Java_PresenceUpdated_v1_0":
               this.handlePresenceUpdated(id, params);
               break;
            default:
               LOGGER.warn("Unsupported signaling method={}, id={}", method, id);
               if (id != null) {
                  this.rpc.sendError(id, JsonRPCErrors.METHOD_NOT_FOUND);
               }
         }

      }
   }

   private void handlePresenceUpdated(final @Nullable JsonElement id, final @Nullable JsonElement params) {
      PresenceStatusDto presence;
      try {
         JsonObject envelope = paramsObject(params, "PresenceUpdated");
         presence = (PresenceStatusDto)PRESENCE_CODEC.parse(JsonOps.INSTANCE, envelope).getOrThrow(JsonParseException::new);
      } catch (IllegalStateException | JsonParseException error) {
         LOGGER.warn("Rejected signaling PresenceUpdated: id={}, params={}", new Object[]{id, params, error});
         if (id != null) {
            this.rpc.sendError(id, JsonRPCErrors.INVALID_PARAMS);
         }

         return;
      }

      LOGGER.debug("Signaling presence updated: profileId={}, status={}", presence.profileId(), presence.status());
      this.acknowledge(id);

      for(Consumer<PresenceStatusDto> listener : this.presenceListeners) {
         try {
            listener.accept(presence);
         } catch (RuntimeException error) {
            LOGGER.warn("Signaling presence listener failed for profile {}", presence.profileId(), error);
         }
      }

   }

   private static JsonObject paramsObject(final @Nullable JsonElement params, final String method) {
      if (params != null && params.isJsonObject()) {
         return params.getAsJsonObject();
      } else if (params != null && params.isJsonArray() && !params.getAsJsonArray().isEmpty()) {
         return GsonHelper.convertToJsonObject(params.getAsJsonArray().get(0), "params[0]");
      } else {
         throw new IllegalStateException("Expected " + method + " params to be an object or a non-empty array");
      }
   }

   public void addPresenceListener(final Consumer<PresenceStatusDto> listener) {
      this.presenceListeners.addIfAbsent(listener);
   }

   private void acknowledge(final @Nullable JsonElement id) {
      if (id != null && !id.isJsonNull()) {
         this.rpc.sendResponse(id, new JsonObject());
      }

   }

   public synchronized void close() {
      if (!this.closed) {
         this.closed = true;
         this.executor.execute(() -> {
            this.stopHeartbeat();
            if (this.reconnectTask != null) {
               this.reconnectTask.cancel(false);
               this.reconnectTask = null;
            }

            this.rpc.close();
            this.signalingService.close();
            this.executor.shutdown();
         });
      }
   }

   static {
      PRESENCE_STATUS_CODEC = Codec.STRING.comapFlatMap((status) -> {
         try {
            return DataResult.success(PresenceStatus.valueOf(status));
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> "Unknown presence status " + status);
         }
      }, Enum::name);
      PRESENCE_CODEC = RecordCodecBuilder.create((i) -> i.group(UUIDUtil.AUTHLIB_CODEC.fieldOf("profileId").forGetter(PresenceStatusDto::profileId), UUIDUtil.AUTHLIB_CODEC.fieldOf("pmid").forGetter(PresenceStatusDto::pmid), PRESENCE_STATUS_CODEC.fieldOf("status").forGetter(PresenceStatusDto::status), ExtraCodecs.INSTANT_ISO8601.fieldOf("lastUpdated").forGetter(PresenceStatusDto::lastUpdated)).apply(i, PresenceStatusDto::new));
   }
}
