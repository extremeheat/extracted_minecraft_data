package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundKeepAlivePacket implements Packet<ServerGamePacketListener> {
   private final long id;

   public ServerboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public ServerboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public long getId() {
      return this.id;
   }
}
