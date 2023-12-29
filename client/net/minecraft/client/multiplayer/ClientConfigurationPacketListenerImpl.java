package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

public class ClientConfigurationPacketListenerImpl extends ClientCommonPacketListenerImpl implements TickablePacketListener, ClientConfigurationPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile localGameProfile;
   private RegistryAccess.Frozen receivedRegistries;
   private FeatureFlagSet enabledFeatures;

   public ClientConfigurationPacketListenerImpl(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super(var1, var2, var3);
      this.localGameProfile = var3.localGameProfile();
      this.receivedRegistries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   @Override
   protected RegistryAccess.Frozen registryAccess() {
      return this.receivedRegistries;
   }

   @Override
   protected void handleCustomPayload(CustomPacketPayload var1) {
      this.handleUnknownCustomPayload(var1);
   }

   private void handleUnknownCustomPayload(CustomPacketPayload var1) {
      LOGGER.warn("Unknown custom packet payload: {}", var1.id());
   }

   @Override
   public void handleRegistryData(ClientboundRegistryDataPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      RegistryAccess.Frozen var2 = ClientRegistryLayer.createRegistryAccess().replaceFrom(ClientRegistryLayer.REMOTE, var1.registryHolder()).compositeAccess();
      if (!this.connection.isMemoryConnection()) {
         var2.registries().forEach(var0 -> var0.value().resetTags());
      }

      this.receivedRegistries = var2;
   }

   @Override
   public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket var1) {
      this.enabledFeatures = FeatureFlags.REGISTRY.fromNames(var1.features());
   }

   @Override
   public void handleConfigurationFinished(ClientboundFinishConfigurationPacket var1) {
      this.connection.suspendInboundAfterProtocolChange();
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.connection
         .setListener(
            new ClientPacketListener(
               this.minecraft,
               this.connection,
               new CommonListenerCookie(
                  this.localGameProfile,
                  this.telemetryManager,
                  this.receivedRegistries,
                  this.enabledFeatures,
                  this.serverBrand,
                  this.serverData,
                  this.postDisconnectScreen
               )
            )
         );
      this.connection.resumeInboundAfterProtocolChange();
      this.connection.send(new ServerboundFinishConfigurationPacket());
   }

   @Override
   public void tick() {
      this.sendDeferredPackets();
   }

   @Override
   public void onDisconnect(Component var1) {
      super.onDisconnect(var1);
      this.minecraft.clearDownloadedResourcePacks();
   }
}
