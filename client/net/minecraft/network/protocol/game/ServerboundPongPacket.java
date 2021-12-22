package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPongPacket implements Packet<ServerGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_268;

   public ServerboundPongPacket(int var1) {
      super();
      this.field_268 = var1;
   }

   public ServerboundPongPacket(FriendlyByteBuf var1) {
      super();
      this.field_268 = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.field_268);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePong(this);
   }

   public int getId() {
      return this.field_268;
   }
}
