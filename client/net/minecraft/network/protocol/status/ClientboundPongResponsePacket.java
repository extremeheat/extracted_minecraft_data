package net.minecraft.network.protocol.status;

import net.minecraft.network.ClientPongPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPongResponsePacket implements Packet<ClientPongPacketListener> {
   private final long time;

   public ClientboundPongResponsePacket(long var1) {
      super();
      this.time = var1;
   }

   public ClientboundPongResponsePacket(FriendlyByteBuf var1) {
      super();
      this.time = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.time);
   }

   public void handle(ClientPongPacketListener var1) {
      var1.handlePongResponse(this);
   }

   public long getTime() {
      return this.time;
   }
}
