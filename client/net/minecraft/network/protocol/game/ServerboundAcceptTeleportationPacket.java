package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundAcceptTeleportationPacket implements Packet<ServerGamePacketListener> {
   private final int id;

   public ServerboundAcceptTeleportationPacket(int var1) {
      super();
      this.id = var1;
   }

   public ServerboundAcceptTeleportationPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
