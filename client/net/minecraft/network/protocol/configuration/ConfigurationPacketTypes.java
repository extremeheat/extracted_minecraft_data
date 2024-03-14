package net.minecraft.network.protocol.configuration;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public class ConfigurationPacketTypes {
   public static final PacketType<ClientboundFinishConfigurationPacket> CLIENTBOUND_FINISH_CONFIGURATION = createClientbound("finish_configuration");
   public static final PacketType<ClientboundRegistryDataPacket> CLIENTBOUND_REGISTRY_DATA = createClientbound("registry_data");
   public static final PacketType<ClientboundUpdateEnabledFeaturesPacket> CLIENTBOUND_UPDATE_ENABLED_FEATURES = createClientbound("update_enabled_features");
   public static final PacketType<ClientboundSelectKnownPacks> CLIENTBOUND_SELECT_KNOWN_PACKS = createClientbound("select_known_packs");
   public static final PacketType<ServerboundFinishConfigurationPacket> SERVERBOUND_FINISH_CONFIGURATION = createServerbound("finish_configuration");
   public static final PacketType<ServerboundSelectKnownPacks> SERVERBOUND_SELECT_KNOWN_PACKS = createServerbound("select_known_packs");

   public ConfigurationPacketTypes() {
      super();
   }

   private static <T extends Packet<ClientConfigurationPacketListener>> PacketType<T> createClientbound(String var0) {
      return new PacketType<>(PacketFlow.CLIENTBOUND, new ResourceLocation(var0));
   }

   private static <T extends Packet<ServerConfigurationPacketListener>> PacketType<T> createServerbound(String var0) {
      return new PacketType<>(PacketFlow.SERVERBOUND, new ResourceLocation(var0));
   }
}
