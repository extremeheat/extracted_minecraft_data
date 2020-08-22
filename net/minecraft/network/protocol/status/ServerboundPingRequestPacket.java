package net.minecraft.network.protocol.status;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPingRequestPacket implements Packet {
   private long time;

   public ServerboundPingRequestPacket() {
   }

   public ServerboundPingRequestPacket(long var1) {
      this.time = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.time = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeLong(this.time);
   }

   public void handle(ServerStatusPacketListener var1) {
      var1.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
