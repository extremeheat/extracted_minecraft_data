package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundKeepAlivePacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id long
   private final long field_266;

   public ClientboundKeepAlivePacket(long var1) {
      super();
      this.field_266 = var1;
   }

   public ClientboundKeepAlivePacket(FriendlyByteBuf var1) {
      super();
      this.field_266 = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.field_266);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleKeepAlive(this);
   }

   public long getId() {
      return this.field_266;
   }
}
