package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketProcessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ServerboundAcceptCodeOfConductPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.network.config.JoinWorldTask;
import net.minecraft.server.network.config.PrepareSpawnTask;
import net.minecraft.server.network.config.ServerCodeOfConductConfigurationTask;
import net.minecraft.server.network.config.ServerResourcePackConfigurationTask;
import net.minecraft.server.network.config.SynchronizeRegistriesTask;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ServerConfigurationPacketListenerImpl extends ServerCommonPacketListenerImpl implements ServerConfigurationPacketListener, TickablePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component DISCONNECT_REASON_INVALID_DATA = Component.translatable("multiplayer.disconnect.invalid_player_data");
   private static final Component DISCONNECT_REASON_CONFIGURATION_ERROR = Component.translatable("multiplayer.disconnect.configuration_error");
   private final GameProfile gameProfile;
   private final Queue<ConfigurationTask> configurationTasks = new ConcurrentLinkedQueue();
   @Nullable
   private ConfigurationTask currentTask;
   private ClientInformation clientInformation;
   @Nullable
   private SynchronizeRegistriesTask synchronizeRegistriesTask;
   @Nullable
   private PrepareSpawnTask prepareSpawnTask;

   public ServerConfigurationPacketListenerImpl(MinecraftServer var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
      this.gameProfile = var3.gameProfile();
      this.clientInformation = var3.clientInformation();
   }

   protected GameProfile playerProfile() {
      return this.gameProfile;
   }

   public void onDisconnect(DisconnectionDetails var1) {
      LOGGER.info("{} ({}) lost connection: {}", new Object[]{this.gameProfile.name(), this.gameProfile.id(), var1.reason().getString()});
      if (this.prepareSpawnTask != null) {
         this.prepareSpawnTask.close();
         this.prepareSpawnTask = null;
      }

      super.onDisconnect(var1);
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void startConfiguration() {
      this.send(new ClientboundCustomPayloadPacket(new BrandPayload(this.server.getServerModName())));
      ServerLinks var1 = this.server.serverLinks();
      if (!var1.isEmpty()) {
         this.send(new ClientboundServerLinksPacket(var1.untrust()));
      }

      LayeredRegistryAccess var2 = this.server.registries();
      List var3 = this.server.getResourceManager().listPacks().flatMap((var0) -> var0.location().knownPackInfo().stream()).toList();
      this.send(new ClientboundUpdateEnabledFeaturesPacket(FeatureFlags.REGISTRY.toNames(this.server.getWorldData().enabledFeatures())));
      this.synchronizeRegistriesTask = new SynchronizeRegistriesTask(var3, var2);
      this.configurationTasks.add(this.synchronizeRegistriesTask);
      this.addOptionalTasks();
      this.returnToWorld();
   }

   public void returnToWorld() {
      this.prepareSpawnTask = new PrepareSpawnTask(this.server, new NameAndId(this.gameProfile));
      this.configurationTasks.add(this.prepareSpawnTask);
      this.configurationTasks.add(new JoinWorldTask());
      this.startNextTask();
   }

   private void addOptionalTasks() {
      Map var1 = this.server.getCodeOfConducts();
      if (!var1.isEmpty()) {
         this.configurationTasks.add(new ServerCodeOfConductConfigurationTask(() -> {
            String var2 = (String)var1.get(this.clientInformation.language().toLowerCase(Locale.ROOT));
            if (var2 == null) {
               var2 = (String)var1.get("en_us");
            }

            if (var2 == null) {
               var2 = (String)var1.values().iterator().next();
            }

            return var2;
         }));
      }

      this.server.getServerResourcePack().ifPresent((var1x) -> this.configurationTasks.add(new ServerResourcePackConfigurationTask(var1x)));
   }

   public void handleClientInformation(ServerboundClientInformationPacket var1) {
      this.clientInformation = var1.information();
   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket var1) {
      super.handleResourcePackResponse(var1);
      if (var1.action().isTerminal()) {
         this.finishCurrentTask(ServerResourcePackConfigurationTask.TYPE);
      }

   }

   public void handleSelectKnownPacks(ServerboundSelectKnownPacks var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (PacketProcessor)this.server.packetProcessor());
      if (this.synchronizeRegistriesTask == null) {
         throw new IllegalStateException("Unexpected response from client: received pack selection, but no negotiation ongoing");
      } else {
         this.synchronizeRegistriesTask.handleResponse(var1.knownPacks(), this::send);
         this.finishCurrentTask(SynchronizeRegistriesTask.TYPE);
      }
   }

   public void handleAcceptCodeOfConduct(ServerboundAcceptCodeOfConductPacket var1) {
      this.finishCurrentTask(ServerCodeOfConductConfigurationTask.TYPE);
   }

   public void handleConfigurationFinished(ServerboundFinishConfigurationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (PacketProcessor)this.server.packetProcessor());
      this.finishCurrentTask(JoinWorldTask.TYPE);
      this.connection.setupOutboundProtocol(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess())));

      try {
         PlayerList var2 = this.server.getPlayerList();
         if (var2.getPlayer(this.gameProfile.id()) != null) {
            this.disconnect(PlayerList.DUPLICATE_LOGIN_DISCONNECT_MESSAGE);
            return;
         }

         Component var3 = var2.canPlayerLogin(this.connection.getRemoteAddress(), new NameAndId(this.gameProfile));
         if (var3 != null) {
            this.disconnect(var3);
            return;
         }

         ((PrepareSpawnTask)Objects.requireNonNull(this.prepareSpawnTask)).spawnPlayer(this.connection, this.createCookie(this.clientInformation));
      } catch (Exception var4) {
         LOGGER.error("Couldn't place player in world", var4);
         this.disconnect(DISCONNECT_REASON_INVALID_DATA);
      }

   }

   public void tick() {
      this.keepConnectionAlive();
      ConfigurationTask var1 = this.currentTask;
      if (var1 != null) {
         try {
            if (var1.tick()) {
               this.finishCurrentTask(var1.type());
            }
         } catch (Exception var3) {
            LOGGER.error("Failed to tick configuration task {}", var1.type(), var3);
            this.disconnect(DISCONNECT_REASON_CONFIGURATION_ERROR);
         }
      }

      if (this.prepareSpawnTask != null) {
         this.prepareSpawnTask.keepAlive();
      }

   }

   private void startNextTask() {
      if (this.currentTask != null) {
         throw new IllegalStateException("Task " + this.currentTask.type().id() + " has not finished yet");
      } else if (this.isAcceptingMessages()) {
         ConfigurationTask var1 = (ConfigurationTask)this.configurationTasks.poll();
         if (var1 != null) {
            this.currentTask = var1;

            try {
               var1.start(this::send);
            } catch (Exception var3) {
               LOGGER.error("Failed to start configuration task {}", var1.type(), var3);
               this.disconnect(DISCONNECT_REASON_CONFIGURATION_ERROR);
            }
         }

      }
   }

   private void finishCurrentTask(ConfigurationTask.Type var1) {
      ConfigurationTask.Type var2 = this.currentTask != null ? this.currentTask.type() : null;
      if (!var1.equals(var2)) {
         String var10002 = String.valueOf(var2);
         throw new IllegalStateException("Unexpected request for task finish, current task: " + var10002 + ", requested: " + String.valueOf(var1));
      } else {
         this.currentTask = null;
         this.startNextTask();
      }
   }
}
