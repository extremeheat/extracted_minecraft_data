package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientGamePacketListener> {
   private final long id;

   public ClientboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   public ClientboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
