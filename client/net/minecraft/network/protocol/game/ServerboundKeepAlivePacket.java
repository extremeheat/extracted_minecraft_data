package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundKeepAlivePacket implements Packet<ServerGamePacketListener> {
   // $FF: renamed from: id long
   private final long field_311;

   public ServerboundKeepAlivePacket(long var1) {
      super();
      this.field_311 = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public ServerboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.field_311 = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.field_311);
   }

   public long getId() {
      return this.field_311;
   }
}
