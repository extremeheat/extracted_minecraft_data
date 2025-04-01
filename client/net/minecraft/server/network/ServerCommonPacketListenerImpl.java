package net.minecraft.server.network;

import java.util.Objects;
import net.minecraft.ReportedException;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.TheGame;
import net.minecraft.util.thread.BlockableEventLoop;

public abstract class ServerCommonPacketListenerImpl extends ServerLessCommonPacketListenerImpl {
   protected final TheGame theGame;
   private volatile boolean suspendFlushingOnServerThread = false;

   public ServerCommonPacketListenerImpl(TheGame var1, Connection var2, CommonListenerCookie var3) {
      super(var2, var3);
      this.theGame = var1;
   }

   public void onDisconnect(DisconnectionDetails var1) {
      if (this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.theGame.server().halt(false);
      }

   }

   public void onPacketError(Packet var1, Exception var2) throws ReportedException {
      super.onPacketError(var1, var2);
      this.theGame.server().reportPacketHandlingException(var2, var1.type());
   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.theGame.eventLoop());
      if (var1.action() == ServerboundResourcePackPacket.Action.DECLINED && this.theGame.server().isResourcePackRequired()) {
         LOGGER.info("Disconnecting {} due to resource pack {} rejection", this.playerProfile().getName(), var1.id());
         this.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
      }

   }

   public void suspendFlushing() {
      this.suspendFlushingOnServerThread = true;
   }

   public void resumeFlushing() {
      this.suspendFlushingOnServerThread = false;
      this.connection.flushChannel();
   }

   public void disconnect(DisconnectionDetails var1) {
      this.connection.send(new ClientboundDisconnectPacket(var1.reason()), PacketSendListener.thenRun(() -> this.connection.disconnect(var1)));
      this.connection.setReadOnly();
      BlockableEventLoop var10000 = this.theGame.eventLoop();
      Connection var10001 = this.connection;
      Objects.requireNonNull(var10001);
      var10000.executeBlocking(var10001::handleDisconnection);
   }

   protected boolean isSingleplayerOwner() {
      return this.theGame.server().isSingleplayerOwner(this.playerProfile());
   }

   protected boolean shouldFLush() {
      return !this.suspendFlushingOnServerThread || !this.theGame.eventLoop().isSameThread();
   }
}
