package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPingPacket implements Packet<ClientCommonPacketListener> {
   private final int id;

   public ClientboundPingPacket(int var1) {
      super();
      this.id = var1;
   }

   public ClientboundPingPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.id);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handlePing(this);
   }

   public int getId() {
      return this.id;
   }
}
