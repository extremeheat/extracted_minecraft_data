package net.minecraft.client.multiplayer.p2p.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.onvoid.webrtc.RTCIceServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.p2p.SignalingErrorMapper;
import net.minecraft.client.multiplayer.p2p.SignalingException;
import net.minecraft.client.multiplayer.p2p.SignalingMessage;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.jsonrpc.JsonRPCErrors;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class SignalingServiceClient {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String WS_CONNECTION_ENDPOINT = "/ws/v1.0/messaging/connect/java";
   private static final Codec<String> SIGNALING_URI_CODEC;
   private static final Duration PING_INTERVAL;
   private static final String HEADER_AUTH = "x-mojangauth";
   private static final String HEADER_SESSION_ID = "Session-Id";
   private static final String HEADER_REQUEST_ID = "Request-Id";
   private static final Environment ENVIRONMENT;
   private final User user;
   private final String sessionId = UUID.randomUUID().toString();
   private final List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList();
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((r) -> {
      Thread t = new Thread(r, "P2P-Signaling");
      t.setDaemon(true);
      t.setUncaughtExceptionHandler((th, e) -> LOGGER.error("Uncaught in {}", th, e));
      return t;
   });
   private @Nullable HttpClient httpClient;
   private @Nullable CompletableFuture<JsonRpcClient> websocketConnect;
   private @Nullable ScheduledFuture<?> pingTask;
   private @Nullable FriendJoinHandler friendJoinHandler;
   private @Nullable WebRtcSignalingHandler webRtcSignalingHandler;
   private @Nullable CachedTurn cachedTurn;
   private @Nullable CachedSignalingUri cachedSignalingUri;
   private @Nullable CompletableFuture<RTCIceServer> pendingTurnRefresh;

   public SignalingServiceClient(final User user) {
      super();
      this.user = user;
   }

   public void setFriendJoinHandler(final @Nullable FriendJoinHandler handler) {
      this.executor.execute(() -> this.friendJoinHandler = handler);
   }

   public void setWebRtcSignalingHandler(final @Nullable WebRtcSignalingHandler handler) {
      this.executor.execute(() -> this.webRtcSignalingHandler = handler);
   }

   public void addConnectionListener(final ConnectionListener listener) {
      this.connectionListeners.add(listener);
   }

   public void removeConnectionListener(final ConnectionListener listener) {
      this.connectionListeners.remove(listener);
   }

   public void clearHandlers() {
      this.executor.execute(() -> {
         this.friendJoinHandler = null;
         this.webRtcSignalingHandler = null;
      });
   }

   public void connect() {
      this.executor.execute(this::connectWebSocket);
   }

   public void disconnect() {
      this.executor.execute(() -> this.teardown("explicit disconnect"));
   }

   public CompletableFuture<RTCIceServer> requestTurnAuth() {
      return CompletableFuture.completedFuture((Object)null).thenComposeAsync((var1) -> {
         CachedTurn cached = this.cachedTurn;
         if (cached != null && cached.isUsable()) {
            return CompletableFuture.completedFuture(cached.turnAuth().toRtcIceServer());
         } else if (this.pendingTurnRefresh != null) {
            return this.pendingTurnRefresh;
         } else {
            CompletableFuture<RTCIceServer> refresh = this.refreshTurnAuth();
            this.pendingTurnRefresh = refresh;
            refresh.whenCompleteAsync((var1x, var2) -> this.pendingTurnRefresh = null, this.executor);
            return refresh;
         }
      }, this.executor);
   }

   public CompletableFuture<Void> sendClientMessage(final UUID toPlayerId, final SignalingMessage message) {
      String encoded = ((JsonElement)SignalingMessage.CODEC.encodeStart(JsonOps.INSTANCE, message).getOrThrow(IllegalStateException::new)).toString();
      return CompletableFuture.completedFuture((Object)null).thenComposeAsync((var3) -> this.sendRequest("Signaling_SendClientMessage_v1_0", List.of(JsonNull.INSTANCE, new JsonPrimitive(toPlayerId.toString()), new JsonPrimitive(encoded))), this.executor).thenApply((var0) -> null).exceptionallyCompose((err) -> {
         Throwable patt0$temp = err.getCause();
         if (patt0$temp instanceof JsonRpcException rpcErr) {
            SignalingException mapped = SignalingErrorMapper.fromJsonRpc(toPlayerId, rpcErr);
            LOGGER.warn("Signaling rejected send: {}", mapped.getMessage());
            this.fireListeners((l) -> l.onSignalingError(toPlayerId, mapped));
            return CompletableFuture.failedFuture(mapped);
         } else {
            return CompletableFuture.failedFuture(err);
         }
      });
   }

   private void fireListeners(final Consumer<ConnectionListener> action) {
      for(ConnectionListener listener : this.connectionListeners) {
         try {
            action.accept(listener);
         } catch (RuntimeException e) {
            LOGGER.error("ConnectionListener {} threw", listener.getClass().getSimpleName(), e);
         }
      }

   }

   private void connectWebSocket() {
      if (this.websocketConnect == null) {
         HttpClient client = HttpClient.newBuilder().executor(Util.backgroundExecutor()).build();
         this.httpClient = client;
         JsonRpcClient rpc = new JsonRpcClient(this.executor, this::onRpcMethod, this::onWebsocketDown);
         String requestId = UUID.randomUUID().toString();
         this.websocketConnect = this.getSignalingUri(client, requestId).thenComposeAsync((wsUrl) -> this.openWebSocket(client, rpc, wsUrl, requestId), this.executor);
         this.websocketConnect.whenCompleteAsync((var2, err) -> {
            if (err != null) {
               Throwable cause = err instanceof CompletionException && err.getCause() != null ? err.getCause() : err;
               if (!this.isSameHttpClientSession(client)) {
                  LOGGER.debug("Stale signaling connect attempt failed: {}", cause.toString());
               } else {
                  LOGGER.warn("Signaling websocket connect failed: {}", cause.toString());
                  this.cachedSignalingUri = null;
                  this.teardown("websocket connect failed: " + cause.getMessage());
               }
            }
         }, this.executor);
      }
   }

   private void onWebsocketDown() {
      if (this.teardown("websocket closed")) {
         this.fireListeners(ConnectionListener::onSignalingDisconnected);
      }

   }

   private boolean teardown(final String reason) {
      HttpClient client = this.httpClient;
      CompletableFuture<JsonRpcClient> connectFuture = this.websocketConnect;
      if (client != null && connectFuture != null) {
         LOGGER.debug("Signaling session disconnecting ({})", reason);
         if (this.pingTask != null) {
            this.pingTask.cancel(false);
            this.pingTask = null;
         }

         this.pendingTurnRefresh = null;
         connectFuture.whenComplete((rpc, var2) -> {
            CompletableFuture<?> rpcClosed = rpc != null ? rpc.close() : CompletableFuture.completedFuture((Object)null);
            rpcClosed.whenComplete((var1, var2x) -> {
               Objects.requireNonNull(client);
               CompletableFuture.runAsync(client::close, Util.backgroundExecutor());
            });
         });
         connectFuture.completeExceptionally(new IllegalStateException("Signaling torn down: " + reason));
         this.httpClient = null;
         this.websocketConnect = null;
         return true;
      } else {
         return false;
      }
   }

   private CompletableFuture<String> getSignalingUri(final HttpClient client, final String requestId) {
      CachedSignalingUri cached = this.cachedSignalingUri;
      if (cached != null && cached.isUsable()) {
         return CompletableFuture.completedFuture(cached.wsUrl);
      } else {
         HttpRequest request = HttpRequest.newBuilder().uri(URI.create(ENVIRONMENT.getConfigurationUri())).header("x-mojangauth", this.user.getAccessToken()).header("Session-Id", this.sessionId).header("Request-Id", requestId).GET().build();
         return client.sendAsync(request, BodyHandlers.ofString()).thenApplyAsync((response) -> {
            if (response.statusCode() != 200) {
               throw new IllegalStateException("Unexpected config response status: " + response.statusCode());
            } else {
               String baseUri = (String)SIGNALING_URI_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString((String)response.body())).getOrThrow((s) -> new IllegalStateException("Malformed config response: " + s));
               String wsUrl = baseUri + "/ws/v1.0/messaging/connect/java";
               this.cachedSignalingUri = new CachedSignalingUri(wsUrl);
               return wsUrl;
            }
         }, this.executor);
      }
   }

   private CompletableFuture<JsonRpcClient> openWebSocket(final HttpClient client, final JsonRpcClient rpc, final String wsUrl, final String requestId) {
      return !this.isSameHttpClientSession(client) ? CompletableFuture.failedFuture(new IllegalStateException("Signaling torn down before WebSocket open")) : client.newWebSocketBuilder().header("x-mojangauth", this.user.getAccessToken()).header("Session-Id", this.sessionId).header("Request-Id", requestId).buildAsync(URI.create(wsUrl), rpc).thenComposeAsync((webSocket) -> {
         if (this.isSameHttpClientSession(client)) {
            this.schedulePing(rpc);
            return CompletableFuture.completedFuture(rpc);
         } else {
            webSocket.abort();
            return CompletableFuture.failedFuture(new IllegalStateException("Stale signaling WebSocket connection"));
         }
      }, this.executor);
   }

   private void schedulePing(final JsonRpcClient rpc) {
      this.pingTask = this.executor.scheduleAtFixedRate(() -> {
         try {
            rpc.sendNotification("System_Ping_v1_0");
         } catch (RuntimeException e) {
            LOGGER.warn("Signaling ping failed", e);
         }

      }, PING_INTERVAL.toMillis(), PING_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
   }

   private boolean isSameHttpClientSession(final HttpClient client) {
      return this.httpClient == client;
   }

   private CompletableFuture<RTCIceServer> refreshTurnAuth() {
      return this.sendRequest("Signaling_TurnAuth_v1_0", List.of()).exceptionallyCompose((error) -> {
         Throwable patt0$temp = error.getCause();
         Object var10000;
         if (patt0$temp instanceof JsonRpcException jre) {
            var10000 = new SignalingException.TurnAuthFailedException(jre.serverMessage());
         } else {
            var10000 = error;
         }

         return CompletableFuture.failedFuture((Throwable)var10000);
      }).thenApplyAsync((result) -> {
         TurnAuthResult turnAuth = (TurnAuthResult)SignalingServiceClient.TurnAuthResult.CODEC.parse(JsonOps.INSTANCE, result).getOrThrow((s) -> new IllegalStateException("Malformed TurnAuth response: " + s));
         RTCIceServer ice = turnAuth.toRtcIceServer();
         this.cachedTurn = new CachedTurn(turnAuth);
         return ice;
      }, this.executor);
   }

   private CompletableFuture<JsonElement> sendRequest(final String method, final List<JsonElement> params) {
      return this.websocketConnect == null ? CompletableFuture.failedFuture(new IllegalStateException("Signaling is not connected; call connect() first")) : this.websocketConnect.thenCompose((r) -> r.sendRequest(method, params));
   }

   private void onRpcMethod(final JsonRpcClient rpc, final @Nullable JsonElement id, final String method, final @Nullable JsonElement params) {
      switch (method) {
         case "System_Pong_v1_0":
            break;
         case "Signaling_ReceiveMessage_v1_0":
            this.handleReceiveMessage(rpc, id, params);
            break;
         default:
            if (id != null) {
               rpc.sendError(id, JsonRPCErrors.METHOD_NOT_FOUND, method);
            }
      }

   }

   private void handleReceiveMessage(final JsonRpcClient rpc, final @Nullable JsonElement id, final @Nullable JsonElement params) {
      JsonArray arr = params != null && params.isJsonArray() ? params.getAsJsonArray() : null;
      JsonElement first = arr != null && !arr.isEmpty() ? arr.get(0) : null;
      if (first != null && first.isJsonObject()) {
         if (id != null) {
            rpc.sendResponse(id, new JsonObject());
         }

         ClientWebRtcMessage msg;
         try {
            msg = (ClientWebRtcMessage)SignalingServiceClient.ClientWebRtcMessage.CODEC.parse(JsonOps.INSTANCE, first).getOrThrow(IllegalStateException::new);
         } catch (RuntimeException e) {
            LOGGER.warn("Malformed ReceiveMessage envelope: {}", e.getMessage());
            return;
         }

         JsonElement inner;
         try {
            inner = JsonParser.parseString(msg.message());
         } catch (JsonSyntaxException e) {
            LOGGER.warn("Dropping non-JSON signaling payload: {}", e.getMessage());
            return;
         }

         SignalingException serviceError = SignalingErrorMapper.fromServiceEnvelope(inner);
         if (serviceError != null) {
            LOGGER.debug("Signaling service reported error: {}", serviceError.getMessage());
            UUID errorPmid = serviceError.peerPmid() != null ? serviceError.peerPmid() : UUID.fromString(msg.from());
            this.fireListeners((l) -> l.onSignalingError(errorPmid, serviceError));
         } else {
            SignalingMessage parsed;
            try {
               parsed = (SignalingMessage)SignalingMessage.CODEC.parse(JsonOps.INSTANCE, inner).getOrThrow(IllegalStateException::new);
            } catch (RuntimeException e) {
               LOGGER.warn("Malformed signaling payload: {}", e.getMessage());
               return;
            }

            UUID fromPmid = UUID.fromString(msg.from());
            SignalingMessage.Payload var11 = parsed.decode();
            byte var12 = 0;
            //$FF: var12->value
            //0->net/minecraft/client/multiplayer/p2p/SignalingMessage$FriendJoin
            //1->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc
            switch (var11.typeSwitch<invokedynamic>(var11, var12)) {
               case -1:
                  LOGGER.debug("Ignoring malformed signaling message of type {}", parsed.type());
                  break;
               case 0:
                  SignalingMessage.FriendJoin friendJoin = (SignalingMessage.FriendJoin)var11;
                  this.dispatchFriendJoinMessage(fromPmid, friendJoin);
                  break;
               case 1:
                  SignalingMessage.WebRtc webRtc = (SignalingMessage.WebRtc)var11;
                  this.dispatchWebRtcMessage(fromPmid, webRtc);
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

         }
      } else {
         LOGGER.warn("Malformed ReceiveMessage params: {}", params);
         if (id != null) {
            rpc.sendError(id, JsonRPCErrors.INVALID_PARAMS, "Expected [object] params");
         }

      }
   }

   private void dispatchFriendJoinMessage(final UUID fromPmid, final SignalingMessage.FriendJoin message) {
      FriendJoinHandler handler = this.friendJoinHandler;
      if (handler != null) {
         try {
            handler.handle(fromPmid, message);
         } catch (RuntimeException e) {
            LOGGER.error("Failed to dispatch FriendJoin", e);
         }

      }
   }

   private void dispatchWebRtcMessage(final UUID fromPmid, final SignalingMessage.WebRtc message) {
      WebRtcSignalingHandler handler = this.webRtcSignalingHandler;
      if (handler != null) {
         try {
            handler.handle(fromPmid, message);
         } catch (RuntimeException e) {
            LOGGER.error("Failed to dispatch WebRtc", e);
         }

      }
   }

   static {
      SIGNALING_URI_CODEC = Codec.STRING.fieldOf("signalingUri").codec().fieldOf("result").codec();
      PING_INTERVAL = Duration.ofSeconds(50L);
      ENVIRONMENT = (Environment)Optional.ofNullable(System.getenv("signaling.environment")).or(() -> Optional.ofNullable(System.getProperty("signaling.environment"))).flatMap(Environment::byName).orElse(SignalingServiceClient.Environment.PRODUCTION);
   }

   public interface ConnectionListener {
      default void onSignalingError(final @Nullable UUID peerPmid, final SignalingException cause) {
      }

      default void onSignalingDisconnected() {
      }
   }

   private static record CachedSignalingUri(String wsUrl, Instant expiresAt) {
      private static final Duration TTL = Duration.ofMinutes(5L);

      private CachedSignalingUri(final String wsUrl) {
         this(wsUrl, Instant.now().plus(TTL));
      }

      private CachedSignalingUri {
         super();
      }

      private boolean isUsable() {
         return Instant.now().isBefore(this.expiresAt);
      }
   }

   private static record CachedTurn(TurnAuthResult turnAuth, Instant expiresAt) {
      private static final Duration EXPIRY_MARGIN = Duration.ofSeconds(60L);

      private CachedTurn(final TurnAuthResult turnAuth) {
         this(turnAuth, Instant.now().plusSeconds(turnAuth.expirationInSeconds()));
      }

      private CachedTurn {
         super();
      }

      private boolean isUsable() {
         return Instant.now().isBefore(this.expiresAt.minus(EXPIRY_MARGIN));
      }
   }

   private static record TurnAuthResult(long expirationInSeconds, List<TurnAuthServer> turnAuthServers) {
      private static final Codec<TurnAuthResult> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.LONG.fieldOf("ExpirationInSeconds").forGetter(TurnAuthResult::expirationInSeconds), SignalingServiceClient.TurnAuthServer.CODEC.listOf().fieldOf("TurnAuthServers").forGetter(TurnAuthResult::turnAuthServers)).apply(i, TurnAuthResult::new));

      private TurnAuthResult {
         super();
      }

      private RTCIceServer toRtcIceServer() {
         TurnAuthServer first = (TurnAuthServer)this.turnAuthServers.getFirst();
         RTCIceServer ice = new RTCIceServer();
         ice.username = first.username();
         ice.password = first.password();

         for(TurnAuthServer s : this.turnAuthServers) {
            ice.urls.addAll(s.urls());
         }

         return ice;
      }
   }

   private static record TurnAuthServer(String username, String password, List<String> urls) {
      private static final Codec<TurnAuthServer> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("Username").forGetter(TurnAuthServer::username), Codec.STRING.fieldOf("Password").forGetter(TurnAuthServer::password), Codec.STRING.listOf().fieldOf("Urls").forGetter(TurnAuthServer::urls)).apply(i, TurnAuthServer::new));

      private TurnAuthServer {
         super();
      }
   }

   private static record ClientWebRtcMessage(String from, String message, @Nullable UUID id) {
      private static final Codec<ClientWebRtcMessage> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("From").forGetter(ClientWebRtcMessage::from), Codec.STRING.fieldOf("Message").forGetter(ClientWebRtcMessage::message), UUIDUtil.STRING_CODEC.optionalFieldOf("Id").forGetter((c) -> Optional.ofNullable(c.id()))).apply(i, (from, msg, id) -> new ClientWebRtcMessage(from, msg, (UUID)id.orElse((Object)null))));

      private ClientWebRtcMessage {
         super();
      }
   }

   private static enum Environment {
      STAGE("https://signaling-afd.stage-6fd5f759.franchise.minecraft-services.net"),
      PRODUCTION("https://signaling-afd.franchise.minecraft-services.net");

      private static final String CONFIGURATION_ENDPOINT = "/api/v1.0/configuration/java";
      private final String baseUrl;

      private Environment(final String baseUrl) {
         this.baseUrl = baseUrl;
      }

      private static Optional<Environment> byName(final String name) {
         Optional var10000;
         switch (name.toLowerCase(Locale.ROOT)) {
            case "stage":
            case "staging":
               var10000 = Optional.of(STAGE);
               break;
            case "prod":
            case "production":
               var10000 = Optional.of(PRODUCTION);
               break;
            default:
               var10000 = Optional.empty();
         }

         return var10000;
      }

      private String getConfigurationUri() {
         return this.baseUrl + "/api/v1.0/configuration/java";
      }

      // $FF: synthetic method
      private static Environment[] $values() {
         return new Environment[]{STAGE, PRODUCTION};
      }
   }

   private static final class RpcMethods {
      private static final String PING = "System_Ping_v1_0";
      private static final String PONG = "System_Pong_v1_0";
      private static final String TURN_AUTH = "Signaling_TurnAuth_v1_0";
      private static final String SEND_CLIENT_MSG = "Signaling_SendClientMessage_v1_0";
      private static final String RECEIVE_MESSAGE = "Signaling_ReceiveMessage_v1_0";

      private RpcMethods() {
         super();
      }
   }

   @FunctionalInterface
   public interface FriendJoinHandler {
      void handle(UUID fromPmid, SignalingMessage.FriendJoin message);
   }

   @FunctionalInterface
   public interface WebRtcSignalingHandler {
      void handle(UUID fromPmid, SignalingMessage.WebRtc message);
   }
}
