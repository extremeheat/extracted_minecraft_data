package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.config.JoinWorldTask;
import net.minecraft.server.network.config.ServerResourcePackConfigurationTask;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ServerConfigurationPacketListenerImpl extends ServerCommonPacketListenerImpl implements TickablePacketListener, ServerConfigurationPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component DISCONNECT_REASON_INVALID_DATA = Component.translatable("multiplayer.disconnect.invalid_player_data");
   private final GameProfile gameProfile;
   private final Queue<ConfigurationTask> configurationTasks = new ConcurrentLinkedQueue<>();
   @Nullable
   private ConfigurationTask currentTask;
   private ClientInformation clientInformation;

   public ServerConfigurationPacketListenerImpl(MinecraftServer var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
      this.gameProfile = var3.gameProfile();
      this.clientInformation = var3.clientInformation();
   }

   @Override
   protected GameProfile playerProfile() {
      return this.gameProfile;
   }

   @Override
   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.gameProfile, var1.getString());
      super.onDisconnect(var1);
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void startConfiguration() {
      this.send(new ClientboundCustomPayloadPacket(new BrandPayload(this.server.getServerModName())));
      LayeredRegistryAccess var1 = this.server.registries();
      this.send(new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(this.server.getWorldData().enabledFeatures())));
      this.send(new ClientboundRegistryDataPacket(new RegistryAccess.ImmutableRegistryAccess(RegistrySynchronization.networkedRegistries(var1)).freeze()));
      this.send(new ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(var1)));
      this.addOptionalTasks();
      this.configurationTasks.add(new JoinWorldTask());
      this.startNextTask();
   }

   public void returnToWorld() {
      this.configurationTasks.add(new JoinWorldTask());
      this.startNextTask();
   }

   private void addOptionalTasks() {
      this.server.getServerResourcePack().ifPresent(var1 -> this.configurationTasks.add(new ServerResourcePackConfigurationTask(var1)));
   }

   @Override
   public void handleClientInformation(ServerboundClientInformationPacket var1) {
      this.clientInformation = var1.information();
   }

   @Override
   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      super.handleResourcePackResponse(var1);
      if (var1.action().isTerminal()) {
         this.finishCurrentTask(ServerResourcePackConfigurationTask.TYPE);
      }
   }

   @Override
   public void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1) {
      this.connection.suspendInboundAfterProtocolChange();
      PacketUtils.ensureRunningOnSameThread(var1, this, this.server);
      this.finishCurrentTask(JoinWorldTask.TYPE);

      try {
         PlayerList var2 = this.server.getPlayerList();
         if (var2.getPlayer(this.gameProfile.getId()) != null) {
            this.disconnect(PlayerList.DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
            return;
         }

         Component var3 = var2.canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
         if (var3 != null) {
            this.disconnect(var3);
            return;
         }

         ServerPlayer var4 = var2.getPlayerForLogin(this.gameProfile, this.clientInformation);
         var2.placeNewPlayer(this.connection, var4, this.createCookie(this.clientInformation));
         this.connection.resumeInboundAfterProtocolChange();
      } catch (Exception var5) {
         LOGGER.error("Couldn't place player in world", var5);
         this.connection.send(new ClientboundDisconnectPacket(DISCONNECT_REASON_INVALID_DATA));
         this.connection.disconnect(DISCONNECT_REASON_INVALID_DATA);
      }
   }

   @Override
   public void tick() {
      this.keepConnectionAlive();
   }

   private void startNextTask() {
      if (this.currentTask != null) {
         throw new IllegalStateException("Task " + this.currentTask.type().id() + " has not finished yet");
      } else if (this.isAcceptingMessages()) {
         ConfigurationTask var1 = this.configurationTasks.poll();
         if (var1 != null) {
            this.currentTask = var1;
            var1.start(this::send);
         }
      }
   }

   private void finishCurrentTask(ConfigurationTask.Type var1) {
      ConfigurationTask.Type var2 = this.currentTask != null ? this.currentTask.type() : null;
      if (!var1.equals(var2)) {
         throw new IllegalStateException("Unexpected request for task finish, current task: " + var2 + ", requested: " + var1);
      } else {
         this.currentTask = null;
         this.startNextTask();
      }
   }
}
