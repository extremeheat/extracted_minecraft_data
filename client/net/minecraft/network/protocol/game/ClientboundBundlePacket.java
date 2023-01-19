package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;

public class ClientboundBundlePacket extends BundlePacket<ClientGamePacketListener> {
   public ClientboundBundlePacket(Iterable<Packet<ClientGamePacketListener>> var1) {
      super(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBundlePacket(this);
   }
}
