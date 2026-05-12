package net.minecraft.client.multiplayer.p2p;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.p2p.client.SignalingServiceClient;
import net.minecraft.client.network.webrtc.RtcHandshake;
import net.minecraft.client.server.IntegratedServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class P2PManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long SIGNALING_RECONNECT_DELAY_SECONDS = 1L;
   private final Minecraft minecraft;
   private final SignalingServiceClient signaling;
   private final FriendJoinHandler friendJoinHandler;
   private final RtcHandshakeHandler rtcHandshakeHandler;
   private volatile boolean shutdown;

   public P2PManager(final Minecraft minecraft, final User user) {
      super();
      this.minecraft = minecraft;
      this.signaling = new SignalingServiceClient(user);
      this.friendJoinHandler = new FriendJoinHandler(minecraft, this.signaling, this);
      this.rtcHandshakeHandler = new RtcHandshakeHandler(minecraft, this.signaling, this);
      this.signaling.addConnectionListener(new SignalingServiceClient.ConnectionListener() {
         {
            Objects.requireNonNull(P2PManager.this);
         }

         public void onSignalingDisconnected() {
            P2PManager.this.onSignalingDisconnected();
         }
      });
   }

   private void onSignalingDisconnected() {
      if (!this.shutdown && this.needsSignaling()) {
         LOGGER.warn("Signaling disconnected while still needed, scheduling reconnect");
         CompletableFuture.delayedExecutor(1L, TimeUnit.SECONDS).execute(() -> {
            if (!this.shutdown && this.needsSignaling()) {
               this.ensureSignalingConnected();
            }

         });
      }
   }

   private boolean needsSignaling() {
      return this.isHostingP2P() || this.friendJoinHandler.hasOutgoingJoinRequests();
   }

   public boolean isHostingP2P() {
      IntegratedServer server = this.minecraft.getSingleplayerServer();
      return server != null && server.isPublishedOnline();
   }

   public void ensureSignalingConnected() {
      this.signaling.connect();
   }

   public void maybeDisconnectSignaling() {
      if (!this.needsSignaling()) {
         this.signaling.disconnect();
      }

   }

   public void onHostScopeChanged(final IntegratedServer.MultiplayerScope scope) {
      LOGGER.info("Host scope changed to {}", scope);
      if (this.isHostingP2P()) {
         this.ensureSignalingConnected();
      } else {
         this.teardownHostState();
      }
   }

   public void onHostServerStopping() {
      this.teardownHostState();
   }

   private void teardownHostState() {
      this.rtcHandshakeHandler.closeHostHandshakes();
      this.friendJoinHandler.clearHostJoinRequestState();
      this.maybeDisconnectSignaling();
   }

   public CompletableFuture<@Nullable Void> joinPlayer(final String peerPmid) {
      return this.friendJoinHandler.joinPlayer(peerPmid);
   }

   public void cancelOutgoingJoins() {
      this.friendJoinHandler.cancelOutgoingJoins();
      this.rtcHandshakeHandler.cancelInitiatorHandshakes();
      this.maybeDisconnectSignaling();
   }

   public boolean hasIncomingJoinRequest(final UUID peerPmid) {
      return this.friendJoinHandler.hasIncomingJoinRequest(peerPmid);
   }

   public boolean hasOutgoingJoinRequest() {
      return this.friendJoinHandler.hasOutgoingJoinRequests();
   }

   public FriendJoinHandler.OutgoingJoinState outgoingJoinState(final UUID peerPmid) {
      return this.friendJoinHandler.outgoingJoinState(peerPmid);
   }

   public @Nullable UUID connectingOutgoingJoinPmid() {
      return this.friendJoinHandler.connectingOutgoingJoinPmid();
   }

   public void acceptIncomingJoinRequest(final UUID peerPmid) {
      this.friendJoinHandler.acceptIncomingJoinRequest(peerPmid);
   }

   public void rejectIncomingJoinRequest(final UUID peerPmid) {
      this.friendJoinHandler.rejectIncomingJoinRequest(peerPmid);
   }

   public CompletableFuture<@Nullable Void> declineInvite(final UUID hostPmid) {
      return this.friendJoinHandler.declineInvite(hostPmid);
   }

   public void addJoinStateListener(final Runnable listener) {
      this.friendJoinHandler.addJoinStateListener(listener);
   }

   public void removeJoinStateListener(final Runnable listener) {
      this.friendJoinHandler.removeJoinStateListener(listener);
   }

   public void notifyJoinStateChanged() {
      this.friendJoinHandler.notifyJoinStateChanged();
   }

   public synchronized void shutdown() {
      this.shutdown = true;
      this.signaling.clearHandlers();
      this.rtcHandshakeHandler.shutdown();
      this.friendJoinHandler.shutdown();
      this.signaling.disconnect();
   }

   public boolean hasHandshake(final UUID peerPmid) {
      return this.rtcHandshakeHandler.hasHandshake(peerPmid);
   }

   public @Nullable RtcHandshake getHandshake(final UUID peerPmid) {
      return this.rtcHandshakeHandler.getHandshake(peerPmid);
   }

   public CompletableFuture<@Nullable Void> startHandshake(final UUID peerPmid, final String sessionId) {
      return this.rtcHandshakeHandler.startHandshake(peerPmid, sessionId);
   }

   public boolean consumeAcceptedJoinRequest(final UUID peerPmid, final String sessionId) {
      return this.friendJoinHandler.consumeAcceptedJoinRequest(peerPmid, sessionId);
   }
}
