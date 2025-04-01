package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.ClientInformation;
import org.slf4j.Logger;

public class ServerHibernateConfigPacketListenerImpl extends ServerLessCommonPacketListenerImpl implements ServerConfigurationPacketListener {
   private final Executor mainThreadExecutor;
   private final GameProfile gameProfile;
   private static final Logger LOGGER = LogUtils.getLogger();
   private ClientInformation clientInformation;

   public ServerHibernateConfigPacketListenerImpl(Connection var1, CommonListenerCookie var2, Executor var3) {
      super(var1, var2);
      this.mainThreadExecutor = var3;
      this.gameProfile = var2.gameProfile();
      this.clientInformation = var2.clientInformation();
   }

   public void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1) {
      LOGGER.warn("Unexpected packet {}", var1.type());
   }

   public void handleSelectKnownPacks(ServerboundSelectKnownPacks var1) {
   }

   protected boolean shouldFLush() {
      return true;
   }

   public void disconnect(DisconnectionDetails var1) {
      this.connection.send(new ClientboundDisconnectPacket(var1.reason()), PacketSendListener.thenRun(() -> this.connection.disconnect(var1)));
      this.connection.setReadOnly();
      Connection var10000 = this.connection;
      Objects.requireNonNull(var10000);
      CompletableFuture.runAsync(var10000::handleDisconnection, this.mainThreadExecutor).join();
   }

   protected boolean isSingleplayerOwner() {
      return false;
   }

   protected GameProfile playerProfile() {
      return this.gameProfile;
   }

   public void handleClientInformation(ServerboundClientInformationPacket var1) {
      this.clientInformation = var1.information();
   }

   public void onDisconnect(DisconnectionDetails var1) {
      LOGGER.info("{} lost connection: {}", this.gameProfile, var1.reason().getString());
   }

   public boolean isAcceptingMessages() {
      return false;
   }

   public ServerConfigurationPacketListenerImpl unfreeze(TheGame var1) {
      return new ServerConfigurationPacketListenerImpl(var1, this.connection, this.createCookie(this.clientInformation));
   }
}
