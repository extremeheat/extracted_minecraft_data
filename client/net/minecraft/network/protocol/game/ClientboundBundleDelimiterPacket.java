package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundleDelimiterPacket;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundleDelimiterPacket extends BundleDelimiterPacket<ClientGamePacketListener> {
   public ClientboundBundleDelimiterPacket() {
      super();
   }

   public PacketType<ClientboundBundleDelimiterPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BUNDLE_DELIMITER;
   }
}
