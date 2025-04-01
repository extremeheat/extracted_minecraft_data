package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.profiling.Profiler;

public abstract class ServerLessCommonPacketListenerImpl implements ServerCommonPacketListener {
   public static final int LATENCY_CHECK_INTERVAL = 15000;
   private static final int CLOSED_LISTENER_TIMEOUT = 15000;
   private static final Component TIMEOUT_DISCONNECTION_MESSAGE = Component.translatable("disconnect.timeout");
   static final Component DISCONNECT_UNEXPECTED_QUERY = Component.translatable("multiplayer.disconnect.unexpected_query_response");
   protected final Connection connection;
   private final boolean transferred;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
   private long closedListenerTime;
   private boolean closed = false;
   private int latency;

   public ServerLessCommonPacketListenerImpl(Connection var1, CommonListenerCookie var2) {
      super();
      this.connection = var1;
      this.keepAliveTime = Util.getMillis();
      this.latency = var2.latency();
      this.transferred = var2.transferred();
   }

   private void close() {
      if (!this.closed) {
         this.closedListenerTime = Util.getMillis();
         this.closed = true;
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
   }

   public void handleCookieResponse(ServerboundCookieResponsePacket var1) {
      this.disconnect(DISCONNECT_UNEXPECTED_QUERY);
   }

   protected void keepConnectionAlive() {
      Profiler.get().push("keepAlive");
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

      Profiler.get().pop();
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

   public void send(Packet<?> var1) {
      this.send(var1, (PacketSendListener)null);
   }

   public void send(Packet<?> var1, @Nullable PacketSendListener var2) {
      if (var1.isTerminal()) {
         this.close();
      }

      boolean var3 = this.shouldFLush();

      try {
         this.connection.send(var1, var2, var3);
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.forThrowable(var7, "Sending packet");
         CrashReportCategory var6 = var5.addCategory("Packet being sent");
         var6.setDetail("Packet class", (CrashReportDetail)(() -> var1.getClass().getCanonicalName()));
         throw new ReportedException(var5);
      }
   }

   protected abstract boolean shouldFLush();

   public void disconnect(Component var1) {
      this.disconnect(new DisconnectionDetails(var1));
   }

   public abstract void disconnect(DisconnectionDetails var1);

   protected abstract boolean isSingleplayerOwner();

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
