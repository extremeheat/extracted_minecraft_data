package net.minecraft.network;

import net.minecraft.network.protocol.PacketFlow;

public interface ClientboundPacketListener extends PacketListener {
   default PacketFlow flow() {
      return PacketFlow.CLIENTBOUND;
   }
}
