package net.minecraft.network.protocol.configuration;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> features) implements Packet<ClientConfigurationPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateEnabledFeaturesPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundUpdateEnabledFeaturesPacket>codec(ClientboundUpdateEnabledFeaturesPacket::write, ClientboundUpdateEnabledFeaturesPacket::new);

   private ClientboundUpdateEnabledFeaturesPacket(FriendlyByteBuf var1) {
      this((Set)var1.readCollection(HashSet::new, FriendlyByteBuf::readResourceLocation));
   }

   public ClientboundUpdateEnabledFeaturesPacket(Set<ResourceLocation> var1) {
      super();
      this.features = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.features, FriendlyByteBuf::writeResourceLocation);
   }

   public PacketType<ClientboundUpdateEnabledFeaturesPacket> type() {
      return ConfigurationPacketTypes.CLIENTBOUND_UPDATE_ENABLED_FEATURES;
   }

   public void handle(ClientConfigurationPacketListener var1) {
      var1.handleEnabledFeatures(this);
   }
}
