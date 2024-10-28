package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;

public abstract class ServerCommonPacketListenerImpl implements ServerCommonPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int LATENCY_CHECK_INTERVAL = 15000;
   private static final int CLOSED_LISTENER_TIMEOUT = 15000;
   private static final Component TIMEOUT_DISCONNECTION_MESSAGE = Component.translatable("disconnect.timeout");
   static final Component DISCONNECT_UNEXPECTED_QUERY = Component.translatable("multiplayer.disconnect.unexpected_query_response");
   protected final MinecraftServer server;
   protected final Connection connection;
   private final boolean transferred;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
   private long closedListenerTime;
   private boolean closed = false;
   private int latency;
   private volatile boolean suspendFlushingOnServerThread = false;

   public ServerCommonPacketListenerImpl(MinecraftServer var1, Connection var2, CommonListenerCookie var3) {
      super();
      this.server = var1;
      this.connection = var2;
      this.keepAliveTime = Util.getMillis();
      this.latency = var3.latency();
      this.transferred = var3.transferred();
   }

   private void close() {
      if (!this.closed) {
         this.closedListenerTime = Util.getMillis();
         this.closed = true;
      }

   }

   public void onDisconnect(DisconnectionDetails var1) {
      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }

   }

   public void handleKeepAlive(ServerboundKeepAlivePacket var1) {
      if (this.keepAlivePending && var1.getId() == this.keepAliveChallenge) {
         int var2 = (int)(Util.getMillis() - this.keepAliveTime);
         this.latency = (this.latency * 3 + var2) / 4;
         this.keepAlivePending = false;
      } else if (!this.isSingleplayerOwner()) {
         this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
      }

   }

   public void handlePong(ServerboundPongPacket var1) {
   }

   public void handleCustomPayload(ServerboundCustomPayloadPacket var1) {
   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.server);
      if (var1.action() == ServerboundResourcePackPacket.Action.DECLINED && this.server.isResourcePackRequired()) {
         LOGGER.info("Disconnecting {} due to resource pack {} rejection", this.playerProfile().getName(), var1.id());
         this.disconnect((Component)Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
      }

   }

   public void handleCookieResponse(ServerboundCookieResponsePacket var1) {
      this.disconnect(DISCONNECT_UNEXPECTED_QUERY);
   }

   protected void keepConnectionAlive() {
      this.server.getProfiler().push("keepAlive");
      long var1 = Util.getMillis();
      if (!this.isSingleplayerOwner() && var1 - this.keepAliveTime >= 15000L) {
         if (this.keepAlivePending) {
            this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
         } else if (this.checkIfClosed(var1)) {
            this.keepAlivePending = true;
            this.keepAliveTime = var1;
            this.keepAliveChallenge = var1;
            this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
         }
      }

      this.server.getProfiler().pop();
   }

   private boolean checkIfClosed(long var1) {
      if (this.closed) {
         if (var1 - this.closedListenerTime >= 15000L) {
            this.disconnect(TIMEOUT_DISCONNECTION_MESSAGE);
         }

         return false;
      } else {
         return true;
      }
   }

   public void suspendFlushing() {
      this.suspendFlushingOnServerThread = true;
   }

   public void resumeFlushing() {
      this.suspendFlushingOnServerThread = false;
      this.connection.flushChannel();
   }

   public void send(Packet<?> var1) {
      this.send(var1, (PacketSendListener)null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      if (var1.isTerminal()) {
         this.close();
      }

      boolean var3 = !this.suspendFlushingOnServerThread || !this.server.isSameThread();

      try {
         this.connection.send(var1, var2, var3);
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Sending packet");
         CrashReportCategory var6 = var5.addCategory("Packet being sent");
         var6.setDetail("Packet class", () -> {
            return var1.getClass().getCanonicalName();
         });
         throw new ReportedException(var5);
      }
   }

   public void disconnect(Component var1) {
      this.disconnect(new DisconnectionDetails(var1));
   }

   public void disconnect(DisconnectionDetails var1) {
      this.connection.send(new ClientboundDisconnectPacket(var1.reason()), PacketSendListener.thenRun(() -> {
         this.connection.disconnect(var1);
      }));
      this.connection.setReadOnly();
      MinecraftServer var10000 = this.server;
      Connection var10001 = this.connection;
      Objects.requireNonNull(var10001);
      var10000.executeBlocking(var10001::handleDisconnection);
   }

   protected boolean isSingleplayerOwner() {
      return this.server.isSingleplayerOwner(this.playerProfile());
   }

   protected abstract GameProfile playerProfile();

   @VisibleForDebug
   public GameProfile getOwner() {
      return this.playerProfile();
   }

   public int latency() {
      return this.latency;
   }

   protected CommonListenerCookie createCookie(ClientInformation var1) {
      return new CommonListenerCookie(this.playerProfile(), this.latency, var1, this.transferred);
   }
}
