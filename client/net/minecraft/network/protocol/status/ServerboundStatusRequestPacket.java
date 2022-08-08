package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundStatusRequestPacket implements Packet<ServerStatusPacketListener> {
   public ServerboundStatusRequestPacket() {
      super();
   }

   public ServerboundStatusRequestPacket(FriendlyByteBuf var1) {
      super();
   }

   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ServerStatusPacketListener var1) {
      var1.handleStatusRequest(this);
   }
}
