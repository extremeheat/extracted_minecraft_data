package net.minecraft.client.network.webrtc;

import com.mojang.logging.LogUtils;
import dev.onvoid.webrtc.CreateSessionDescriptionObserver;
import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.PeerConnectionObserver;
import dev.onvoid.webrtc.RTCAnswerOptions;
import dev.onvoid.webrtc.RTCConfiguration;
import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCDataChannelBuffer;
import dev.onvoid.webrtc.RTCDataChannelInit;
import dev.onvoid.webrtc.RTCDataChannelObserver;
import dev.onvoid.webrtc.RTCDataChannelState;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCIceConnectionState;
import dev.onvoid.webrtc.RTCIceGatheringState;
import dev.onvoid.webrtc.RTCOfferOptions;
import dev.onvoid.webrtc.RTCPeerConnection;
import dev.onvoid.webrtc.RTCPeerConnectionIceErrorEvent;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import dev.onvoid.webrtc.RTCPriorityType;
import dev.onvoid.webrtc.RTCSdpType;
import dev.onvoid.webrtc.RTCSessionDescription;
import dev.onvoid.webrtc.RTCSignalingState;
import dev.onvoid.webrtc.RTCStats;
import dev.onvoid.webrtc.RTCStatsType;
import dev.onvoid.webrtc.SetSessionDescriptionObserver;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.minecraft.client.telemetry.events.P2PTelemetryEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class RtcHandshake {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String id;
   private final RTCPeerConnection peerConnection;
   private final boolean trickleIce;
   private final boolean initiator;
   private final Consumer<RTCIceCandidate> onLocalCandidate;
   private final CompletableFuture<HandshakeResult> result = new CompletableFuture();
   private final AtomicBoolean started = new AtomicBoolean();
   private final AtomicBoolean handedOff = new AtomicBoolean();
   private volatile @Nullable CompletableFuture<String> sdpResult;
   private volatile @Nullable RTCDataChannel dataChannel;
   private volatile @Nullable Consumer<IceInfo> onIceInfo;

   public RtcHandshake(final PeerConnectionFactory factory, final RTCConfiguration configuration, final String id, final boolean initiator, final Consumer<RTCIceCandidate> onLocalCandidate) {
      super();
      this.id = id;
      this.initiator = initiator;
      this.trickleIce = true;
      this.onLocalCandidate = onLocalCandidate;
      this.peerConnection = factory.createPeerConnection(configuration, new SessionObserver());
   }

   public String id() {
      return this.id;
   }

   public boolean isInitiator() {
      return this.initiator;
   }

   public CompletableFuture<HandshakeResult> future() {
      return this.result;
   }

   public void onIceInfo(final Consumer<IceInfo> cb) {
      this.onIceInfo = cb;
   }

   public void abort(final String reason) {
      this.failHandshake(reason);
   }

   private void markOpen(final RTCDataChannel dc) {
      if (!this.result.isDone()) {
         this.handedOff.set(true);
         if (!this.result.complete(new HandshakeResult(this.peerConnection, dc))) {
            this.handedOff.set(false);
         } else {
            try {
               dc.unregisterObserver();
            } catch (RuntimeException e) {
               LOGGER.warn("[P2P][{}] dataChannel.unregisterObserver at handoff threw: {}", this.id, e.getMessage());
            }

            LOGGER.info("[P2P][{}] handshake complete; peerConnection + dataChannel handed off", this.id);
         }
      }
   }

   private void failHandshake(final String reason) {
      Throwable failure = new CancellationException("Handshake " + this.id + " aborted: " + reason);
      if (this.result.completeExceptionally(failure)) {
         LOGGER.info("[P2P][{}] handshake aborted: {}", this.id, reason);
         CompletableFuture<String> pending = this.sdpResult;
         if (pending != null) {
            pending.completeExceptionally(failure);
         }

         if (!this.handedOff.get()) {
            RtcChannel.dispose(this.peerConnection, this.dataChannel);
         }
      }
   }

   private void wireDataChannel(final RTCDataChannel dc) {
      this.dataChannel = dc;
      dc.registerObserver(new RTCDataChannelObserver() {
         {
            Objects.requireNonNull(RtcHandshake.this);
         }

         public void onStateChange() {
            RTCDataChannelState state = dc.getState();
            RtcHandshake.LOGGER.info("[P2P][{}] DataChannel \u2192 {}", RtcHandshake.this.id, state);
            if (state == RTCDataChannelState.OPEN) {
               RtcHandshake.this.markOpen(dc);
            } else if (state == RTCDataChannelState.CLOSING || state == RTCDataChannelState.CLOSED) {
               RtcHandshake.this.failHandshake("Data channel " + String.valueOf(state));
            }

         }

         public void onMessage(final RTCDataChannelBuffer buf) {
         }

         public void onBufferedAmountChange(final long prev) {
         }
      });
      if (dc.getState() == RTCDataChannelState.OPEN) {
         this.markOpen(dc);
      }

   }

   public CompletableFuture<@Nullable Void> applyAnswer(final String answerSdp) {
      if (this.result.isDone()) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         RTCSignalingState signalingState;
         try {
            signalingState = this.peerConnection.getSignalingState();
         } catch (RuntimeException e) {
            if (this.result.isDone()) {
               return CompletableFuture.completedFuture((Object)null);
            }

            return CompletableFuture.failedFuture(e);
         }

         if (signalingState == RTCSignalingState.STABLE) {
            LOGGER.debug("[P2P][{}] ignoring duplicate answer", this.id);
            return CompletableFuture.completedFuture((Object)null);
         } else {
            return this.setRemoteDescription(new RTCSessionDescription(RTCSdpType.ANSWER, answerSdp));
         }
      }
   }

   public CompletableFuture<@Nullable Void> addRemoteIceCandidate(final RTCIceCandidate candidate) {
      if (this.result.isDone()) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         try {
            this.peerConnection.addIceCandidate(candidate);
            return CompletableFuture.completedFuture((Object)null);
         } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
         }
      }
   }

   public CompletableFuture<String> createOffer() {
      if (!this.started.compareAndSet(false, true)) {
         return CompletableFuture.failedFuture(new IllegalStateException("Cannot create offer after handshake has started"));
      } else {
         RTCDataChannelInit init = new RTCDataChannelInit();
         init.ordered = true;
         init.maxRetransmits = -1;
         init.priority = RTCPriorityType.HIGH;
         this.wireDataChannel(this.peerConnection.createDataChannel("minecraft", init));
         return this.startSdpExchange(this.createOfferSdp().thenCompose(this::setLocalDescription));
      }
   }

   public CompletableFuture<String> acceptOffer(final String offerSdp) {
      return !this.started.compareAndSet(false, true) ? CompletableFuture.failedFuture(new IllegalStateException("Cannot accept offer after handshake has started")) : this.startSdpExchange(this.setRemoteDescription(new RTCSessionDescription(RTCSdpType.OFFER, offerSdp)).thenCompose((var1) -> this.createAnswerSdp()).thenCompose(this::setLocalDescription));
   }

   private CompletableFuture<Void> setRemoteDescription(final RTCSessionDescription desc) {
      final CompletableFuture<Void> future = new CompletableFuture();
      this.peerConnection.setRemoteDescription(desc, new SetSessionDescriptionObserver() {
         {
            Objects.requireNonNull(RtcHandshake.this);
         }

         public void onSuccess() {
            future.complete((Object)null);
         }

         public void onFailure(final String err) {
            future.completeExceptionally(new RuntimeException("setRemoteDescription: " + err));
         }
      });
      return future;
   }

   private CompletableFuture<Void> setLocalDescription(final RTCSessionDescription desc) {
      final CompletableFuture<Void> future = new CompletableFuture();
      this.peerConnection.setLocalDescription(desc, new SetSessionDescriptionObserver() {
         {
            Objects.requireNonNull(RtcHandshake.this);
         }

         public void onSuccess() {
            future.complete((Object)null);
         }

         public void onFailure(final String err) {
            future.completeExceptionally(new RuntimeException("setLocalDescription: " + err));
         }
      });
      return future;
   }

   private CompletableFuture<RTCSessionDescription> createOfferSdp() {
      final CompletableFuture<RTCSessionDescription> future = new CompletableFuture();
      this.peerConnection.createOffer(new RTCOfferOptions(), new CreateSessionDescriptionObserver() {
         {
            Objects.requireNonNull(RtcHandshake.this);
         }

         public void onSuccess(final RTCSessionDescription desc) {
            future.complete(desc);
         }

         public void onFailure(final String err) {
            future.completeExceptionally(new RuntimeException("createOffer: " + err));
         }
      });
      return future;
   }

   private CompletableFuture<RTCSessionDescription> createAnswerSdp() {
      final CompletableFuture<RTCSessionDescription> future = new CompletableFuture();
      this.peerConnection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {
         {
            Objects.requireNonNull(RtcHandshake.this);
         }

         public void onSuccess(final RTCSessionDescription desc) {
            future.complete(desc);
         }

         public void onFailure(final String err) {
            future.completeExceptionally(new RuntimeException("createAnswer: " + err));
         }
      });
      return future;
   }

   private CompletableFuture<String> startSdpExchange(final CompletableFuture<@Nullable Void> pipeline) {
      CompletableFuture<String> sdpFuture = new CompletableFuture();
      this.sdpResult = sdpFuture;
      pipeline.whenComplete((var2, err) -> {
         if (err != null) {
            sdpFuture.completeExceptionally(err);
         } else if (this.trickleIce) {
            this.completeSdp(sdpFuture);
         }

      });
      return sdpFuture.whenComplete((var1, err) -> {
         this.sdpResult = null;
         if (err != null) {
            LOGGER.warn("[P2P][{}] SDP exchange failed: {}", this.id, err.getMessage());
         }

      });
   }

   private void completeSdp(final CompletableFuture<String> sdp) {
      RTCSessionDescription local = this.peerConnection.getLocalDescription();
      if (local == null) {
         sdp.completeExceptionally(new IllegalStateException("local description missing after setLocalDescription"));
      } else {
         sdp.complete(local.sdp);
      }
   }

   private void fireLocalCandidate(final RTCIceCandidate candidate) {
      try {
         this.onLocalCandidate.accept(candidate);
      } catch (RuntimeException e) {
         LOGGER.warn("[P2P][{}] onLocalCandidate threw", this.id, e);
      }

   }

   private void fireIceInfo(final IceInfo info) {
      if (!this.result.isDone()) {
         Consumer<IceInfo> cb = this.onIceInfo;
         if (cb != null) {
            try {
               cb.accept(info);
            } catch (RuntimeException e) {
               LOGGER.warn("[P2P][{}] onIceInfo threw", this.id, e);
            }

         }
      }
   }

   private void reportIceInfo() {
      this.peerConnection.getStats((report) -> {
         if (!this.result.isDone()) {
            Map<String, RTCStats> all = report.getStats();
            Optional<RTCStats> nominatedPair = all.values().stream().filter((s) -> s.getType() == RTCStatsType.CANDIDATE_PAIR && Boolean.TRUE.equals(s.getAttributes().get("nominated"))).findFirst();
            if (nominatedPair.isEmpty()) {
               LOGGER.debug("[P2P][{}] selected ICE pair missing from stats", this.id);
            } else {
               Optional<IceInfo> info = this.extractIceInfo((RTCStats)nominatedPair.get(), all);
               if (info.isEmpty()) {
                  LOGGER.debug("[P2P][{}] selected ICE pair missing candidate details", this.id);
               } else {
                  LOGGER.info("[P2P][{}] selected ICE pair: {}/{}", new Object[]{this.id, ((IceInfo)info.get()).local(), ((IceInfo)info.get()).remote()});
                  this.fireIceInfo((IceInfo)info.get());
               }
            }
         }
      });
   }

   private Optional<IceInfo> extractIceInfo(final RTCStats pair, final Map<String, RTCStats> all) {
      Map<String, Object> attrs = pair.getAttributes();
      RTCStats local = (RTCStats)all.get(String.valueOf(attrs.get("localCandidateId")));
      RTCStats remote = (RTCStats)all.get(String.valueOf(attrs.get("remoteCandidateId")));
      if (local != null && remote != null) {
         Object localTypeObj = local.getAttributes().get("candidateType");
         Object remoteTypeObj = remote.getAttributes().get("candidateType");
         if (localTypeObj != null && remoteTypeObj != null) {
            Optional<P2PTelemetryEvent.IceCandidateType> localType = P2PTelemetryEvent.IceCandidateType.byName(String.valueOf(localTypeObj));
            Optional<P2PTelemetryEvent.IceCandidateType> remoteType = P2PTelemetryEvent.IceCandidateType.byName(String.valueOf(remoteTypeObj));
            if (!localType.isEmpty() && !remoteType.isEmpty()) {
               return Optional.of(new IceInfo((P2PTelemetryEvent.IceCandidateType)localType.get(), (P2PTelemetryEvent.IceCandidateType)remoteType.get()));
            } else {
               LOGGER.debug("[P2P][{}] unknown ICE candidate type local={} remote={}", new Object[]{this.id, localTypeObj, remoteTypeObj});
               return Optional.empty();
            }
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   public static record IceInfo(P2PTelemetryEvent.IceCandidateType local, P2PTelemetryEvent.IceCandidateType remote) {
      public IceInfo {
         super();
      }
   }

   public static record HandshakeResult(RTCPeerConnection peerConnection, RTCDataChannel dataChannel) {
      public HandshakeResult {
         super();
      }
   }

   private final class SessionObserver implements PeerConnectionObserver {
      private SessionObserver() {
         Objects.requireNonNull(RtcHandshake.this);
         super();
      }

      public void onSignalingChange(final RTCSignalingState s) {
         RtcHandshake.LOGGER.debug("[P2P][{}] signaling \u2192 {}", RtcHandshake.this.id, s);
      }

      public void onIceConnectionChange(final RTCIceConnectionState s) {
         RtcHandshake.LOGGER.debug("[P2P][{}] ICE \u2192 {}", RtcHandshake.this.id, s);
      }

      public void onIceCandidate(final RTCIceCandidate c) {
         if (!RtcHandshake.this.result.isDone()) {
            if (RtcHandshake.this.trickleIce) {
               RtcHandshake.this.fireLocalCandidate(c);
            }

         }
      }

      public void onIceCandidateError(final RTCPeerConnectionIceErrorEvent e) {
         RtcHandshake.LOGGER.warn("[P2P][{}] ICE error: url={} code={} text={}", new Object[]{RtcHandshake.this.id, e.getUrl(), e.getErrorCode(), e.getErrorText()});
      }

      public void onConnectionChange(final RTCPeerConnectionState state) {
         RtcHandshake.LOGGER.info("[P2P][{}] connection \u2192 {}", RtcHandshake.this.id, state);
         switch (state) {
            case CONNECTED -> RtcHandshake.this.reportIceInfo();
            case FAILED -> RtcHandshake.this.failHandshake("connection FAILED");
            case CLOSED -> RtcHandshake.this.failHandshake("connection CLOSED");
         }

      }

      public void onIceGatheringChange(final RTCIceGatheringState state) {
         if (!RtcHandshake.this.trickleIce && state == RTCIceGatheringState.COMPLETE) {
            CompletableFuture<String> pending = RtcHandshake.this.sdpResult;
            if (pending != null) {
               RtcHandshake.this.completeSdp(pending);
            }

         }
      }

      public void onDataChannel(final RTCDataChannel dc) {
         RtcHandshake.LOGGER.info("[P2P][{}] DataChannel received (state={})", RtcHandshake.this.id, dc.getState());
         RtcHandshake.this.wireDataChannel(dc);
      }
   }
}
