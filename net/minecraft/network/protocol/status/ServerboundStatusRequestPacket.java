package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundStatusRequestPacket implements Packet {
   public void read(FriendlyByteBuf var1) throws IOException {
   }

   public void write(FriendlyByteBuf var1) throws IOException {
   }

   public void handle(ServerStatusPacketListener var1) {
      var1.handleStatusRequest(this);
   }
}
