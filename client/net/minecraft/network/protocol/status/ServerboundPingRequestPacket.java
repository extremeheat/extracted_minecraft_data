package net.minecraft.network.protocol.status;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPingRequestPacket implements Packet<ServerStatusPacketListener> {
   private final long time;

   public ServerboundPingRequestPacket(long var1) {
      super();
      this.time = var1;
   }

   public ServerboundPingRequestPacket(FriendlyByteBuf var1) {
      super();
      this.time = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.time);
   }

   public void handle(ServerStatusPacketListener var1) {
      var1.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
