package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPongPacket implements Packet<ServerCommonPacketListener> {
   private final int id;

   public ServerboundPongPacket(int var1) {
      super();
      this.id = var1;
   }

   public ServerboundPongPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.id);
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handlePong(this);
   }

   public int getId() {
      return this.id;
   }
}
