package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundAcceptTeleportationPacket implements Packet<ServerGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_527;

   public ServerboundAcceptTeleportationPacket(int var1) {
      super();
      this.field_527 = var1;
   }

   public ServerboundAcceptTeleportationPacket(FriendlyByteBuf var1) {
      super();
      this.field_527 = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_527);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.field_527;
   }
}
