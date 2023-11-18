package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientCommonPacketListener> {
   private final long id;

   public ClientboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   public ClientboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
