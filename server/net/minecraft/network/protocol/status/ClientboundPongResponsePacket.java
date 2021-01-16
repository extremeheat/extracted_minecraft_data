package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPongResponsePacket implements Packet<ClientStatusPacketListener> {
   private long time;

   public ClientboundPongResponsePacket() {
      super();
   }

   public ClientboundPongResponsePacket(long var1) {
      super();
      this.time = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.time = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeLong(this.time);
   }

   public void handle(ClientStatusPacketListener var1) {
      var1.handlePongResponse(this);
   }
}
