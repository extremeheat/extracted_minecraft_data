package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ClientConfigurationPacketListenerImpl extends ClientCommonPacketListenerImpl implements ClientConfigurationPacketListener, TickablePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile localGameProfile;
   private FeatureFlagSet enabledFeatures;
   private final RegistryAccess.Frozen receivedRegistries;
   private final RegistryDataCollector registryDataCollector = new RegistryDataCollector();
   @Nullable
   private KnownPacksManager knownPacks;
   @Nullable
   protected ChatComponent.State chatState;

   public ClientConfigurationPacketListenerImpl(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
      this.localGameProfile = var3.localGameProfile();
      this.receivedRegistries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
      this.chatState = var3.chatState();
   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   protected void handleCustomPayload(CustomPacketPayload var1) {
      this.handleUnknownCustomPayload(var1);
   }

   private void handleUnknownCustomPayload(CustomPacketPayload var1) {
      LOGGER.warn("Unknown custom packet payload: {}", var1.type().id());
   }

   public void handleRegistryData(ClientboundRegistryDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.registryDataCollector.appendContents(var1.registry(), var1.entries());
   }

   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.registryDataCollector.appendTags(var1.getTags());
   }

   public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket var1) {
      this.enabledFeatures = FeatureFlags.REGISTRY.fromNames(var1.features());
   }

   public void handleSelectKnownPacks(ClientboundSelectKnownPacks var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (this.knownPacks == null) {
         this.knownPacks = new KnownPacksManager();
      }

      List var2 = this.knownPacks.trySelectingPacks(var1.knownPacks());
      this.send(new ServerboundSelectKnownPacks(var2));
   }

   public void handleResetChat(ClientboundResetChatPacket var1) {
      this.chatState = null;
   }

   private <T> T runWithResources(Function<ResourceProvider, T> var1) {
      if (this.knownPacks == null) {
         return var1.apply(ResourceProvider.EMPTY);
      } else {
         CloseableResourceManager var2 = this.knownPacks.createResourceManager();

         Object var3;
         try {
            var3 = var1.apply(var2);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         return var3;
      }
   }

   public void handleConfigurationFinished(ClientboundFinishConfigurationPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      RegistryAccess.Frozen var2 = (RegistryAccess.Frozen)this.runWithResources((var1x) -> {
         return this.registryDataCollector.collectGameRegistries(var1x, this.receivedRegistries, this.connection.isMemoryConnection());
      });
      this.connection.setupInboundProtocol(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(var2)), new ClientPacketListener(this.minecraft, this.connection, new CommonListenerCookie(this.localGameProfile, this.telemetryManager, var2, this.enabledFeatures, this.serverBrand, this.serverData, this.postDisconnectScreen, this.serverCookies, this.chatState, this.strictErrorHandling, this.customReportDetails, this.serverLinks)));
      this.connection.send(ServerboundFinishConfigurationPacket.INSTANCE);
      this.connection.setupOutboundProtocol(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(var2)));
   }

   public void tick() {
      this.sendDeferredPackets();
   }

   public void onDisconnect(DisconnectionDetails var1) {
      super.onDisconnect(var1);
      this.minecraft.clearDownloadedResourcePacks();
   }
}
