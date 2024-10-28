package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundlePacket extends BundlePacket<ClientGamePacketListener> {
   public ClientboundBundlePacket(Iterable<Packet<? super ClientGamePacketListener>> var1) {
      super(var1);
   }

   public PacketType<ClientboundBundlePacket> type() {
      return GamePacketTypes.CLIENTBOUND_BUNDLE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBundlePacket(this);
   }
}
