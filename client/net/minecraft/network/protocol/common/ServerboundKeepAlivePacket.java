package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundKeepAlivePacket implements Packet<ServerCommonPacketListener> {
   private final long id;

   public ServerboundKeepAlivePacket(long var1) {
      super();
      this.id = var1;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public ServerboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.id);
   }

   public long getId() {
      return this.id;
   }
}
