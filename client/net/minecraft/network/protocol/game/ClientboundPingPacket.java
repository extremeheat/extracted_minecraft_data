package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPingPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_514;

   public ClientboundPingPacket(int var1) {
      super();
      this.field_514 = var1;
   }

   public ClientboundPingPacket(FriendlyByteBuf var1) {
      super();
      this.field_514 = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.field_514);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePing(this);
   }

   public int getId() {
      return this.field_514;
   }
}
