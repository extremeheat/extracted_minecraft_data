package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundlePacket extends BundlePacket<ClientGamePacketListener> {
   public ClientboundBundlePacket(final Iterable<Packet<? super ClientGamePacketListener>> packets) {
      super(packets);
   }

   public PacketType<ClientboundBundlePacket> type() {
      return GamePacketTypes.CLIENTBOUND_BUNDLE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleBundlePacket(this);
   }
}
