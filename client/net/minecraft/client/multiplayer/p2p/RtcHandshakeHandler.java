package net.minecraft.client.multiplayer.p2p;

import com.mojang.logging.LogUtils;
import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.RTCConfiguration;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCIceServer;
import dev.onvoid.webrtc.logging.LogSink;
import dev.onvoid.webrtc.logging.Logging;
import dev.onvoid.webrtc.logging.Logging.Severity;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.p2p.client.SignalingServiceClient;
import net.minecraft.client.network.webrtc.RtcChannel;
import net.minecraft.client.network.webrtc.RtcHandshake;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.telemetry.events.P2PTelemetryEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

final class RtcHandshakeHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long HANDSHAKE_TIMEOUT_MS;
   private final Minecraft minecraft;
   private final SignalingServiceClient signaling;
   private final P2PManager manager;
   private final ConcurrentHashMap<UUID, RtcHandshake> handshakes = new ConcurrentHashMap();
   private @Nullable PeerConnectionFactory factory;
   private final SignalingServiceClient.ConnectionListener connectionListener = new SignalingServiceClient.ConnectionListener() {
      {
         Objects.requireNonNull(RtcHandshakeHandler.this);
      }

      public void onSignalingError(final @Nullable UUID peerPmid, final SignalingException cause) {
         if (peerPmid == null) {
            RtcHandshakeHandler.LOGGER.debug("Signaling error", cause);
         } else {
            RtcHandshake handshake = RtcHandshakeHandler.this.getHandshake(peerPmid);
            if (handshake != null) {
               handshake.abort("signaling error: " + cause.getClass().getSimpleName());
            }

         }
      }
   };

   public RtcHandshakeHandler(final Minecraft minecraft, final SignalingServiceClient signaling, final P2PManager manager) {
      super();
      this.minecraft = minecraft;
      this.signaling = signaling;
      this.manager = manager;
      signaling.setWebRtcSignalingHandler(this::handleWebRtc);
      signaling.addConnectionListener(this.connectionListener);
   }

   private PeerConnectionFactory getPeerConnectionFactory() {
      if (this.factory == null) {
         if (SharedConstants.DEBUG_NATIVE_WEBRTC_LOGS) {
            Logging.addLogSink(Severity.INFO, new WebRtcLogSink());
         }

         this.factory = new PeerConnectionFactory();
      }

      return this.factory;
   }

   public boolean hasHandshake(final UUID peerPmid) {
      return this.handshakes.containsKey(peerPmid);
   }

   public @Nullable RtcHandshake getHandshake(final UUID peerPmid) {
      return (RtcHandshake)this.handshakes.get(peerPmid);
   }

   public CompletableFuture<@Nullable Void> startHandshake(final UUID peerPmid, final String sessionId) {
      return this.startHandshake(peerPmid, sessionId, true, (handshake) -> handshake.createOffer().thenApply((sdp) -> SignalingMessage.offer(handshake.id(), sdp)));
   }

   public void closeHostHandshakes() {
      this.handshakes.values().forEach((h) -> {
         if (!h.isInitiator()) {
            h.abort("Host stopped");
         }

      });
   }

   public void cancelInitiatorHandshakes() {
      this.handshakes.values().forEach((h) -> {
         if (h.isInitiator()) {
            h.abort("handshake cancelled");
         }

      });
   }

   public synchronized void shutdown() {
      this.signaling.removeConnectionListener(this.connectionListener);
      this.handshakes.values().forEach((handshake) -> handshake.abort("shutdown"));
      this.handshakes.clear();
      if (this.factory != null) {
         this.factory.dispose();
         this.factory = null;
      }

   }

   private void handleWebRtc(final UUID fromPmid, final SignalingMessage.WebRtc msg) {
      Objects.requireNonNull(msg);
      byte var4 = 0;
      //$FF: var4->value
      //0->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$Offer
      //1->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$Answer
      //2->net/minecraft/client/multiplayer/p2p/SignalingMessage$WebRtc$IceCandidate
      switch (msg.typeSwitch<invokedynamic>(msg, var4)) {
         case 0:
            SignalingMessage.WebRtc.Offer offer = (SignalingMessage.WebRtc.Offer)msg;
            this.handleOffer(fromPmid, offer);
            break;
         case 1:
            SignalingMessage.WebRtc.Answer answer = (SignalingMessage.WebRtc.Answer)msg;
            this.handleAnswer(fromPmid, answer);
            break;
         case 2:
            SignalingMessage.WebRtc.IceCandidate ice = (SignalingMessage.WebRtc.IceCandidate)msg;
            this.handleIceCandidate(fromPmid, ice);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

   }

   private void handleOffer(final UUID fromPmid, final SignalingMessage.WebRtc.Offer msg) {
      if (!this.manager.isHostingOnline()) {
         LOGGER.debug("Ignoring WebRTC offer (not hosting)");
      } else if (!this.minecraft.getPlayerSocialManager().isFriendsPmid(fromPmid)) {
         LOGGER.debug("Ignoring WebRTC offer (not a friend)");
      } else if (!this.manager.consumeAcceptedJoinRequest(fromPmid, msg.sessionId())) {
         LOGGER.debug("Ignoring WebRTC offer for session={} (join request was not accepted)", msg.sessionId());
      } else {
         this.startHandshake(fromPmid, msg.sessionId(), false, (handshake) -> handshake.acceptOffer(msg.sdp()).thenApply((sdp) -> SignalingMessage.answer(handshake.id(), sdp))).exceptionally((var0) -> null);
      }
   }

   private void handleAnswer(final UUID fromPmid, final SignalingMessage.WebRtc.Answer msg) {
      RtcHandshake existing = this.getHandshake(fromPmid);
      if (existing != null && existing.isInitiator()) {
         if (!existing.id().equals(msg.sessionId())) {
            LOGGER.debug("Ignoring stale WebRTC answer for session={} (current={})", msg.sessionId(), existing.id());
         } else {
            existing.applyAnswer(msg.sdp()).exceptionally((err) -> {
               existing.abort("answer failed: " + err.getMessage());
               return null;
            });
         }
      } else {
         LOGGER.debug("Ignoring WebRTC answer for session={} (no initiator handshake)", msg.sessionId());
      }
   }

   private void handleIceCandidate(final UUID fromPmid, final SignalingMessage.WebRtc.IceCandidate msg) {
      RtcHandshake handshake = this.getHandshake(fromPmid);
      if (handshake == null) {
         LOGGER.trace("Dropping ICE candidate for session={} (no handshake)", msg.sessionId());
      } else if (!handshake.id().equals(msg.sessionId())) {
         LOGGER.trace("Dropping stale ICE candidate for session={} (current={})", msg.sessionId(), handshake.id());
      } else {
         RTCIceCandidate candidate = msg.toRtcIceCandidate();
         handshake.addRemoteIceCandidate(candidate).exceptionally((err) -> {
            LOGGER.warn("Failed to add remote ICE candidate for session={}", msg.sessionId(), err);
            return null;
         });
      }
   }

   private CompletableFuture<Void> startHandshake(final UUID peerPmid, final String sessionId, final boolean initiator, final Function<RtcHandshake, CompletableFuture<SignalingMessage>> sdpOp) {
      if (this.handshakes.containsKey(peerPmid)) {
         return CompletableFuture.failedFuture(new IllegalStateException("Handshake already in progress"));
      } else {
         CompletableFuture<Void> result = new CompletableFuture();
         Instant attemptStart = Instant.now();
         AtomicReference<Instant> signalingDoneAt = new AtomicReference();
         P2PTelemetryEvent.State telemetry = new P2PTelemetryEvent.State();
         this.signaling.requestTurnAuth().thenCompose((turnAuth) -> {
            RtcHandshake handshake = this.createHandshake(peerPmid, sessionId, initiator, turnAuth, result, telemetry);
            if (handshake == null) {
               result.completeExceptionally(new IllegalStateException("Failed to establish P2P handshake"));
               return CompletableFuture.completedFuture((Object)null);
            } else {
               if (initiator) {
                  result.whenComplete((var3, error) -> P2PTelemetryEvent.INSTANCE.send(error == null, telemetry, attemptStart, (Instant)signalingDoneAt.get(), Instant.now()));
               }

               return ((CompletableFuture)sdpOp.apply(handshake)).thenCompose((sdpMsg) -> this.signaling.sendClientMessage(peerPmid, sdpMsg)).whenComplete((var4, error) -> {
                  if (error != null) {
                     telemetry.setFailureStage(P2PTelemetryEvent.FailureStage.SIGNALING);
                     handshake.abort("SDP exchange failed: " + error.getMessage());
                     result.completeExceptionally(error);
                  } else {
                     signalingDoneAt.set(Instant.now());
                  }

               });
            }
         }).whenComplete((var3, error) -> {
            if (error != null) {
               LOGGER.warn("WebRTC handshake failed for session={}", sessionId, error);
               telemetry.setFailureStage(P2PTelemetryEvent.FailureStage.SIGNALING);
               result.completeExceptionally(error);
            }

         });
         return result;
      }
   }

   private @Nullable RtcHandshake createHandshake(final UUID peerPmid, final String sessionId, final boolean initiator, final RTCIceServer turnAuth, final CompletableFuture<@Nullable Void> result, final P2PTelemetryEvent.State telemetry) {
      RTCConfiguration config = new RTCConfiguration();
      config.iceServers.add(turnAuth);
      config.portAllocatorConfig.setEnableIpv6(true).setEnableIpv6OnWifi(true);
      RtcHandshake handshake;
      synchronized(this) {
         handshake = new RtcHandshake(this.getPeerConnectionFactory(), config, sessionId, initiator, (candidate) -> this.signaling.sendClientMessage(peerPmid, SignalingMessage.iceCandidate(sessionId, candidate)).exceptionally((err) -> {
               LOGGER.debug("Failed to send ICE candidate for session={}", sessionId, err);
               return null;
            }));
         if (this.handshakes.putIfAbsent(peerPmid, handshake) != null) {
            handshake.abort("Duplicate");
            return null;
         }
      }

      CompletableFuture.delayedExecutor(HANDSHAKE_TIMEOUT_MS, TimeUnit.MILLISECONDS).execute(() -> {
         if (!result.isDone()) {
            telemetry.setFailureStage(P2PTelemetryEvent.FailureStage.TIMEOUT);
            handshake.abort("Handshake timeout");
         }
      });
      handshake.onIceInfo((info) -> telemetry.setIceInfo(info.local(), info.remote()));
      handshake.future().whenComplete((handshakeResult, err) -> {
         this.handshakes.remove(peerPmid, handshake);
         this.manager.notifyJoinStateChanged();
         if (err != null) {
            if (!result.isDone()) {
               telemetry.setFailureStage(P2PTelemetryEvent.FailureStage.ICE_CONNECT);
               result.completeExceptionally(err);
            }

         } else if (!result.complete((Object)null)) {
            RtcChannel.dispose(handshakeResult);
         } else {
            if (handshake.isInitiator()) {
               this.joinHost(handshakeResult);
            } else {
               UUID profileId = this.minecraft.getPlayerSocialManager().getPresenceHandler().getProfileIdFromPmid(peerPmid);
               if (profileId == null) {
                  handshake.abort("No profile ID for peer");
                  return;
               }

               this.acceptGuest(handshakeResult, profileId);
            }

         }
      });
      return handshake;
   }

   private void joinHost(final RtcHandshake.HandshakeResult handshakeResult) {
      this.minecraft.execute(() -> {
         if (this.minecraft.level != null || this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.disconnectWithProgressScreen(false);
         }

         Connection connection = Connection.fromChannel(new RtcChannel(handshakeResult), PacketFlow.CLIENTBOUND, this.minecraft.getDebugOverlay().getBandwidthLogger());
         connection.initiateServerboundPlayConnection("rtc-peer", 0, LoginProtocols.SERVERBOUND, LoginProtocols.CLIENTBOUND, new ClientHandshakePacketListenerImpl(connection, this.minecraft, new ServerData("Online", "rtc-peer", ServerData.Type.ONLINE), (Screen)null, false, (Duration)null, (var0) -> {
         }, new LevelLoadTracker(), (TransferState)null), false);
         connection.send(new ServerboundHelloPacket(this.minecraft.getUser().getName(), this.minecraft.getUser().getProfileId()));
         this.minecraft.setPendingConnection(connection);
      });
   }

   private void acceptGuest(final RtcHandshake.HandshakeResult handshakeResult, final UUID profileId) {
      IntegratedServer server = this.minecraft.getSingleplayerServer();
      if (server == null) {
         RtcChannel.dispose(handshakeResult);
      } else {
         server.execute(() -> {
            if (server.isPublishedOnline()) {
               server.getConnection().acceptChannel(new RtcChannel(handshakeResult), profileId);
            } else {
               RtcChannel.dispose(handshakeResult);
            }

         });
      }
   }

   static {
      HANDSHAKE_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(30L);
   }

   private static final class WebRtcLogSink implements LogSink {
      private static final Logger LOGGER = LogUtils.getLogger();

      private WebRtcLogSink() {
         super();
      }

      public void onLogMessage(final Logging.Severity severity, final String message) {
         switch (severity) {
            case VERBOSE:
               LOGGER.trace(message);
               break;
            case INFO:
               LOGGER.info(message);
               break;
            case WARNING:
               LOGGER.warn(message);
               break;
            case ERROR:
               LOGGER.error(message);
            case NONE:
         }

      }
   }
}
